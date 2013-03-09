package com.dubture.composer.core.job;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.launch.ConsoleResponseHandler;
import com.dubture.composer.core.launch.DefaultExecutableLauncher;

abstract public class ComposerJob extends Job
{
    protected String composerPhar;
    protected IProject project;

    protected static final IStatus ERROR_STATUS = new Status(Status.ERROR,
            ComposerPlugin.ID,
            "Error installing composer dependencies, see log for details");

    public ComposerJob(String name)
    {
        super(name);
    }
    
    public ComposerJob(IProject project, String name) {
    	this(name);
    	this.project = project;
    	composerPhar = project.findMember("composer.phar").getLocation().toOSString();
    }

    protected void execute(String argument, IProgressMonitor monitor) throws IOException,
            InterruptedException, CoreException
    {
        doExecute(new String[]{argument}, monitor);
    }

    protected void execute(String argument, String[] composerArgs, IProgressMonitor monitor)
            throws IOException, InterruptedException, CoreException
    {
        String[] args = new String[composerArgs.length + 1];
        args[0] = argument;
        System.arraycopy(composerArgs, 0, args, 1, composerArgs.length);
        doExecute(args, monitor);
    }
    
    private void doExecute(String[] arguments, IProgressMonitor monitor) throws IOException, InterruptedException, CoreException 
    {
        new DefaultExecutableLauncher().launch(composerPhar, arguments, new ConsoleResponseHandler(monitor));
    }
}
