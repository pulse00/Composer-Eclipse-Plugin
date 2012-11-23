package com.dubture.composer.ui.converter;

import org.eclipse.core.databinding.conversion.Converter;
import org.getcomposer.collection.GenericArray;

public class String2KeywordsConverter extends Converter {

	public String2KeywordsConverter() {
		super(String.class, String[].class);
	}

	@Override
	public GenericArray convert(Object fromObject) {
		String[] chunks = ((String)fromObject).split(",");
		GenericArray keywords = new GenericArray();
		for (String chunk : chunks) {
			keywords.add(chunk.trim());
		}
		return keywords;
	}

}
