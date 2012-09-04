package com.dubture.composer.core.ui.handler;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class RequirePageTwo extends WizardPage
{
    private RequirePageOne pageOne;

    protected RequirePageTwo(RequirePageOne pageOne)
    {
        super("Select the versions from your packages");
        this.pageOne = pageOne;
    }

    @Override
    public void createControl(Composite parent)
    {
        initializeDialogUnits(parent);
        final Composite container = new Composite(parent, SWT.NULL);
        
        container.setLayout(new GridLayout(1, false));
        
        final Composite header = new Composite(container, SWT.NULL);
        GridLayoutFactory.fillDefaults().applyTo(header);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(header);
        
        setControl(container);
    }
}
