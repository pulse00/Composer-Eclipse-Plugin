package com.dubture.composer.ui.converter;

import org.getcomposer.core.collection.License;

public class String2LicenseConverter extends ComposerConverter {
	
	public String2LicenseConverter() {
		super(String.class, License.class);
	}

	@Override
	public License convert(Object fromObject) {
		License license = composerPackage.getLicense();
		String[] chunks = ((String)fromObject).split(",");
		for (String chunk : chunks) {
			chunk = chunk.trim();
			if (!license.has(chunk)) {
				license.add(chunk);
			}
		}
		return license;
	}


}
