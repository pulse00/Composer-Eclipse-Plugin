package com.dubture.composer.ui.converter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.conversion.Converter;
import org.getcomposer.core.collection.License;

public class License2StringConverter extends Converter {

	public License2StringConverter() {
		super(String[].class, String.class);
	}

	@Override
	public String convert(Object fromObject) {
		if (fromObject == null) {
			return "";
		}
		License licenses = (License)fromObject;
		return StringUtils.join((String[])licenses.toArray(new String[]{}), ", ");
	}

}
