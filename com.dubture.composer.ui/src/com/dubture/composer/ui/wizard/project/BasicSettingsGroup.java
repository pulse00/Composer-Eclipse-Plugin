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
	protected final StringDialogField typeField;
	protected final StringDialogField descriptionField;
	protected final StringDialogField keywordField;
	protected final StringDialogField licenseField;
	
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
		
		// text field for project type
		typeField = new StringDialogField();
		typeField.setLabelText("Type");
		typeField.setDialogFieldListener(this);
		typeField.doFillIntoGrid(nameComposite, 2);
		LayoutUtil.setHorizontalGrabbing(typeField.getTextControl(null));
		
		// text field for project description
		descriptionField = new StringDialogField();
		descriptionField.setLabelText("Description");
		descriptionField.setDialogFieldListener(this);
		descriptionField.doFillIntoGrid(nameComposite, 2);
		LayoutUtil.setHorizontalGrabbing(descriptionField.getTextControl(null));
		
		// text field for project description
		keywordField = new StringDialogField();
		keywordField.setLabelText("Keywords");
		keywordField.setDialogFieldListener(this);
		keywordField.doFillIntoGrid(nameComposite, 2);
		LayoutUtil.setHorizontalGrabbing(keywordField.getTextControl(null));
		
		
		// text field for project description
		licenseField = new StringDialogField();
		licenseField.setLabelText("License");
		licenseField.setDialogFieldListener(this);
		licenseField.doFillIntoGrid(nameComposite, 2);
		LayoutUtil.setHorizontalGrabbing(licenseField.getTextControl(null));
		
	}
	
	protected void fireEvent() {
		setChanged();
		notifyObservers();
	}

	public String getVendor() {
		return vendorField.getText().trim();
	}
	
	public String getDescription() {
		return descriptionField.getText().trim();
	}
	
	public String getLicense() {
		return licenseField.getText().trim();
	}
	
	public String getType() {
		return typeField.getText().trim();
	}
	
	public String getKeywords() {
		return keywordField.getText().trim();
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
