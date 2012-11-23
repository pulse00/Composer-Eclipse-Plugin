package com.dubture.composer.ui.converter;

import org.eclipse.core.databinding.conversion.Converter;
import org.getcomposer.ComposerPackage;
import org.getcomposer.collection.License;

public class String2LicenseConverter extends Converter {

	private ComposerPackage composerPackage;
	
	public String2LicenseConverter() {
		super(String.class, License.class);
	}

	@Override
	public License convert(Object fromObject) {
		License license = composerPackage.getLicense();
		String[] chunks = ((String)fromObject).split(",");
		for (String chunk : chunks) {
			license.add(chunk.trim());
		}
		return license;
	}

	/**
	 * @return the composerPackage
	 */
	public ComposerPackage getComposerPackage() {
		return composerPackage;
	}

	/**
	 * @param composerPackage the composerPackage to set
	 */
	public void setComposerPackage(ComposerPackage composerPackage) {
		this.composerPackage = composerPackage;
	}

}
