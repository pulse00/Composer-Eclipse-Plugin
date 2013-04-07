package com.dubture.composer.core.buildpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.php.internal.core.buildpath.BuildPathUtils;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.ComposerPluginConstants;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.resources.IComposerProject;

@SuppressWarnings("restriction")
public class BuildPathManager {

	private IComposerProject composerProject;
	private IPath[] exclusions;
	private IPath vendorPath;
	private IPath composerPath;
	
	public BuildPathManager(IComposerProject composerProject) {
		this.composerProject = composerProject;
		vendorPath = composerProject.getProject().getFullPath().append(composerProject.getVendorDir());
		composerPath = vendorPath.append("composer");
	}
	
	public void update() {
		try {
			IProject project = composerProject.getProject();
			IScriptProject scriptProject = composerProject.getScriptProject();
			BuildPathParser parser = new BuildPathParser(composerProject);
			List<String> paths = parser.getPaths();
			
			// project prefs
			IEclipsePreferences prefs = ComposerPlugin.getDefault().getProjectPreferences(project);

			try {
				String encoded = prefs.get(ComposerPluginConstants.BUILDPATH_INCLUDES_EXCLUDES, "");
				exclusions = scriptProject.decodeBuildpathEntry(encoded).getExclusionPatterns();
			} catch (Exception e) {
				exclusions = new IPath[]{};
			}
			
			// add includes
//			paths.addAll(Arrays.asList(PreferenceHelper.deserialize(prefs.get("buildpath.include", ""))));
			
			// remove excludes
//			paths.removeAll(Arrays.asList(PreferenceHelper.deserialize(prefs.get("buildpath.exclude", ""))));
			
			// Debug:
			Logger.debug("Paths to add:");
			for (String path : paths) {
				Logger.debug("> " + path);
			}
			
			// clean build path
			IBuildpathEntry[] rawBuildpath = scriptProject.getRawBuildpath();
			for (IBuildpathEntry entry : rawBuildpath) {
				if (entry.getEntryKind() != IBuildpathEntry.BPE_SOURCE) {
					continue;
				}

				BuildPathUtils.removeEntryFromBuildPath(scriptProject, entry);
			}
	
			// add new entries to buildpath
			List<IBuildpathEntry> newEntries = new ArrayList<IBuildpathEntry>();
			for (String path : paths) {
				IPath entry = new Path(path);
				IFolder folder = project.getFolder(entry);
				if (folder != null) {
					addPath(folder.getFullPath(), newEntries);
				}
			}
	
			if (newEntries.size() > 0) {
				BuildPathUtils.addNonDupEntriesToBuildPath(scriptProject, newEntries);
			}
		} catch (ModelException e) {
			Logger.logException(e);
		} 
	}
	
	private void addPath(IPath path, List<IBuildpathEntry> entries) {
		// find parent
		IBuildpathEntry parent = null;
		for (IBuildpathEntry entry : entries) {
			if (entry.getPath().isPrefixOf(path)) {
				parent = entry;
			}
		}
		
		// add exclusion to found parent
		if (parent != null) {
			List<IPath> exclusions = new ArrayList<IPath>(); 
			exclusions.addAll(Arrays.asList(parent.getExclusionPatterns()));
			IPath diff = path.removeFirstSegments(path.matchingFirstSegments(parent.getPath())).removeTrailingSeparator().addTrailingSeparator();
			exclusions.add(diff);
			
			entries.remove(parent);
			
			parent = DLTKCore.newSourceEntry(parent.getPath(), exclusions.toArray(new IPath[]{}));
			entries.add(parent);
		}
		
		// add own entry
		// add exclusions only to vendor folders, but not to vendor/composer
		if (vendorPath.isPrefixOf(path) && composerPath.isPrefixOf(path) == false) {
			entries.add(DLTKCore.newSourceEntry(path, exclusions));
		} else {
			entries.add(DLTKCore.newSourceEntry(path));			
		}
	}
}
