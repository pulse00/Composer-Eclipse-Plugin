package com.dubture.composer.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;

import com.dubture.composer.core.launch.PharNotFoundException;
import com.dubture.composer.ui.job.DownloadJob;
import com.dubture.composer.ui.job.InstallJob;

/**
 * 
 */
public class InstallHandler extends AbstractHandler
{
	@Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
//		InstallJob job = new InstallJob(project);
//		job.setUser(true);
//		job.schedule();
		System.out.println("Install handler");
		
        
        return null;
    }
}
