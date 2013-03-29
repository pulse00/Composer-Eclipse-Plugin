package com.dubture.composer.core.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.php.internal.core.buildpath.BuildPathUtils;
import org.getcomposer.core.ComposerConstants;
import org.getcomposer.core.ComposerPackage;

import com.dubture.composer.core.ComposerNature;
import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.buildpath.BuildPathManager;
import com.dubture.composer.core.buildpath.BuildPathParser;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.resources.IComposerProject;

/**
 * This builder is checking for changes inside the `vendor` directory and
 * adjusts the buildpath of the project accordingly.
 * 
 */
@SuppressWarnings("restriction")
public class ComposerBuildPathManagementBuilder extends
		IncrementalProjectBuilder {

	public static final String ID = "com.dubture.composer.core.builder.buildPathManagementBuilder";

	@Override
	protected IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {

		IProject project = getProject();

		if (project.getProject().hasNature(ComposerNature.NATURE_ID) == false) {
			return null;
		}

		// return when no composer.json present
		IComposerProject composerProject = ComposerPlugin.getDefault().getComposerProject(project);
		IFile composerJson = composerProject.getComposerJson();
		if (composerJson == null) {
			return null;
		}

		try {
			boolean changed = false;
			IResourceDelta delta = getDelta(project);

			if (delta == null) {
				Logger.debug("Composer builder executed but did get no delta");
				return null;
			}

			String vendor = composerProject.getVendorDir();

			for (IResourceDelta affected : delta.getAffectedChildren()) {
				String path = affected.getProjectRelativePath().toOSString();

				if (path.equals("composer.lock") || path.equals("vendor") || path.equals(vendor)
						&& affected.getKind() == IResourceDelta.ADDED || path.equals(vendor + "/composer/autoload_namespaces.php")
						|| path.equals(vendor + "/composer/autoload_classmap.php")
						|| path.equals(vendor + "/composer/autoload_files.php")) {
					changed = true;
				}
			}
			
			// nothing to do
			if (!changed) {
				return null;
			}
			
			
			BuildPathManager buildPathManager = new BuildPathManager(composerProject);
			buildPathManager.update();
		} catch (Exception e) {
			Logger.logException(e);
		}

		return null;
	}
}
