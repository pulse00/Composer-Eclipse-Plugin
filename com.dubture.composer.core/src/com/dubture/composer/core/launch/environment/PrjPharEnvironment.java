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
		
		if (phar.getFullPath().segmentCount() != 2) {
			throw new ComposerPharNotFoundException("The composer.phar file in project " + project.getName() + " is in the wrong location."); 
		}
		
		this.phar = phar.getFullPath().removeFirstSegments(1).toOSString();
	}

	
}
