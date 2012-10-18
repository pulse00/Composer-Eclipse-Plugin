package com.dubture.composer.core.launch;

import org.eclipse.core.runtime.IProgressMonitor;

import com.dubture.composer.core.log.Logger;


public class ConsoleResponseHandler implements ILaunchResponseHandler {

    private IProgressMonitor monitor;

    public ConsoleResponseHandler(IProgressMonitor monitor) {
        this.monitor = monitor;
    }
    
	@Override
	public void handle(String response) {
		//TODO: log to eclipse console
		monitor.subTask(response);
	}

    @Override
    public void handleError(String response)
    {
        Logger.log(Logger.ERROR, response);
    }
}
