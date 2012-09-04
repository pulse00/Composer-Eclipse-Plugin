package com.dubture.composer.core.ui.wizard.require;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.wizard.Wizard;

public class RequireWizard extends Wizard
{
    private RequirePageOne firstPage;
    private RequirePageTwo secondPage;
    private IResource composer;
    
    public RequireWizard(IResource composer)
    {
        this.setComposer(composer);
    }

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
        return secondPage.doFinish();
    }
    
    @Override
    public boolean needsProgressMonitor()
    {
        return true;
    }

    public IResource getComposer()
    {
        return composer;
    }

    public void setComposer(IResource composer)
    {
        this.composer = composer;
    }
}
