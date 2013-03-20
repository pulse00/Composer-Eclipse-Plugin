package com.dubture.composer.core.launch.environment;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.dubture.composer.core.launch.ComposerPharNotFoundException;

public abstract class PrjPharEnvironment implements Environment {

	protected String phar;
	
	public void setUp(IProject project) throws ComposerPharNotFoundException {
		IResource phar = project.findMember("composer.phar");
		if (phar == null) {
			throw new ComposerPharNotFoundException("No composer.phar found in project " + project.getName());
		}
		
		this.phar = phar.getFullPath().toOSString();
	}

	
}
