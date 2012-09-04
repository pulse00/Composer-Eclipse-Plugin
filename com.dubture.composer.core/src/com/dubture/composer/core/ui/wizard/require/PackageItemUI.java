package com.dubture.composer.core.ui.wizard.require;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.pex.ui.wizards.iteminstaller.AbstractDescriptorItemUi;
import org.pex.ui.wizards.iteminstaller.ItemInstaller;

import com.dubture.composer.core.model.PHPPackage;

public class PackageItemUI extends AbstractDescriptorItemUi
{
    
    private Composite checkboxContainer;
    private Label nameLabel;

    public PackageItemUI(ItemInstaller installer, PHPPackage item, Composite parent, Color color, Font h1Font, Font h2Font)
    {
        super(installer, item, parent, color);

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
