package com.dubture.composer.ui.converter;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.conversion.Converter;
import org.getcomposer.collection.License;

public class License2StringConverter extends Converter {

	public License2StringConverter() {
		super(String[].class, String.class);
	}

	@Override
	public String convert(Object fromObject) {
		ArrayList<String> list = new ArrayList<String>();
		License licenses = (License)fromObject;
		for (String license : licenses) {
			list.add(license);
		}
		return StringUtils.join(list.toArray(), ", ");
	}

}
