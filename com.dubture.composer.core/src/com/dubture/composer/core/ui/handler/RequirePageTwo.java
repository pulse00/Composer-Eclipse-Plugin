package com.dubture.composer.core.ui.handler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.pex.core.log.Logger;
import org.pex.core.model.InstallableItem;
import org.pex.ui.wizards.iteminstaller.AbstractDescriptorItemUi;
import org.pex.ui.wizards.iteminstaller.AbstractItemInstallerPage;
import org.pex.ui.wizards.iteminstaller.ItemInstaller;

import com.dubture.composer.core.model.PHPPackage;
import com.dubture.composer.core.packagist.PackageDownloader;

public class RequirePageTwo extends AbstractItemInstallerPage implements IPageChangedListener
{
    private RequirePageOne pageOne;

    private List<PHPPackage> packages;

    protected RequirePageTwo(RequirePageOne pageOne)
    {
        super("Select the versions from your packages");
        setDescription("Choose the versions to install for the selected packages");
        this.pageOne = pageOne;
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
            System.err.println("no change");
        }
    }

    protected void createHeader(Composite container)
    {

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
                    List<PHPPackage> rawPackages = (List<PHPPackage>) RequirePageTwo.this.pageOne
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
                    System.err.println("ITEMS: " + items.size());
                    
                    Display.getDefault().asyncExec(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            try {

                                if (categoryChildrenContainer.isDisposed()) {
                                    return;
                                }

                                createBodyContents();
                                // createPackages(categoryChildrenContainer);

                            } catch (Exception e) {
                                Logger.logException(e);
                            }
                        }
                    });

                }
            });
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
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

    public class PackageItemUI extends AbstractDescriptorItemUi
    {

        private Composite checkboxContainer;
        private Label nameLabel;

        public PackageItemUI(ItemInstaller installer, PHPPackage item,
                Composite parent)
        {
            super(installer, item, parent, colorCategoryGradientEnd);

            connectorContainer = new Composite(parent, SWT.NULL);

            GridDataFactory.fillDefaults().grab(true, false)
                    .applyTo(getConnectorContainer());
            GridLayout layout = new GridLayout(4, false);
            layout.marginLeft = 7;
            layout.marginTop = 2;
            layout.marginBottom = 2;
            getConnectorContainer().setLayout(layout);

            checkboxContainer = new Composite(getConnectorContainer(), SWT.NULL);
            GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING)
                    .span(1, 2).applyTo(checkboxContainer);
            GridLayoutFactory.fillDefaults().spacing(1, 1).numColumns(2)
                    .applyTo(checkboxContainer);

            nameLabel = new Label(getConnectorContainer(), SWT.NULL);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(nameLabel);
            nameLabel.setFont(h2Font);
            nameLabel.setText(item.getName());

        }
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
