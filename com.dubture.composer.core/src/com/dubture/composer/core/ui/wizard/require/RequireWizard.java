package com.dubture.composer.core.ui.wizard.require;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.wizard.Wizard;

import com.dubture.composer.core.ComposerPluginImages;

public class RequireWizard extends Wizard
{
    private RequirePageOne firstPage;
    private RequirePageTwo secondPage;
    private IResource composer;
    
    public RequireWizard(IResource composer)
    {
        setDefaultPageImageDescriptor(ComposerPluginImages.DESC_WIZBAN_ADD_DEPENDENCY);
        setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
        setWindowTitle("Search packagist.org");
        
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
