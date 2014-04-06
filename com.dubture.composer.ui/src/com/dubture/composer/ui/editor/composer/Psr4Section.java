package com.dubture.composer.ui.editor.composer;

import org.eclipse.swt.widgets.Composite;

import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.getcomposer.core.collection.Psr;


public class Psr4Section extends PsrSection {

	public Psr4Section(ComposerFormPage page, Composite parent) {
		super(page, parent);
	}

	@Override
	protected Psr getPsr() {
		return composerPackage.getAutoload().getPsr4();
	}

	@Override
	protected String getPsrName() {
		return "psr-4";
	}

}
