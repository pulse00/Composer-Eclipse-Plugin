package com.dubture.composer.core.job;

import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.pex.core.log.Logger;

import com.dubture.composer.core.CorePlugin;

public class DownloadJob extends Job
{
    private IProject project;

    public DownloadJob(IProject project, String name)
    {
        super(name);
        this.project = project;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        try {
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod("http://getcomposer.org/composer.phar");
            
            monitor.worked(40);
            client.executeMethod(get);
            
            InputStream stream = get.getResponseBodyAsStream();
            
            monitor.worked(80);
            
            IFile file = project.getFile("composer.phar");
            file.create(stream, true, new NullProgressMonitor());
            file.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
            monitor.worked(100);
            
        } catch (Exception e) {
            Logger.logException(e);
            return new Status(Status.ERROR, CorePlugin.ID, "Error while downloading composer.phar. See {workspace}/.metadata/.log for details");
        }
        return Status.OK_STATUS;
    }
}
