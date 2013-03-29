package com.dubture.composer.core.internal.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.getcomposer.core.ComposerConstants;
import org.getcomposer.core.ComposerPackage;
import org.getcomposer.core.collection.InstalledPackages;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.resources.IComposerProject;

public class ComposerProject implements IComposerProject {

	private IProject project;
	private IScriptProject scriptProject;
	private ComposerPackage composer = null;
	private IFile json = null;
	private String vendorDir = null;
	private IPath vendorPath = null;
	
	public ComposerProject(IProject project) {
		this.project = project;
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
	public List<ComposerPackage> getInstalledPackages() {
		String vendor = getVendorDir();
		List<ComposerPackage> packages = new ArrayList<ComposerPackage>();
		
		IFile installed = project.getFile(vendor + "/composer/installed.json");
		if (installed != null && installed.exists()) {
			packages.addAll(loadInstalled(installed));
		}
		
		return packages;
	}

	@Override
	public List<ComposerPackage> getInstalledDevPackages() {
		String vendor = getVendorDir();
		List<ComposerPackage> packages = new ArrayList<ComposerPackage>();
		
		IFile installedDev = project.getFile(vendor + "/composer/installed_dev.json");
		if (installedDev != null && installedDev.exists()) {
			packages.addAll(loadInstalled(installedDev));
		}
		
		return packages;
	}

	@Override
	public List<ComposerPackage> getAllInstalledPackages() {
		List<ComposerPackage> packages = getInstalledPackages();
		packages.addAll(getInstalledDevPackages());
		return packages;
	}
	
	protected List<ComposerPackage> loadInstalled(IFile installed) {
		try {
			return new InstalledPackages(installed).toList();
		} catch (Exception e) {
			Logger.logException(e);
		}		
		
		return new ArrayList<ComposerPackage>();
	}

}
