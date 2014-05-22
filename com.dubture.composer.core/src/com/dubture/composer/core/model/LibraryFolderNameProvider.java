package com.dubture.composer.core.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.php.core.libfolders.ILibraryFolderNameProvider;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.resources.IComposerProject;

public class LibraryFolderNameProvider implements ILibraryFolderNameProvider {

	@Override
	public String[] getLibraryFolderNames(IProject project) {
		IComposerProject prj = ComposerPlugin.getDefault().getComposerProject(project);
		return new String[]{prj.getVendorDir()};
	}

}
