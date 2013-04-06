package com.dubture.composer.ui.converter;

import com.dubture.getcomposer.core.ComposerPackage;
import com.dubture.getcomposer.core.collection.JsonArray;

public class String2KeywordsConverter extends String2ListConverter {

	private JsonArray keywords;
	
	public String2KeywordsConverter() {
		super(String.class, JsonArray.class);
	}
	
	public String2KeywordsConverter(ComposerPackage composerPackage) {
		this();
		setComposerPackage(composerPackage);
	}
	
	@Override
	protected void composerPackageUpdated() {
		keywords = composerPackage.getKeywords();
	}

	@Override
	protected String[] start() {
		if (keywords == null) {
			return new String[]{};
		}
		
		return keywords.toArray(new String[]{});
	}

	@Override
	protected Object finish() {
		return keywords;
	}

	@Override
	protected boolean has(String value) {
		if (keywords == null) {
			return false;
		}
		
		return keywords.has(value);
	}

	@Override
	protected void add(String value) {
		if (keywords !=  null && !keywords.has(value)) {
			keywords.add(value);
		}
	}

	@Override
	protected void remove(String value) {
		if (keywords != null && keywords.has(value)) {
			keywords.remove(value);
		}
	}
}
