package com.dubture.composer.core.buildpath;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.getcomposer.core.ComposerConstants;
import org.getcomposer.core.ComposerPackage;
import org.getcomposer.core.collection.InstalledPackages;
import org.getcomposer.core.objects.Autoload;
import org.getcomposer.core.objects.Namespace;

import com.dubture.composer.core.log.Logger;

public class BuildpathParser {
	
	private IProject project;
	private ComposerPackage composer = null;
	private IResource json = null;

	public BuildpathParser(IProject project) {
		this.project = project;
	}
	
	private IResource getComposerJson() {
		if (json == null) {
			json = project.findMember(ComposerConstants.COMPOSER_JSON);
		}
		return json;
	}
	
	private ComposerPackage getComposerPackage() {
		if (composer == null) {
			try {
				IResource json = getComposerJson();
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
	
	private String getVendorDir(ComposerPackage composer) {
		// find 'vendor' folder
		String vendor = composer.getConfig().getVendorDir();
		
		if (vendor == null || vendor.trim().isEmpty()) {
			vendor = ComposerConstants.VENDOR_DIR_DEFAULT; // default
		}
		
		return vendor;
	}
	
	private IPath getVendorPath(String vendor) {
		IResource json = getComposerJson();
		IPath root = json.getLocation();

		if (root == null || root.segmentCount() <= 1) {
			throw new RuntimeException("Error getting composer vendor path");
		}
		
		
		return root.removeLastSegments(1).addTrailingSeparator().append(vendor);
	}
	
	
	public List<ComposerPackage> getInstalledPackages() {
		
		List<ComposerPackage> packages = new ArrayList<ComposerPackage>();
		
		IFile installed = project.getFile("vendor/composer/installed.json");
		if (installed != null && installed.exists()) {
			packages.addAll(loadInstalled(installed));
		}
		
		IFile installedDev = project.getFile("vendor/composer/installed_dev.json");
		if (installedDev != null && installedDev.exists()) {
			packages.addAll(loadInstalled(installedDev));
		}
		
		return packages;
	}
	
	protected List<ComposerPackage> loadInstalled(IFile installed) {

		try {
			InputStreamReader reader = new InputStreamReader(installed.getContents());
			return new InstalledPackages(reader).toList();
		} catch (Exception e) {
			Logger.logException(e);
		}		
		
		return new ArrayList<ComposerPackage>();
	}
	
	public List<String> getPaths() {
		List<ComposerPackage> packages = getInstalledPackages();
		if (packages == null) {
			return null;
		}
		
		ComposerPackage composer = getComposerPackage();
		String vendor = getVendorDir(composer);
		IPath packageRoot = getVendorPath(vendor);
		
		// find paths for found composer packages
		List<String> paths = new ArrayList<String>();
		for (ComposerPackage p : packages) {
			Autoload a = p.getAutoload();
			
			// psr first
			for (Namespace namespace : a.getPsr0()) {
				for (Object path : namespace.getPaths()) {
					String target = "";
					if (p.getTargetDir() != null && p.getTargetDir().length() > 0) {
						target = p.getTargetDir();
						String pathAsString = (String) path;
						if ( ! pathAsString.endsWith("/") && pathAsString.length() > 0  && !target.startsWith("/")) {
							target = "/" + target;
						}
						if (!target.endsWith("/")) {
							target += "/";
						}
					}
					paths.add("/" + vendor + "/" + p.getName() + "/" + path + target);
				}
			}
			
			// classmap
			for (Object path : a.getClassMap()) {
				String cleanedPath = vendor +  getDirectory( p.getName() + "/" + (String) path, packageRoot);
				addPath(cleanedPath, paths);
			}
			
			// files
			for (Object path : a.getFiles()) {
				String cleanedPath = getDirectory(p.getName() + "/" + (String) path, packageRoot);
				addPath(cleanedPath, paths);
			}
		}
		
		Autoload autoload = composer.getAutoload();
		
		for (Namespace namespace : autoload.getPsr0()) {
			for (Object path : namespace.getPaths()) {
				paths.add("/" + project.getName() + "/" + path);
			}
		}
		
		
		paths.add("/vendor/composer/");
		return paths;
	}
	
	
	private String getDirectory(String path, IPath root) {
		String cleanedPath = null;
		File f = new File(root.toFile(), path);
		if (f.exists()) {
			if (f.isDirectory()) {
				cleanedPath = f.getPath().replace(root.toOSString(), "");
			} else {
				cleanedPath = f.getParentFile().getPath().replace(root.toOSString(), "");
			}
		}
		return cleanedPath;
	}
	
	private void addPath(String path, List<String> paths) {
		if (path != null && !path.trim().isEmpty() && !paths.contains(path)) {
			paths.add(path);
		}
	}
}
