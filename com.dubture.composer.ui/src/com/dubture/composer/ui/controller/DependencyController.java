package com.dubture.composer.ui.controller;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.Viewer;

import com.dubture.getcomposer.core.MinimalPackage;
import com.dubture.getcomposer.core.VersionedPackage;
import com.dubture.getcomposer.core.collection.Dependencies;

public class DependencyController extends PackageController {

	private Dependencies deps;

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		deps = (Dependencies)newInput;
	}

	public Object[] getElements(Object inputElement) {
		return deps.toArray();
	}
	
	public void updateText(MinimalPackage pkg, StyledString styledString) {
		if (pkg instanceof VersionedPackage) {
			VersionedPackage vpkg = (VersionedPackage)pkg;

			super.updateText(pkg, styledString);
			
			if (vpkg.getVersion() != null && vpkg.getVersion().trim() != "" && !vpkg.getVersion().trim().isEmpty()) {
				styledString.append(" : " + vpkg.getVersion().trim(), StyledString.QUALIFIER_STYLER);
			}
		}
	}
}