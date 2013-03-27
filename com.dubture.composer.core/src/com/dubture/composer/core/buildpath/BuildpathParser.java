package com.dubture.composer.core.buildpath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.getcomposer.core.ComposerConstants;
import org.getcomposer.core.ComposerPackage;
import org.getcomposer.core.objects.Autoload;
import org.getcomposer.core.objects.Namespace;

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
		
		ComposerPackage composer = getComposerPackage();
		
		if (composer == null) {
			return null;
		}
		
		String vendor = getVendorDir(composer);
		
		
		// root folder for packages
		IPath packageRoot = getVendorPath(vendor);

		
		// find installed composer packages
		List<ComposerPackage> packages = new ArrayList<ComposerPackage>();
		if (packageRoot.toFile().exists()) {
			File[] vendors = packageRoot.toFile().listFiles();
			
			for (File v : vendors) {
				if (v.isDirectory()) {
					for (File p : v.listFiles()) {
						if (p.isDirectory()) {
							File composerJson = new File(p, ComposerConstants.COMPOSER_JSON);
							if (composerJson.exists()) {
								try {
									packages.add(new ComposerPackage(composerJson));
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
		
		return packages;
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
					paths.add(vendor + "/" + p.getName() + "/" + path);
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
