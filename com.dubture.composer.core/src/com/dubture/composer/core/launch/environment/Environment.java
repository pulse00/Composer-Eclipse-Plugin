package com.dubture.composer.core.launch.environment;

import org.apache.commons.exec.CommandLine;
import org.eclipse.core.resources.IProject;

import com.dubture.composer.core.launch.ComposerPharNotFoundException;

public interface Environment {

	public boolean isAvailable();
	
	public void setUp(IProject project) throws ComposerPharNotFoundException;
	
	public CommandLine getCommand();
}
