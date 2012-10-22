package com.dubture.composer.ui.converter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.conversion.Converter;

public class Keywords2StringConverter extends Converter {

	public Keywords2StringConverter() {
		super(String[].class, String.class);
	}

	@Override
	public Object convert(Object fromObject) {
		return StringUtils.join((String[])fromObject, ",");
	}

}
