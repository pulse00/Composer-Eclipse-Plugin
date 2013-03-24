package com.dubture.composer.ui.wizard.project;

import java.util.Observable;

import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class BasicSettingsGroup extends Observable implements IDialogFieldListener {

	protected final StringDialogField vendorField;
	private Shell shell;
	
	public BasicSettingsGroup(Composite composite, Shell shell) {
		this.shell = shell;
		final Composite nameComposite = new Composite(composite, SWT.NONE);
		nameComposite.setFont(composite.getFont());
		nameComposite.setLayout(new GridLayout(2, false));
		nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// text field for project vendor name
		vendorField = new StringDialogField();
		vendorField.setLabelText("Vendor name");
		vendorField.setDialogFieldListener(this);
		vendorField.doFillIntoGrid(nameComposite, 2);
		LayoutUtil.setHorizontalGrabbing(vendorField.getTextControl(null));
	}
	
	protected void fireEvent() {
		setChanged();
		notifyObservers();
	}

	public String getVendor() {
		return vendorField.getText().trim();
	}

	public void postSetFocus() {
		vendorField.postSetFocusOnDialogField(shell.getDisplay());
	}

	public void setVendor(String name) {
		vendorField.setText(name);
	}
	

	@Override
	public void dialogFieldChanged(DialogField field) {
		fireEvent();
	}
}
