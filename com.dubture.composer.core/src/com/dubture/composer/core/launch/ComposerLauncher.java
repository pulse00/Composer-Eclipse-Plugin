package com.dubture.composer.core.launch;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.getcomposer.core.ComposerConstants;

import com.dubture.composer.core.launch.environment.Environment;
import com.dubture.composer.core.launch.environment.EnvironmentFactory;
import com.dubture.composer.core.launch.execution.ComposerExecutor;
import com.dubture.composer.core.launch.execution.ExecutionResponseListener;

public class ComposerLauncher {

	private Environment environment;
	private IProject project;
	private IResource composerJson;
	
	private ComposerExecutor executor;
	
	private Set<ExecutionResponseListener> listeners = new HashSet<ExecutionResponseListener>();
	
	private static Environment env = null;
	
	public ComposerLauncher(Environment environment, IProject project) throws ComposerJsonNotFoundException, ComposerPharNotFoundException {
		this.environment = environment;
		this.project = project;
		
		composerJson = project.findMember(ComposerConstants.COMPOSER_JSON);
		
		if (composerJson == null) {
			throw new ComposerJsonNotFoundException(null);
		}
		
		this.environment.setUp(project);
	}

	public void addResponseListener(ExecutionResponseListener listener) {
		listeners.add(listener);
	}

	public void removeResponseListener(ExecutionResponseListener listener) {
		listeners.remove(listener);
	}
	
	public void launch(String composerCommand) throws ExecuteException, IOException, InterruptedException {
		launch(composerCommand, new String[]{});
	}
	
	public void launch(String composerCommand, String param) throws ExecuteException, IOException, InterruptedException {
		launch(composerCommand, new String[]{param});
	}
	
	public void launch(String composerCommand, String[] params) throws ExecuteException, IOException, InterruptedException {
		CommandLine cmd = environment.getCommand();
		cmd.addArgument(composerCommand);
		cmd.addArguments(params);
		
		executor = new ComposerExecutor();
		executor.setWorkingDirectory(project.getLocation().toFile());
		
		for (ExecutionResponseListener listener : listeners) {
			executor.addResponseListener(listener);
		}
		
		executor.execute(cmd);
	}
	
	public void abort() {
		executor.abort();
	}
	
	private static Environment getEnvironment() {
		if (env == null) {
			env = EnvironmentFactory.getEnvironment();
		}
		
		return env;
	}
	
	public static ComposerLauncher getLauncher(IProject project) throws ComposerJsonNotFoundException, ComposerPharNotFoundException {
		Environment env = getEnvironment();
		if (env == null) {
			throw new ComposerPharNotFoundException("Can't find any executable");
		}
		return new ComposerLauncher(env, project);
	}
}
