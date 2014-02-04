package com.dubture.composer.ui.pex;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IScriptFolder;
import org.pdtextensions.core.ui.extension.INamespaceResolver;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.resources.IComposerProject;

public class NamespaceResolver implements INamespaceResolver {

	@Override
	public String resolve(IScriptFolder container) {
		
		IComposerProject project = ComposerPlugin.getDefault().getComposerProject(container.getScriptProject());
		
		IPath path = container.getPath().makeRelativeTo(project.getFullPath());
		
		return project.getNamespace(path);
	}
}
