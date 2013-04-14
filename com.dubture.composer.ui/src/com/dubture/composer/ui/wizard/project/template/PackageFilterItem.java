package com.dubture.composer.ui.wizard.project.template;

import com.dubture.getcomposer.core.MinimalPackage;

/**
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
class PackageFilterItem {

	protected boolean isChecked;
	protected MinimalPackage item;
	protected String[] versions;
	protected String selectedVersion = null;
	
	public PackageFilterItem(MinimalPackage pkg) {
		isChecked = false;
		item = pkg;
	}
	
	public MinimalPackage getPackage() {
		return item;
	}
	
	public boolean isChecked() {
		return isChecked;
	}
	
	public void setChecked(boolean checked) {
		isChecked = checked;
	}

	public void setVersions(String[] versionInput) {
		versions = versionInput;
	}
	
	public String[] getVersions() {
		return versions;
	}

	public void setSelectedVersion(String text) {
		selectedVersion = text;
	}
	
	public String getSelectedVersion() {
		return selectedVersion;
	}
}