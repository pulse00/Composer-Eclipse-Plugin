package com.dubture.composer.core.launch;

import org.eclipse.core.runtime.IProgressMonitor;


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
}
