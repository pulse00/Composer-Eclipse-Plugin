package com.dubture.composer.ui.controller;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.getcomposer.collection.Dependencies;
import org.getcomposer.entities.Dependency;

import com.dubture.composer.ui.ComposerUIPluginImages;

public class DependencyController extends PackageController {

	private Dependencies deps;
	private Image phpImage = ComposerUIPluginImages.PHP.createImage();

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		deps = (Dependencies)newInput;
	}

	public Object[] getElements(Object inputElement) {
		return deps.toArray();
	}

	public Image getColumnImage(Object element, int columnIndex) {
		Dependency dep = (Dependency)element;
		if (dep.getName() == "php") {
			return phpImage;
		}
		return pkgImage;
	}

	public String getColumnText(Object element, int columnIndex) {
		Dependency dep = (Dependency)element;
		StringBuilder sb = new StringBuilder();
		sb.append(dep.getName());
		sb.append(": ");
		
		// TODO: would be cool to have this in a decorator with hmm grey? text color
		sb.append(dep.getVersion());
		
		return sb.toString();
	}
}