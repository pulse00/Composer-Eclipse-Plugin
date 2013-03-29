package com.dubture.composer.core.buildpath;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.php.internal.core.buildpath.BuildPathUtils;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.resources.IComposerProject;

@SuppressWarnings("restriction")
public class BuildPathManager {

	private IComposerProject composerProject;
	
	public BuildPathManager(IComposerProject composerProject) {
		this.composerProject = composerProject;
	}
	
	public void update() {
		try {
			IProject project = composerProject.getProject();
			IScriptProject scriptProject = composerProject.getScriptProject();
			BuildPathParser parser = new BuildPathParser(composerProject);
			List<String> paths = parser.getPaths();
			List<IBuildpathEntry> newEntries = new ArrayList<IBuildpathEntry>();
	
			// add new entries to buildpath
			for (String path : paths) {
				IPath entry = new Path(path);
				IFolder folder = project.getFolder(entry);
				if (folder != null && !BuildPathUtils.isContainedInBuildpath(entry, scriptProject)) {
					Logger.debug("Adding new dependency to buildpath " + folder.getFullPath());
					newEntries.add(DLTKCore.newSourceEntry(folder.getFullPath()));
				}
			}
	
			if (newEntries.size() > 0) {
				BuildPathUtils.addNonDupEntriesToBuildPath(scriptProject, newEntries);
			}
	
			// remove non existing entries from buildpath
			IBuildpathEntry[] rawBuildpath = scriptProject.getRawBuildpath();
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
		} catch (ModelException e) {
			Logger.logException(e);
		} 
	}
}
