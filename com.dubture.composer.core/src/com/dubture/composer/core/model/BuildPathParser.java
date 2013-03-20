package com.dubture.composer.core.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.getcomposer.core.ComposerPackage;
import org.getcomposer.core.objects.Autoload;
import org.getcomposer.core.objects.Namespace;

public class BuildPathParser {
	
	private IProject project;
	private ComposerPackage composer = null;
	private IResource json = null;

	public BuildPathParser(IProject project) {
		this.project = project;
	}
	
	private IResource getComposerJson() {
		if (json == null) {
			json = project.findMember("composer.json");
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
	
	public List<String> getPaths() {
		
		ComposerPackage composer = getComposerPackage();
		
		if (composer == null) {
			return null;
		}
		
		
		// find 'vendor' folder
		String vendor = composer.getConfig().getVendorDir();
		
		if (vendor == null || vendor.isEmpty()) {
			vendor = "vendor"; // default
		}
		
		
		// root folder for packages
		IResource json = getComposerJson();
		IPath root = json.getLocation();

		IPath packages = root.addTrailingSeparator().append(vendor);

		
		// find installed composer packages
		List<ComposerPackage> composers = new ArrayList<ComposerPackage>();
		if (packages.toFile().exists()) {
			File[] vendors = packages.toFile().listFiles();
			
			for (File v : vendors) {
				if (v.isDirectory()) {
					for (File p : v.listFiles()) {
						if (p.isDirectory()) {
							File composerJson = new File(p, "composer.json");
							if (composerJson.exists()) {
								try {
									composers.add(new ComposerPackage(composerJson));
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}

		
		// find paths for found composer packages
		List<String> paths = new ArrayList<String>();
		for (ComposerPackage p : composers) {
			Autoload a = p.getAutoload();
			
			// psr first
			for (Namespace psr : a.getPsr0()) {
				for (Object path : psr.getAll()) {
					paths.add(vendor + "/" + p.getName() + "/" + path);
				}
			}
			
			// classmap
			for (Object path : a.getClassMap()) {
				String cleanedPath = getDirectory(p.getName() + "/" + (String) path, packages);
				addPath(cleanedPath, paths);
			}
			
			// files
			for (Object path : a.getFiles()) {
				String cleanedPath = getDirectory(p.getName() + "/" + (String) path, packages);
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
		if (path != null && !paths.contains(path)) {
			paths.add(path);
		}
	}
}
