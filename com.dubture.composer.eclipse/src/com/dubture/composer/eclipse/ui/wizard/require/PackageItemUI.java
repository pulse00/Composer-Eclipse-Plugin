package com.dubture.composer.eclipse.ui.wizard.require;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.getcomposer.core.PHPPackage;
import org.getcomposer.core.PackageInterface;
import org.pdtextensions.ui.wizards.iteminstaller.AbstractDescriptorItemUi;
import org.pdtextensions.ui.wizards.iteminstaller.ItemInstaller;

import com.dubture.composer.eclipse.model.EclipsePHPPackage;

/**
 * 
 * Represents a row in the "Add dependency" Wizards second page.
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class PackageItemUI extends AbstractDescriptorItemUi
{
    private Label nameLabel;
    
    private EclipsePHPPackage phpPackage;
    
    private PackageInterface composerPackage;
    
    private Combo versionDropdown;
    
    private VersionChangeListener listener;
    
    
    public PackageItemUI(ItemInstaller installer, EclipsePHPPackage item, Composite parent, Color color)
    {
        super(installer, item, parent, color);
        phpPackage = item;
        composerPackage = item.getPhpPackage();
        
        doCreateBody(parent);
    }
    
    protected void createBody(Composite parent) {

    }
    
    @SuppressWarnings("rawtypes")
    protected void doCreateBody(Composite parent)
    {
        itemContainer = new Composite(parent, SWT.NULL);

        installer.configureLook(getItemContainer(), background);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(itemContainer);
        GridLayout layout = new GridLayout(4, false);
        layout.marginLeft = 7;
        layout.marginTop = 2;
        layout.marginBottom = 2;
        itemContainer.setLayout(layout);

        nameLabel = new Label(itemContainer, SWT.NULL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(nameLabel);
        nameLabel.setFont(installer.getH1Font());
        nameLabel.setText(composerPackage.getName());
        
        Map<String, PHPPackage> versions = composerPackage.getVersions();
        
        Iterator it = versions.keySet().iterator();
        
        versionDropdown = new Combo(itemContainer, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        
        while (it.hasNext()) {
            String versionName = (String) it.next();
            versionDropdown.add(versionName);
        }
        
        versionDropdown.select(0);
        versionDropdown.addSelectionListener(new VersionListener());
    }
    
    private class VersionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent e)
        {
            String version = versionDropdown.getItem(versionDropdown.getSelectionIndex());
            listener.versionChanged(composerPackage, version);
        }
    }

    public void setVersionChangeListener(VersionChangeListener listener)
    {
        this.listener = listener;
    }
}
