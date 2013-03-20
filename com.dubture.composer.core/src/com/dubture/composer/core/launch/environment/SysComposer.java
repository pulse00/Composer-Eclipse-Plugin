package com.dubture.composer.core.launch.environment;

import org.apache.commons.exec.CommandLine;
import org.eclipse.core.resources.IProject;

public class SysComposer implements Environment {

	private String composer;
	
	public SysComposer() {
		composer = EnvironmentFinder.findComposer();
	}
	
	public boolean isAvailable() {
		return composer != null;
	}

	public void setUp(IProject project) {
		
	}

	public CommandLine getCommand() {
		return new CommandLine(composer);
	}
	
}
