package com.dubture.composer.core.job;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.launch.ConsoleResponseHandler;
import com.dubture.composer.core.launch.DefaultExecutableLauncher;

abstract public class ComposerJob extends Job
{
    protected String composer;

    protected static final IStatus ERROR_STATUS = new Status(Status.ERROR,
            ComposerPlugin.ID,
            "Error installing composer dependencies, see log for details");

    public ComposerJob(String name)
    {
        super(name);
    }

    protected void execute(String argument, IProgressMonitor monitor) throws IOException,
            InterruptedException
    {
        doExecute(new String[]{argument}, monitor);
    }

    protected void execute(String argument, String[] composerArgs, IProgressMonitor monitor)
            throws IOException, InterruptedException
    {
        String[] args = new String[composerArgs.length + 1];
        args[0] = argument;
        System.arraycopy(composerArgs, 0, args, 1, composerArgs.length);
        doExecute(args, monitor);
    }
    
    private void doExecute(String[] arguments, IProgressMonitor monitor) throws IOException, InterruptedException 
    {
        new DefaultExecutableLauncher().launch(composer, arguments, new ConsoleResponseHandler(monitor));
    }
}
