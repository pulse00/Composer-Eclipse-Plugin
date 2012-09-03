package com.dubture.composer.core.ui.handler;

import org.eclipse.jface.wizard.Wizard;

public class RequireWizard extends Wizard
{
    private RequirePageOne one;

    public void addPages() {
        one = new RequirePageOne("Page one");
        addPage(one);
    }
    
    @Override
    public boolean performFinish()
    {
        return true;
    }
}
