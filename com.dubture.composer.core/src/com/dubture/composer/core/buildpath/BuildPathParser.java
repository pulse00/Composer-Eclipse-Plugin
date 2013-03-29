package com.dubture.composer.core.buildpath;

import java.io.File;
import java.io.IOException;
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
import com.dubture.composer.core.resources.IComposerProject;

public class BuildPathParser {
	
	private IComposerProject project;

	public BuildPathParser(IComposerProject project) {
		this.project = project;
	}
	
	
	public List<String> getPaths() {
		List<ComposerPackage> packages = project.getInstalledPackages();
		if (packages == null) {
			return null;
		}
		
		ComposerPackage composer = project.getComposerPackage();
		String vendor = project.getVendorDir();
		IPath packageRoot = project.getVendorPath();
		
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
		
		// add source paths from this package
		Autoload autoload = composer.getAutoload();
		
		for (Namespace namespace : autoload.getPsr0()) {
			for (Object path : namespace.getPaths()) {
				paths.add("/" + project.getProject().getName() + "/" + path);
			}
		}
		
		// maybe don't add this one
		paths.add("/" + vendor + "/composer/");
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
