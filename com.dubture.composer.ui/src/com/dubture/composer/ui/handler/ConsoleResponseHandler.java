package com.dubture.composer.ui.handler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.dubture.composer.core.launch.execution.ResponseHandler;
import com.dubture.composer.core.log.Logger;

public class ConsoleResponseHandler implements ResponseHandler {

	private IProgressMonitor monitor;

	public ConsoleResponseHandler(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];

		// no console found, so create a new one
		MessageConsole console = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { console });
		return console;
	}

	public void handle(int exitValue, String response) {
		MessageConsole console = findConsole("Composer");
		console.clearConsole();
		MessageConsoleStream out = console.newMessageStream();
		out.println(response);
		
		monitor.subTask(response);
	}

	public void handleError(String response) {
		Logger.log(Logger.ERROR, response);
	}

}
