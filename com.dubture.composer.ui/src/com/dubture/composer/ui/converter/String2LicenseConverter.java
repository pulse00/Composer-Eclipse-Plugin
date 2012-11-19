package com.dubture.composer.ui.converter;

import org.eclipse.core.databinding.conversion.Converter;
import org.getcomposer.collection.License;

public class String2LicenseConverter extends Converter {

	public String2LicenseConverter() {
		super(String.class, String[].class);
	}

	@Override
	public Object convert(Object fromObject) {
		License license = new License();
		String[] chunks = ((String)fromObject).split(",");
		for (String chunk : chunks) {
			license.add(chunk.trim());
		}
		return license;
	}

}
