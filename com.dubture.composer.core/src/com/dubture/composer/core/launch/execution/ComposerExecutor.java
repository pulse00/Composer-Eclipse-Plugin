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
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import com.dubture.composer.core.log.Logger;

public class ComposerExecutor {

	private DefaultExecutor executor;
	private ExecuteWatchdog watchdog;
	
	private PumpStreamHandler streamHandler;
	private ByteArrayOutputStream out;
	private ByteArrayOutputStream err;
	
	private StringBuilder outBuilder;
	private StringBuilder errBuilder;

	private static final int TIMEOUT = 60000;

	private Set<ExecutionResponseListener> listeners = new HashSet<ExecutionResponseListener>();
	
	private DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler() {
		
		public void onProcessComplete(int exitValue) {
			String response = outBuilder.toString();
			
			super.onProcessComplete(exitValue);
			
			for (ExecutionResponseListener handler : listeners) {
				handler.executionFinished(response, exitValue);
			}
		}

		public void onProcessFailed(ExecuteException e) {
			String response = errBuilder.toString();
			String stdOutResponse = outBuilder.toString();
			
			super.onProcessFailed(e);
			
			for (ExecutionResponseListener handler : listeners) {
				
				if (stdOutResponse.length() > 0) {
					response = stdOutResponse;
				}
				
				handler.executionFailed(response, e);
			}
		}
	};

	public ComposerExecutor() {
		out = new ByteArrayOutputStream();
		err = new ByteArrayOutputStream();
		streamHandler = new PumpStreamHandler(out, err);
		
		executor = new DefaultExecutor();
		executor.setStreamHandler(streamHandler);

		watchdog = new ExecuteWatchdog(TIMEOUT);
		executor.setWatchdog(watchdog);
	}

	public void addResponseListener(ExecutionResponseListener listener) {
		listeners.add(listener);
	}

	public void removeResponseListener(ExecutionResponseListener listener) {
		listeners.remove(listener);
	}

	public void setTimeout(long timeout) {
		watchdog = new ExecuteWatchdog(timeout);
		executor.setWatchdog(watchdog);
	}
	
	public void setExitValue(int exitValue) {
		executor.setExitValue(exitValue);
	}

	public void execute(String cmd) throws ExecuteException, IOException, InterruptedException {
		execute(CommandLine.parse(cmd));
	}

	public void execute(CommandLine cmd) {
		
		Thread outThread = new Thread(new Reader(out));
		Thread errThread = new Thread(new Reader(err));
		
		try {
			for (ExecutionResponseListener handler : listeners) {
				handler.executionAboutToStart();
			}
			
			outBuilder = new StringBuilder();
			errBuilder = new StringBuilder();
			
			outThread.start();
			errThread.start();
			
			Logger.debug("executing command using executable: " + cmd.getExecutable());
			executor.execute(cmd, handler);
			
			for (ExecutionResponseListener handler : listeners) {
				handler.executionStarted();
			}
			
			handler.waitFor();
		} catch (Exception e) {
			for (ExecutionResponseListener handler : listeners) {
				handler.executionFailed("", e);
			}
		} finally {
			if (outThread != null) {
				outThread.interrupt();
			}
			if (errThread != null) {
				errThread.interrupt();
			}
		}
	}
	
	public void abort() {
		if (watchdog != null) {
            watchdog.destroyProcess();
        }
	}
	
	public void setWorkingDirectory(File dir) {
		executor.setWorkingDirectory(dir);
	}
	
	public File getWorkingDirectory() {
		return executor.getWorkingDirectory();
	}

	private class Reader implements Runnable {
		private ByteArrayOutputStream stream;
		private boolean out = true;
		
		public Reader(ByteArrayOutputStream in) {
			if (in == err) {
				this.out = false;
			}
			stream = in;
		}

		public void run() {
			while (!Thread.interrupted()) {
				String content = stream.toString();
				stream.reset();
				if (!content.isEmpty()) {
					
					if (this.out) {
						outBuilder.append(content);
					} else {
						errBuilder.append(content);
					}
					
					for (ExecutionResponseListener listener : listeners) {
						if (this.out) {
							listener.executionMessage(content);
						} else {
							listener.executionError(content);
						}
					}
				}
			}
		}
	}
}
