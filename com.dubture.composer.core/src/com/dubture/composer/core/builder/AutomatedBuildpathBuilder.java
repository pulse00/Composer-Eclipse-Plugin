package com.dubture.composer.core.builder;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.getcomposer.core.ComposerConstants;
import org.getcomposer.core.ComposerPackage;

import com.dubture.composer.core.log.Logger;

public class AutomatedBuildpathBuilder extends IncrementalProjectBuilder {

	public static final String ID = "com.dubture.composer.core.builder.automatedBuildpathBuilder";
	
	@Override
	protected IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {

		IProject project = getProject();
		
	
		
		// return when no composer.json present
		IResource composer = project.findMember("composer.json"); 
		if (composer == null) {
			return null;
		}
		
		try {
			boolean changed = false;
			IResourceDelta delta = getDelta(project);
			ComposerPackage composerPackage = new ComposerPackage(composer.getLocation().toFile());
			String vendor = composerPackage.getConfig().getVendorDir();
			
			if (vendor == null || vendor.trim().isEmpty()) {
				vendor = ComposerConstants.VENDOR_DIR_DEFAULT;
			}
			
			for (IResourceDelta affected : delta.getAffectedChildren()) {
				String path = affected.getProjectRelativePath().toOSString();
				
				if (path.equals("composer.lock")
						|| path.equals(vendor) && affected.getKind() == IResourceDelta.ADDED
						|| path.equals(vendor + "/composer/autoload_namespaces.php")
						|| path.equals(vendor + "/composer/autoload_classmap.php")
						|| path.equals(vendor + "/composer/autoload_files.php") /* hum? */) {
					changed = true;
				}
			}
			
			if (changed) {
				// run the update
			}
		} catch (IOException e) {
			Logger.logException(e);
		}
		
		return null;
	}

}
