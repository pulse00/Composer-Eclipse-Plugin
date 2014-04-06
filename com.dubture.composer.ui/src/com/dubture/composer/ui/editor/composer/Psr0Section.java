package com.dubture.composer.ui.editor.composer;

import org.eclipse.swt.widgets.Composite;

import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.getcomposer.core.collection.Psr;

public class Psr0Section extends PsrSection {

	public Psr0Section(ComposerFormPage page, Composite parent) {
		super(page, parent);
	}

	@Override
	protected Psr getPsr() {
		return composerPackage.getAutoload().getPsr0();
	}

	@Override
	protected String getPsrName() {
		return "psr-0";
	}

}
