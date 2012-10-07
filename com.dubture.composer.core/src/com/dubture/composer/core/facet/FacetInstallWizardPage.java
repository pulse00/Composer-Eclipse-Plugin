package com.dubture.composer.core.facet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;

public class FacetInstallWizardPage extends AbstractFacetWizardPage
{
    public FacetInstallWizardPage()
    {
        super("Configure composer");
        
        setTitle("Composer configuration");
        setDescription("Configure your composer project");
    }

    @Override
    public void setConfig(Object config)
    {
        
    }

    @Override
    public void createControl(Composite parent)
    {
        Composite container = new Composite(parent, SWT.NONE);
        Label label = new Label(container, SWT.NONE);
        label.setText("Composer test");
        
        setControl(container);
    }
}
