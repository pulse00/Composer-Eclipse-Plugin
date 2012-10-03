package com.dubture.composer.core.ui.wizard.require;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.statushandlers.StatusManager;

import com.dubture.composer.core.ComposerPluginImages;
import com.dubture.composer.core.launch.ConsoleResponseHandler;
import com.dubture.composer.core.launch.DefaultExecutableLauncher;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.model.EclipsePHPPackage;

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

    public void addPages()
    {

        firstPage = new RequirePageOne();
        addPage(firstPage);

        secondPage = new RequirePageTwo(firstPage);
        addPage(secondPage);
    }

    @Override
    public boolean performFinish()
    {
        final IResource composer = getComposer();

        FinishJob job = new FinishJob("Adding composer dependencies...", composer);
        job.setUser(true);
        job.schedule();

        return true;
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
    
    private class FinishJob extends Job {
        
        private IResource composer;
        private DefaultExecutableLauncher launcher;

        public FinishJob(String name, IResource composer)
        {
            super(name);
            this.composer = composer;
        }
        
        @Override
        protected void canceling()
        {
            super.canceling();
            
            if (launcher != null) {
                launcher.abort();
            }
        }

        @Override
        @SuppressWarnings("rawtypes")
        protected IStatus run(IProgressMonitor monitor)
        {
            if (composer == null) {
                return Status.CANCEL_STATUS;
            }
            
            int count = secondPage.getPackages().size();

            Iterator it = secondPage.getPackages().keySet().iterator();
            monitor.beginTask("Installing composer packages - ", count + 2 );

            monitor.worked(1);
            while (it.hasNext()) {
                
                EclipsePHPPackage composerPackage = (EclipsePHPPackage) it.next();
                String version = secondPage.getPackages().get(composerPackage);

                try {
                    String dependency = composerPackage.getPhpPackage().getPackageName(version);
                    monitor.subTask("(require " + dependency + ")");
                    launcher = new DefaultExecutableLauncher();
                    String[] arg = new String[]{"require", dependency};
                    launcher.launch(composer.getLocation().toOSString(),
                            arg, new ConsoleResponseHandler(monitor));

                    composerPackage.createUserLibraryFromPackage(composer, monitor);
                    monitor.worked(1);
                    
                } catch (CoreException e) {
                    StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW|StatusManager.BLOCK);
                } catch (Exception e) {
                    Logger.logException(e);
                }
            }
            
            IProject project = composer.getProject();
            IResource vendor = project.findMember("vendor");

            try {
                if (vendor != null) {
                    vendor.refreshLocal(IResource.DEPTH_ONE, monitor);
                }
                // make sure that composer.json gets a refresh as well
                project.refreshLocal(IResource.DEPTH_ONE, monitor);
            } catch (CoreException e) {
                Logger.logException(e);
            } finally {
                monitor.worked(1);
                monitor.done();
            }
            
            return Status.OK_STATUS;
        }        
    }
}
