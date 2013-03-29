package com.dubture.composer.core.resources;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IScriptProject;
import org.getcomposer.core.ComposerPackage;


public interface IComposerProject {

	public String getVendorDir();
	
	/**
	 * Returns the absolute path to the vendor directory
	 * 
	 * @return
	 */
	public IPath getVendorPath();
	
	public IFile getComposerJson();

	public ComposerPackage getComposerPackage();
	
	public IProject getProject();
	
	public IScriptProject getScriptProject();
	
	public List<ComposerPackage> getInstalledPackages();
	
	public List<ComposerPackage> getInstalledDevPackages();
	
	public List<ComposerPackage> getAllInstalledPackages();
}
