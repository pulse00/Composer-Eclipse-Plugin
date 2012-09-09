package com.dubture.composer.eclipse.ui.wizard.require;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.getcomposer.core.PHPPackage;
import org.getcomposer.core.PackageInterface;
import org.getcomposer.core.packagist.PackageDownloader;

import com.dubture.composer.eclipse.launch.ConsoleResponseHandler;
import com.dubture.composer.eclipse.launch.DefaultExecutableLauncher;
import com.dubture.composer.eclipse.log.Logger;
import com.dubture.composer.eclipse.model.EclipsePHPPackage;
import com.dubture.composer.eclipse.model.InstallableItem;
import com.dubture.composer.eclipse.ui.wizard.iteminstaller.AbstractDescriptorItemUi;
import com.dubture.composer.eclipse.ui.wizard.iteminstaller.AbstractItemInstallerPage;

public class RequirePageTwo extends AbstractItemInstallerPage implements IPageChangedListener, VersionChangeListener
{
    private RequirePageOne firstPage;

    private Map<EclipsePHPPackage, String> packages;

    protected RequirePageTwo(RequirePageOne pageOne)
    {
        super("");
        
        setTitle("Select package versions");
        setDescription("Choose the versions to install for the selected packages");
        this.firstPage = pageOne;
        setPackages(new HashMap<EclipsePHPPackage, String>());
    }

    @Override
    public void createControl(Composite parent)
    {
        super.createControl(parent);

        IWizardContainer wizardContainer = getWizard().getContainer();

        if (wizardContainer instanceof IPageChangeProvider) {
            ((IPageChangeProvider) wizardContainer)
                    .addPageChangedListener(this);
        } else {
            Logger.debug("Unable to retrieve IPageChangeProvider");
        }
    }

    protected void createHeader(Composite container)
    {
        // no header needed
    }
    
    protected void loadPackages()
    {
        try {
            getContainer().run(true, true, new IRunnableWithProgress()
            {
                @Override
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException
                {
                    
                    items.clear();
                    getPackages().clear();
                    List<InstallableItem> rawPackages = RequirePageTwo.this.firstPage
                            .getSelectedItems();
                    monitor.beginTask(
                            "Retrieving package info from packagist.org...",
                            rawPackages.size());
                    int i = 0;

                    for (InstallableItem item : rawPackages) {
                        try {
                            PackageDownloader downloader = new PackageDownloader(item.getUrl());
                            PackageInterface phpPackage = downloader.getPackage();
                            packages.put(new EclipsePHPPackage(phpPackage), phpPackage.getDefaultVersion());
                        } catch (IOException e) {
                            Logger.logException(e);
                        }

                        monitor.worked(++i);
                    }

                    monitor.done();
                    items = new ArrayList<InstallableItem>(getPackages().keySet());
                    
                    Display.getDefault().asyncExec(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            createBodyContents();
                        }
                    });
                }
            });
        } catch (Exception e) {
            Logger.logException(e);
        }
    }

    @Override
    public void pageChanged(PageChangedEvent event)
    {
        if (event.getSelectedPage() == this) {
            loadPackages();
        }
    }

    protected AbstractDescriptorItemUi getItemUI(InstallableItem item,
            Composite container, Color background)
    {
        PackageItemUI ui = new PackageItemUI(this, (EclipsePHPPackage) item, container, background);
        ui.setVersionChangeListener(this);
        return ui;
    }

    @Override
    public boolean doFinish()
    {
        try {
            
            RequireWizard parentWizard = (RequireWizard) getWizard();
            final IResource composer = parentWizard.getComposer();
            
            getContainer().run(true, true, new IRunnableWithProgress()
            {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException,
                        InterruptedException
                {
                    int count = getPackages().size();
                    int current = 0;
                    
                    Iterator it = getPackages().keySet().iterator();
                    
                    monitor.beginTask("Installing composer dependencies...", count);
                    
                    while(it.hasNext()) {
                        PHPPackage composerPackage = (PHPPackage) it.next();
                        String version = getPackages().get(composerPackage);
                        
                        try {
                            String dependency = composerPackage.getPackageName(version);
                            
                            DefaultExecutableLauncher launcher = new DefaultExecutableLauncher();
                            String[] arg = new String[]{"require", dependency};
                            launcher.launch(composer.getLocation().toOSString(), arg, new ConsoleResponseHandler());

                            monitor.worked(++current);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    monitor.done();
                }
            });
        } catch (Exception e) {
            Logger.logException(e);
        }
        
        return true;
    }

    @Override
    public boolean modifySelection(AbstractDescriptorItemUi item,
            boolean selected)
    {
        return true;
    }

    @Override
    protected void createRefreshJob()
    {
        // refresh happens in pageChanged()
    }

    @Override
    public Button getItemButton(Composite checkboxContainer)
    {
        return null;
    }

    @Override
    public void versionChanged(PackageInterface packageName, String versionName)
    {
        packages.put(new EclipsePHPPackage(packageName), versionName);
    }

    public Map<EclipsePHPPackage, String> getPackages()
    {
        return packages;
    }

    public void setPackages(Map<EclipsePHPPackage, String> packages)
    {
        this.packages = packages;
    }
}
