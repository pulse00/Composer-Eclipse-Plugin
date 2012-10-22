package com.dubture.composer.ui.converter;

import org.eclipse.core.databinding.conversion.Converter;

public class String2KeywordsConverter extends Converter {

	public String2KeywordsConverter() {
		super(String.class, String[].class);
	}

	@Override
	public Object convert(Object fromObject) {
		String[] chunks = ((String)fromObject).split(",");
		for (String chunk : chunks) {
			chunk = chunk.trim();
		}
		return chunks;
	}

}
