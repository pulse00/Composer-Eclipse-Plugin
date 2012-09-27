package com.dubture.composer.core.ui.wizard.require;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

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

        IRunnableWithProgress op = new IRunnableWithProgress()
        {
            @SuppressWarnings("rawtypes")
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException
            {
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
                        DefaultExecutableLauncher launcher = new DefaultExecutableLauncher();
                        String[] arg = new String[]{"require", dependency};
                        launcher.launch(composer.getLocation().toOSString(),
                                arg, new ConsoleResponseHandler(monitor));

                        monitor.worked(1);
                        
                    } catch (Exception e) {
                        Logger.logException(e);
                    }
                }
                
                IProject project = composer.getProject();
                IResource vendor = project.findMember("vendor");

                try {
                    if (vendor != null) {
                        vendor.refreshLocal(IResource.DEPTH_ONE, monitor);
                    } else {
                        project.refreshLocal(IResource.DEPTH_ONE, monitor);
                    }
                } catch (CoreException e) {
                    Logger.logException(e);
                } finally {
                    monitor.worked(1);
                    monitor.done();
                }
            }
        };

        try {
            getContainer().run(true, false, op);
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());            
            return false;
        } catch (InterruptedException e) {
            return false;
        }

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
}
