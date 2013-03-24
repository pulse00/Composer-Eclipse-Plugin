package com.dubture.composer.ui.preferences;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

public class ComposerPropertyPage extends PropertyPage {

	@Override
	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		return null;
	}

}
