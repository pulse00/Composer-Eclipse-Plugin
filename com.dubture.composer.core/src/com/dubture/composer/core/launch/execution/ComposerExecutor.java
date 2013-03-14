package com.dubture.composer.core.launch.execution;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;

import com.dubture.composer.core.launch.execution.ResponseHandler;

public class ComposerExecutor {

	private DefaultExecutor executor;
	private PumpStreamHandler streamHandler;
	private ByteArrayOutputStream outputStream;
	private ByteArrayOutputStream errStream;
	private long timeout = TIMEOUT;

	private static final int TIMEOUT = 60000;

	private Set<ResponseHandler> listeners = new HashSet<ResponseHandler>();

	public ComposerExecutor() {

		outputStream = new ByteArrayOutputStream();
		errStream = new ByteArrayOutputStream();
		streamHandler = new PumpStreamHandler(outputStream, errStream);

		executor = new DefaultExecutor();
		executor.setStreamHandler(streamHandler);

		setTimeout(TIMEOUT);
//		setExitValue(1);
	}

	public void addResponseHandler(ResponseHandler handler) {
		listeners.add(handler);
	}

	public void removeResponseHandler(ResponseHandler handler) {
		listeners.remove(handler);
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	public void setExitValue(int exitValue) {
		executor.setExitValue(exitValue);
	}

	public void execute(String cmd) throws ExecuteException, IOException, InterruptedException {
		execute(CommandLine.parse(cmd));
	}

	public void execute(CommandLine cmd) throws ExecuteException, IOException, InterruptedException {
		DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler() {
			public void onProcessComplete(int exitValue) {
				String response = "";
				if (outputStream != null) {
					response = outputStream.toString();
				}
				
				System.out.println("ComposerExecutor, complete: " + exitValue + ", response: " + response);
				
				super.onProcessComplete(exitValue);
				
				
				
				for (ResponseHandler handler : listeners) {
					handler.handle(exitValue, response);
				}
			}

			public void onProcessFailed(ExecuteException e) {
				String response = "";
				if (errStream != null) {
					response = errStream.toString();
				}
				
				System.out.println("ComposerExecutor, failed, response: " + response);
				e.printStackTrace();
				
				super.onProcessFailed(e);
				
				

				for (ResponseHandler handler : listeners) {
					handler.handleError(response);
				}
			}
		};
		
		executor.execute(cmd, handler);
		
		handler.waitFor(timeout);
	}
	
	public void setWorkingDirectory(File dir) {
		executor.setWorkingDirectory(dir);
	}
	
	public File getWorkingDirectory() {
		return executor.getWorkingDirectory();
	}
}
