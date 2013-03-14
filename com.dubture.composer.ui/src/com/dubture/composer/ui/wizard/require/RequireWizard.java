package com.dubture.composer.ui.wizard.require;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.statushandlers.StatusManager;

import com.dubture.composer.core.ComposerPluginImages;
import com.dubture.composer.core.launch.DefaultExecutableLauncher;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.model.EclipsePHPPackage;
import com.dubture.composer.core.model.ModelAccess;
import com.dubture.composer.ui.handler.ConsoleResponseHandler;

public class RequireWizard extends Wizard
{
    private RequirePageOne firstPage;
    private RequirePageTwo secondPage;
    private final IResource composer;
//    private final IScriptProject project;

    public RequireWizard(IResource composer, IScriptProject project)
    {
        setDefaultPageImageDescriptor(ComposerPluginImages.DESC_WIZBAN_ADD_DEPENDENCY);
        setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
        setWindowTitle("Search packagist.org");

//        this.project = project;
        this.composer = composer;
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
            List<String> deps = new ArrayList<String>();
            deps.add("require");
            
            monitor.beginTask("Installing composer packages - ", count + 2 );
            monitor.worked(1);
            
            while (it.hasNext()) {
                try {
                    EclipsePHPPackage composerPackage = (EclipsePHPPackage) it.next();
                    String version = secondPage.getPackages().get(composerPackage);
//                    String dependency = composerPackage.getPhpPackage().getPackageName(version);
                    String dependency = "";
                    System.err.println(dependency);
                    deps.add(dependency);
                } catch (Exception e) {
                    Logger.logException(e);
                }
            }
            
            try {
                monitor.subTask("(require " + deps);
                launcher = new DefaultExecutableLauncher();
                launcher.launch(composer.getLocation().toOSString(),
                        deps.toArray(new String[deps.size()]), new ConsoleResponseHandler(monitor));
                monitor.worked(1);
            } catch (CoreException e) {
                StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW|StatusManager.BLOCK);
            } catch (Exception e) {
                Logger.logException(e);
            }
            
            IProject project = composer.getProject();
            IResource vendor = project.findMember("vendor");

            try {
                if (vendor != null) {
                    vendor.refreshLocal(IResource.DEPTH_ONE, monitor);
                }
                // make sure that composer.json gets a refresh as well
                project.refreshLocal(IResource.DEPTH_ONE, monitor);
                ModelAccess.getInstance().getPackageManager().updateBuildpath();
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
