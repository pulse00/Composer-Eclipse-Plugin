package com.dubture.composer.ui.converter;

import org.eclipse.core.databinding.conversion.Converter;
import org.getcomposer.ComposerPackage;

public abstract class ComposerConverter extends Converter {

	protected ComposerPackage composerPackage;
	
	public ComposerConverter(Object fromType, Object toType) {
		super(fromType, toType);
	}

	/**
	 * @return the composerPackage
	 */
	public ComposerPackage getComposerPackage() {
		return composerPackage;
	}

	/**
	 * @param composerPackage the composerPackage to set
	 */
	public void setComposerPackage(ComposerPackage composerPackage) {
		this.composerPackage = composerPackage;
	}
}
