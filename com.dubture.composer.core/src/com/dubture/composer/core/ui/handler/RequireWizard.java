package com.dubture.composer.core.ui.handler;

import org.eclipse.jface.wizard.Wizard;

public class RequireWizard extends Wizard
{
    private RequirePageOne firstPage;
    private RequirePageTwo secondPage;
    
    @Override
    public String getWindowTitle()
    {
        return "Add composer dependencies";
    }

    public void addPages() {
        
        firstPage = new RequirePageOne();
        addPage(firstPage);
        
        secondPage = new RequirePageTwo(firstPage);
        addPage(secondPage);
    }
    
    
    @Override
    public boolean performFinish()
    {
        return firstPage.doFinish();
    }
}
