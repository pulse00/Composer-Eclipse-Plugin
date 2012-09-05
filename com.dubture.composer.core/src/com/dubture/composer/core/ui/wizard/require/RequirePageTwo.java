package com.dubture.composer.core.ui.wizard.require;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.pex.core.launch.ConsoleResponseHandler;
import org.pex.core.launch.DefaultExecutableLauncher;
import org.pex.core.log.Logger;
import org.pex.core.model.InstallableItem;
import org.pex.ui.wizards.iteminstaller.AbstractDescriptorItemUi;
import org.pex.ui.wizards.iteminstaller.AbstractItemInstallerPage;

import com.dubture.composer.core.model.PHPPackage;
import com.dubture.composer.core.packagist.PackageDownloader;

public class RequirePageTwo extends AbstractItemInstallerPage implements IPageChangedListener, VersionChangeListener
{
    private RequirePageOne firstPage;

    private Map<PHPPackage, String> packages;

    protected RequirePageTwo(RequirePageOne pageOne)
    {
        super("");
        
        setTitle("Select versions");
        setDescription("Choose the versions to install for the selected packages");
        this.firstPage = pageOne;
        packages = new HashMap<PHPPackage, String>();
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
                @SuppressWarnings("unchecked")
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException
                {
                    
                    items.clear();
                    packages.clear();
                    List<PHPPackage> rawPackages = (List<PHPPackage>) RequirePageTwo.this.firstPage
                            .getSelectedItems();
                    monitor.beginTask(
                            "Retrieving package info from packagist.org...",
                            rawPackages.size());
                    int i = 0;

                    for (PHPPackage item : rawPackages) {
                        try {
                            PackageDownloader downloader = new PackageDownloader(
                                    item.getUrl());
                            PHPPackage phpPackage = downloader.getPackage(new NullProgressMonitor());
                            packages.put(phpPackage, phpPackage.getDefaultVersion());
                        } catch (IOException e) {
                            Logger.logException(e);
                        }

                        monitor.worked(++i);
                    }

                    monitor.done();
                    items = new ArrayList<InstallableItem>(packages.keySet());
                    
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
        PackageItemUI ui = new PackageItemUI(this, (PHPPackage) item, container, background);
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
                    int count = packages.size();
                    int current = 0;
                    
                    Iterator it = packages.keySet().iterator();
                    
                    monitor.beginTask("Installing composer dependencies...", count);
                    
                    while(it.hasNext()) {
                        PHPPackage composerPackage = (PHPPackage) it.next();
                        String version = packages.get(composerPackage);
                        
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
    public void versionChanged(PHPPackage packageName, String versionName)
    {
        packages.put(packageName, versionName);
    }
}
