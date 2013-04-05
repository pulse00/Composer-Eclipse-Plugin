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
		return keywords.toArray(new String[]{});
	}

	@Override
	protected Object finish() {
		return keywords;
	}

	@Override
	protected boolean has(String value) {
		return keywords.has(value);
	}

	@Override
	protected void add(String value) {
		keywords.add(value);
	}

	@Override
	protected void remove(String value) {
		keywords.remove(value);
	}

}
