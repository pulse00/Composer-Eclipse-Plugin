package com.dubture.composer.ui.controller;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import com.dubture.composer.ui.ComposerUIPluginImages;

public class PackageController extends LabelProvider implements ITableController {

	private String[] packages;
	protected Image pkgImage = ComposerUIPluginImages.PACKAGE.createImage();

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		packages = (String[])newInput;
	}

	public Object[] getElements(Object inputElement) {
		return packages;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return pkgImage;
	}

	public String getColumnText(Object element, int columnIndex) {
		return (String)element;
	}
}
