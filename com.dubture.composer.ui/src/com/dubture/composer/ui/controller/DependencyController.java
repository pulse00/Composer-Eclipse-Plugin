package com.dubture.composer.ui.controller;

import org.eclipse.jface.viewers.Viewer;
import org.getcomposer.VersionedPackage;
import org.getcomposer.collection.Dependencies;

public class DependencyController extends PackageController {

	private Dependencies deps;

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		deps = (Dependencies)newInput;
	}

	public Object[] getElements(Object inputElement) {
		return deps.toArray();
	}

	public String getColumnText(Object element, int columnIndex) {
	VersionedPackage dep = (VersionedPackage)element;
		StringBuilder sb = new StringBuilder();
		sb.append(dep.getName());
		sb.append(": ");
		
		// TODO: would be cool to have this in a decorator with hmm grey? text color
		sb.append(dep.getVersion());
		
		return sb.toString();
	}
}