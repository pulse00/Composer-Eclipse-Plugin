package com.dubture.composer.core.buildpath;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;

import com.dubture.composer.core.resources.IComposerProject;
import com.dubture.getcomposer.core.ComposerPackage;
import com.dubture.getcomposer.core.collection.ComposerPackages;
import com.dubture.getcomposer.core.objects.Autoload;
import com.dubture.getcomposer.core.objects.Namespace;

public class BuildPathParser {
	
	private IComposerProject project;

	public BuildPathParser(IComposerProject project) {
		this.project = project;
	}
	
	public List<String> getPaths() {
		ComposerPackages packages = project.getInstalledPackages();
		if (packages == null) {
			return null;
		}
		
		ComposerPackage composer = project.getComposerPackage();
		String vendor = project.getVendorDir();
		
		// empty list for found package paths
		List<String> paths = new ArrayList<String>();
		
		// add source paths from this package
		parsePackage(composer, paths);
		
		// all installed packages
		for (ComposerPackage p : packages) {
			parsePackage(p, paths, vendor + "/" + p.getName());
		}
		
		// maybe add this one ?!
		// those want/need it, can add it via project settings
		paths.add(vendor + "/composer");
		return paths;
	}
	
	private void parsePackage(ComposerPackage pkg, List<String> paths) {
		parsePackage(pkg, paths, "");
	}
	
	private void parsePackage(ComposerPackage pkg, List<String> paths, String prefix) {
		if (prefix != null && !prefix.equals("") && !prefix.endsWith("/")) {
			prefix += "/";
		}

		Autoload a = pkg.getAutoload();
		
		// psr-0
		for (Namespace namespace : a.getPsr0()) {
			for (Object path : namespace.getPaths()) {
				addPath(prefix + path, paths);
			}
		}
		
		// classmap
		for (Object path : a.getClassMap()) {
			String cleanedPath = getDirectory(prefix + (String) path);
			addPath(cleanedPath, paths);
		}
		
		// files
		for (Object path : a.getFiles()) {
			String cleanedPath = getDirectory(prefix + (String) path);
			addPath(cleanedPath, paths);
		}
	}
	
	private String getDirectory(String path) {
		String cleanedPath = null;
		IPath root = project.getProject().getLocation();
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
		if (path != null && !path.trim().isEmpty()) {
			// path cleanup
			if (path.startsWith("/")) {
				path = path.substring(1);
			}
			
			if (path.endsWith("/.")) {
				path = path.substring(0, path.length() - 2);
			}
			
			if (path.endsWith("/")) {
				path = path.substring(0, path.length() - 1);
			}
			
			if (path.equals(".")) {
				path = "";
			}
			
//			if (!path.isEmpty()) {
//				path = project.getProject().getFullPath().toString() + "/" + path;
//			} else {
//				path = project.getProject().getFullPath().toString();
//			}
			
			if (path.startsWith("/")) {
				path = path.substring(1);
			}
			
			if (!paths.contains(path)) {
				paths.add(path);
			}
		}
	}
}
