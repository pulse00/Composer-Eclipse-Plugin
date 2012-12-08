package com.dubture.composer.ui.converter;

import org.getcomposer.collection.GenericArray;

public class String2KeywordsConverter extends ComposerConverter {

	public String2KeywordsConverter() {
		super(String.class, String[].class);
	}

	@Override
	public GenericArray convert(Object fromObject) {
		GenericArray keywords = composerPackage.getKeywords();
		String[] chunks = ((String)fromObject).split(",");

		for (String chunk : chunks) {
			chunk = chunk.trim();
			if (!keywords.has(chunk)) {
				keywords.add(chunk);
			}
		}
		return keywords;
	}

}
