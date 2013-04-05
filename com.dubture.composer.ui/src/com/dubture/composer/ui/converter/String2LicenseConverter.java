package com.dubture.composer.ui.converter;

import com.dubture.getcomposer.core.ComposerPackage;
import com.dubture.getcomposer.core.collection.License;

public class String2LicenseConverter extends String2ListConverter {
	
	private License license;
	
	public String2LicenseConverter() {
		super(String.class, License.class);
	}
	
	public String2LicenseConverter(ComposerPackage composerPackage) {
		this();
		setComposerPackage(composerPackage);
	}
	
	@Override
	protected void composerPackageUpdated() {
		license = composerPackage.getLicense();
	}

	@Override
	protected String[] start() {
		return license.toArray(new String[]{});
	}

	@Override
	protected Object finish() {
		return license;
	}

	@Override
	protected boolean has(String value) {
		return license.has(value);
	}

	@Override
	protected void add(String value) {
		license.add(value);
	}

	@Override
	protected void remove(String value) {
		license.remove(value);
	}


}
