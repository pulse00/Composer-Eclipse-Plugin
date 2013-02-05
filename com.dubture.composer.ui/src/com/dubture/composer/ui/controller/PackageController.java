package com.dubture.composer.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.getcomposer.MinimalPackage;

import com.dubture.composer.ui.ComposerUIPluginImages;

public class PackageController extends LabelProvider implements ITableController, ICheckStateProvider, ICheckStateListener {

	private List<MinimalPackage> packages;
	private List<String> checked = new ArrayList<String>();
	protected static Image pkgImage = ComposerUIPluginImages.PACKAGE.createImage();
	protected static Image phpImage = ComposerUIPluginImages.PHP.createImage();
	protected List<IPackageCheckStateChangedListener> pkgListeners = new ArrayList<IPackageCheckStateChangedListener>();

	public void addPackageCheckStateChangedListener(IPackageCheckStateChangedListener listener) {
		if (!pkgListeners.contains(listener)) {
			pkgListeners.add(listener);
		}
	}
	
	public void removePackageCheckStateChangedListener(IPackageCheckStateChangedListener listener) {
		pkgListeners.remove(listener);
	}
	
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
	
	public void addPackages(List<MinimalPackage> packages) {
		this.packages.addAll(packages);
	}
	
	public Image getImage(Object element) {
		return PackageController.getPackageImage(element);
	}
	
	public static Image getPackageImage(Object element) {
		String name = PackageController.getPackageName(element);
		if (name == "php") {
			return phpImage;
		}
		return pkgImage;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return getImage(element);
	}

	public String getText(Object element) {
		return getName(element);
	}
	
	public String getColumnText(Object element, int columnIndex) {
		return getText(element);
	}

	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		setChecked(((MinimalPackage)event.getElement()).getName(), event.getChecked());
	}
	
	public List<String> getCheckedPackages() {
		return checked;
	}
	
	public int getCheckedPackagesCount() {
		return checked.size();
	}
	
	public void clear() {
		checked.clear();
//		for (String name : checked) {
//			setChecked(name, false);
//		}
	}
	
	public void setChecked(String name, boolean checked) {
		if (checked && !this.checked.contains(name)) {
			this.checked.add(name);
		}
		
		if (!checked) {
			this.checked.remove(name);
		}
		
		for (IPackageCheckStateChangedListener listener : pkgListeners) {
			listener.packageCheckStateChanged(name, checked);
		}
	}
	
	private String getName(Object element) {
		return PackageController.getPackageName(element);
	}
	
	public static String getPackageName(Object element) {
		String name = null;
		if (element instanceof MinimalPackage) {
			name = ((MinimalPackage)element).getName();
		} else if (element instanceof String) {
			name = (String)element;
		}
		return name;
	}


	@Override
	public boolean isChecked(Object element) {
		return checked.contains(getName(element));
	}

	@Override
	public boolean isGrayed(Object element) {
		// TODO Auto-generated method stub
		return false;
	}
}
