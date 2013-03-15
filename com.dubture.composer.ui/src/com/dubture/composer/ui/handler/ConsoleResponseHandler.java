package com.dubture.composer.ui.handler;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.dubture.composer.core.launch.execution.ExecutionResponseAdapter;
import com.dubture.composer.core.log.Logger;

public class ConsoleResponseHandler extends ExecutionResponseAdapter {

	public ConsoleResponseHandler() {
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
	
	@Override
	public void executionAboutToStart() {
		MessageConsole console = findConsole("Composer");
		console.clearConsole();
	}
	
	@Override
	public void executionMessage(String message) {
		MessageConsole console = findConsole("Composer");
		MessageConsoleStream out = console.newMessageStream();
		out.print(message);
	}

	public void executionFailed(String response, Exception e) {
		Logger.log(Logger.ERROR, response);
	}

}
