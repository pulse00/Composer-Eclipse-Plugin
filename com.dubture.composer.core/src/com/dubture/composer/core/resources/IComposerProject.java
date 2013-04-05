package com.dubture.composer.core.resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IScriptProject;

import com.dubture.getcomposer.core.ComposerPackage;
import com.dubture.getcomposer.core.collection.ComposerPackages;


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
	
	public ComposerPackages getInstalledPackages();
	
	public ComposerPackages getInstalledDevPackages();
	
	public ComposerPackages getAllInstalledPackages();
}
