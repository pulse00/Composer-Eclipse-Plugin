/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.dubture.composer.core.launch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.core.log.Logger;

/**
 * 
 * @author "Robert Gruendler <r.gruendler@gmail.com>"
 * @deprecated
 */
public class DefaultExecutableLauncher implements IPHPLauncher {

    private DefaultExecutor executor;
    private PumpStreamHandler streamHandler;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errStream;
    private ExecuteWatchdog watchdog;
    
    private static final int TIMEOUT = 60000;
    
    public DefaultExecutableLauncher() {
        
        outputStream = new ByteArrayOutputStream();
        errStream = new ByteArrayOutputStream();
        streamHandler = new PumpStreamHandler(outputStream, errStream);
        executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        watchdog = new ExecuteWatchdog(TIMEOUT);
        executor.setWatchdog(watchdog);        
        
    }

    @Override
	public void launch(String scriptPath, String[] arguments, ILaunchResponseHandler handler) throws IOException, InterruptedException, CoreException {

		doLaunch(scriptPath, arguments, handler, false);
		
	}

	@Override
	public void launchAsync(String scriptPath, String[] arguments, ILaunchResponseHandler handler)
			throws IOException, InterruptedException, CoreException {

		doLaunch(scriptPath, arguments, handler, true);
		
	}
	
	protected void doLaunch(String scriptPath, String[] arguments, final ILaunchResponseHandler handler, boolean async) throws IOException, InterruptedException, CoreException {
		
		String phpExe = null;
		
		try {
			Logger.debug("Getting default executable");
			phpExe = LaunchUtil.getPHPExecutable();
		} catch (ExecutableNotFoundException e) {
		    showWarning();
		    e.printStackTrace();
	        Logger.logException(e);
			return;
		}
		
		Logger.debug("Launching workspace php executable: " + phpExe + " using target " + scriptPath);
		CommandLine cmd = new CommandLine(phpExe);
		cmd.addArgument(scriptPath);
		for (int i=0; i < arguments.length; i++) {
		    cmd.addArgument(arguments[i], false);
		}
		
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler() {
            @Override
            public void onProcessComplete(int exitValue)
            {
                super.onProcessComplete(exitValue);
                if (handler != null && outputStream != null) {
                    handler.handle(outputStream.toString());
                }
            }
            
            @Override
            public void onProcessFailed(ExecuteException e)
            {
                super.onProcessFailed(e);
                
                if (handler != null && errStream != null) {
                    handler.handleError(errStream.toString());
                }
            }
        };
        
//        System.out.println("Command: " + cmd.toString());
        
        executor.setWorkingDirectory(new File(new Path(scriptPath).removeLastSegments(1).toOSString()));
        executor.execute(cmd, resultHandler);
        
	    if (async == false) {
	        resultHandler.waitFor();
	    }
	}

    public void abort()
    {
        if (watchdog != null) {
            watchdog.destroyProcess();
        }
    }
    
    private void showWarning() 
    {
        IWorkbench workbench = PlatformUI.getWorkbench();
        
        if (workbench == null) {
            Logger.debug("Error retrieving executable");
            return;
        }
        
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        
        if (window == null) {
            Logger.debug("Error retrieving executable");
            return;
        }
        
        
        MessageBox dialog = new MessageBox(
                window.getShell(), SWT.ICON_QUESTION
                        | SWT.OK);
        dialog.setText("PHP executable not found");
        dialog.setMessage("Your PHP executable has not been configured properly.");
        dialog.open();
    }
}
