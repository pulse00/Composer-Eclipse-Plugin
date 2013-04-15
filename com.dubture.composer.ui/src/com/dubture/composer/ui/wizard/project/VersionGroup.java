package com.dubture.composer.ui.wizard.project;

import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.swt.widgets.Composite;

import com.dubture.composer.ui.wizard.AbstractVersionGroup;
import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;

/**
 * @author Robert Gruendler <r.gruendler@gmail.com>
 */
@SuppressWarnings("restriction")
public class VersionGroup extends AbstractVersionGroup {
	public VersionGroup(AbstractWizardFirstPage composerProjectWizardFirstPage, Composite composite) {
		super(composerProjectWizardFirstPage, composite, 3, PHPVersion.PHP5_3);
	}
}