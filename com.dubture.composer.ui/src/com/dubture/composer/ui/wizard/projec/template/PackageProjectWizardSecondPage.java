package com.dubture.composer.ui.wizard.projec.template;

import java.util.Observable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;
import com.dubture.composer.ui.wizard.AbstractWizardSecondPage;

public class PackageProjectWizardSecondPage extends AbstractWizardSecondPage {

	public PackageProjectWizardSecondPage(AbstractWizardFirstPage mainPage, String title) {
		super(mainPage, title);
	}
	
	@Override
	public void createControl(Composite parent) {

		int numColumns = 1;
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout(numColumns, false));
		
		Dialog.applyDialogFont(composite);
		setControl(composite);
	}
	

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getPageTitle() {
		return "Setup it up";
	}

	@Override
	protected String getPageDescription() {
		return "da description";
	}

	@Override
	protected void finishPage(IProgressMonitor monitor) throws Exception {
		
	}
}
