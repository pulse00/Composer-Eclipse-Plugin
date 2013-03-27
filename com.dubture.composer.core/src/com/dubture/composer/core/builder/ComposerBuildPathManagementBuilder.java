package com.dubture.composer.core.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.dubture.composer.core.buildpath.BuildpathParser;
import com.dubture.composer.core.log.Logger;

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
		IResource composer = project
				.findMember(ComposerConstants.COMPOSER_JSON);
		if (composer == null) {
			return null;
		}

		IScriptProject scriptProject = DLTKCore.create(project);

		try {
			boolean changed = false;
			IResourceDelta delta = getDelta(project);

			if (delta == null) {
				Logger.debug("Composer builder executed but did get no delta");
				return null;
			}

			ComposerPackage composerPackage = new ComposerPackage(composer.getLocation().toFile());
			String vendor = composerPackage.getConfig().getVendorDir();

			if (vendor == null || vendor.trim().isEmpty()) {
				vendor = ComposerConstants.VENDOR_DIR_DEFAULT;
			}

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

			BuildpathParser parser = new BuildpathParser(project);
			List<String> paths = parser.getPaths();
			List<IBuildpathEntry> newEntries = new ArrayList<IBuildpathEntry>();

			// add new entries to buildpath
			for (String path : paths) {
				IPath entry = new Path(path);
				IFolder folder = project.getFolder(entry);
				if (folder != null && BuildPathUtils.isContainedInBuildpath(entry, scriptProject) == false) {
					Logger.debug("Adding new dependency to buildpath " + folder.getFullPath());
					newEntries.add(DLTKCore.newSourceEntry(folder.getFullPath()));
				}
			}

			if (newEntries.size() > 0) {
				BuildPathUtils.addNonDupEntriesToBuildPath(scriptProject,
						newEntries);
			}

			// remove non existing entries from buildpath
			IBuildpathEntry[] rawBuildpath = scriptProject
					.getRawBuildpath();
			for (IBuildpathEntry entry : rawBuildpath) {
				if (entry.getEntryKind() != IBuildpathEntry.BPE_SOURCE) {
					continue;
				}

				IFolder folder = project.getFolder(entry.getPath().removeFirstSegments(1));
				if (folder == null || !folder.exists()) {
					Logger.debug("Removing non existing dependency from buildpath: " + entry.getPath().toString());
					BuildPathUtils.removeEntryFromBuildPath(scriptProject, entry);
				}
			}
			
		} catch (Exception e) {
			Logger.logException(e);
		}

		return null;
	}
}
