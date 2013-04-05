package com.dubture.composer.core.internal.resources;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.resources.IComposerProject;
import com.dubture.getcomposer.core.ComposerConstants;
import com.dubture.getcomposer.core.ComposerPackage;
import com.dubture.getcomposer.core.collection.ComposerPackages;

public class ComposerProject implements IComposerProject {

	private IProject project;
	private IScriptProject scriptProject;
	private ComposerPackage composer = null;
	private IFile json = null;
	private String vendorDir = null;
	private IPath vendorPath = null;
	
	public ComposerProject(IProject project) throws IOException {
		this.project = project;
		IFile file = project.getFile(ComposerConstants.COMPOSER_JSON);
		
		if (file != null) {
			composer = new ComposerPackage(file.getLocation().toFile()); 
		}
	}
	
	@Override
	public String getVendorDir() {
		if (vendorDir == null) {
			vendorDir = composer.getConfig().getVendorDir();
			
			if (vendorDir == null || vendorDir.trim().isEmpty()) {
				vendorDir = ComposerConstants.VENDOR_DIR_DEFAULT; // default
			}
		}
		
		return vendorDir;
	}

	@Override
	public IPath getVendorPath() {
		if (vendorPath == null) {
			IPath root = project.getLocation();
			String vendor = getVendorDir();
	
			if (root == null || root.segmentCount() <= 1) {
				throw new RuntimeException("Error getting composer vendor path");
			}
	
			vendorPath = root.removeLastSegments(1).addTrailingSeparator().append(vendor);
		}
		return vendorPath;
	}

	@Override
	public IFile getComposerJson() {
		if (json == null) {
			json = project.getFile(ComposerConstants.COMPOSER_JSON);
		}
		return json;
	}

	@Override
	public ComposerPackage getComposerPackage() {
		if (composer == null) {
			try {
				IFile json = getComposerJson();
				if (json == null) {
					return null;
				}
				composer = new ComposerPackage(json.getLocation().toFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return composer;
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public IScriptProject getScriptProject() {
		if (scriptProject == null) {
			scriptProject = DLTKCore.create(project);
		}
		return scriptProject;
	}

	@Override
	public ComposerPackages getInstalledPackages() {
		String vendor = getVendorDir();
		ComposerPackages packages = new ComposerPackages();
		
		IFile installed = project.getFile(vendor + "/composer/installed.json");
		if (installed != null && installed.exists()) {
			packages.addAll(loadInstalled(installed));
		}
		
		return packages;
	}

	@Override
	public ComposerPackages getInstalledDevPackages() {
		String vendor = getVendorDir();
		ComposerPackages packages = new ComposerPackages();
		
		IFile installedDev = project.getFile(vendor + "/composer/installed_dev.json");
		if (installedDev != null && installedDev.exists()) {
			packages.addAll(loadInstalled(installedDev));
		}
		
		return packages;
	}

	@Override
	public ComposerPackages getAllInstalledPackages() {
		ComposerPackages packages = getInstalledPackages();
		packages.addAll(getInstalledDevPackages());
		return packages;
	}
	
	protected ComposerPackages loadInstalled(IFile installed) {
		try {
			if (installed.getLocation() != null) {
				return new ComposerPackages(installed.getLocation().toFile());
			}
		} catch (Exception e) {
			Logger.logException(e);
		}		
		
		return new ComposerPackages();
	}

}
