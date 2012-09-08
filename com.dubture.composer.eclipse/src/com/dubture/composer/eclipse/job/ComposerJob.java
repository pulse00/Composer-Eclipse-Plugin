package com.dubture.composer.eclipse.job;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.dubture.composer.eclipse.ComposerPlugin;
import com.dubture.composer.eclipse.launch.ConsoleResponseHandler;
import com.dubture.composer.eclipse.launch.DefaultExecutableLauncher;

abstract public class ComposerJob extends Job
{
    protected String composer;
    
    protected static final IStatus ERROR_STATUS = new Status(Status.ERROR, ComposerPlugin.ID, 
            "Error installing composer dependencies, see log for details");

    public ComposerJob(String name)
    {
        super(name);
    }

    protected void execute(String argument) throws IOException, InterruptedException
    {
        DefaultExecutableLauncher launcher = new DefaultExecutableLauncher();
        String[] arg = new String[]{argument};
        launcher.launch(composer, arg, new ConsoleResponseHandler());

    }
}
