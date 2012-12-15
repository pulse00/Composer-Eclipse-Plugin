package com.dubture.composer.ui.controller;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.getcomposer.ComposerPackage;
import org.getcomposer.MinimalPackage;

import com.dubture.composer.ui.ComposerUIPluginImages;

public class PackageController extends LabelProvider implements ITableController {

	private List<MinimalPackage> packages;
	protected Image pkgImage = ComposerUIPluginImages.PACKAGE.createImage();
	protected Image phpImage = ComposerUIPluginImages.PHP.createImage();

	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput == null) {
			packages = null;
		} else {
			packages = (List<MinimalPackage>)newInput;
		}
	}

	public Object[] getElements(Object inputElement) {
		if (packages != null) {
			return packages.toArray();
		} else {
			return null;
		}
	}
	
	public void addPackages(List<ComposerPackage> packages) {
		this.packages.addAll(packages);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		MinimalPackage pkg = (MinimalPackage)element;
		if (pkg.getName() == "php") {
			return phpImage;
		}
		return pkgImage;
	}

	public String getColumnText(Object element, int columnIndex) {
		MinimalPackage pkg = (MinimalPackage)element;
		return pkg.getName();
	}
}
