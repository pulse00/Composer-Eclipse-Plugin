package com.dubture.composer.core.ui.wizard.require;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.pex.core.log.Logger;
import org.pex.core.model.InstallableItem;
import org.pex.ui.wizards.iteminstaller.AbstractDescriptorItemUi;
import org.pex.ui.wizards.iteminstaller.AbstractItemInstallerPage;

import com.dubture.composer.core.model.PHPPackage;
import com.dubture.composer.core.packagist.PackageDownloader;

public class RequirePageTwo extends AbstractItemInstallerPage implements IPageChangedListener
{
    private RequirePageOne firstPage;

    private List<PHPPackage> packages;

    protected RequirePageTwo(RequirePageOne pageOne)
    {
        super("Select the versions from your packages");
        
        setDescription("Choose the versions to install for the selected packages");
        this.firstPage = pageOne;
        packages = new ArrayList<PHPPackage>();
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
                            PHPPackage phpPackage = downloader
                                    .getPackage(new NullProgressMonitor());
                            packages.add(phpPackage);
                        } catch (IOException e) {
                            Logger.logException(e);
                        }

                        monitor.worked(++i);
                    }

                    monitor.done();
                    items = packages;
                    
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
        return new AbstractDescriptorItemUi(this, item, container, background);
    }

    @Override
    public boolean doFinish()
    {
        return false;
    }

    @Override
    public boolean modifySelection(AbstractDescriptorItemUi item,
            boolean selected)
    {
        return true;
    }

    @Override
    public Button getItemButton(Composite checkboxContainer)
    {
        return new Button(checkboxContainer, SWT.CHECK);
    }

    @Override
    protected void createRefreshJob()
    {
        // refresh happens in pageChanged()
    }
}
