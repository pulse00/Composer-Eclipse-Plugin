package com.dubture.composer.ui.converter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.conversion.Converter;

import com.dubture.getcomposer.core.collection.JsonArray;

public class Keywords2StringConverter extends Converter {

	public Keywords2StringConverter() {
		super(JsonArray.class, String.class);
	}

	@Override
	public String convert(Object fromObject) {
		if (fromObject == null) {
			return "";
		}
		JsonArray keywords = (JsonArray)fromObject;
		return StringUtils.join((String[])keywords.toArray(new String[]{}), ", ");
	}

}
