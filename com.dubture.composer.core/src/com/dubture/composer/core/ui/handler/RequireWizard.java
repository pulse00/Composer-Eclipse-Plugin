package com.dubture.composer.core.ui.handler;

import org.eclipse.jface.wizard.Wizard;

public class RequireWizard extends Wizard
{
    private RequirePageOne firstPage;
    
    @Override
    public String getWindowTitle()
    {
        return "Add composer dependencies";
    }

    public void addPages() {
        firstPage = new RequirePageOne();
        addPage(firstPage);
    }
    
    
    @Override
    public boolean performFinish()
    {
        return firstPage.doFinish();
    }
}
