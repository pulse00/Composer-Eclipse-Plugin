package com.dubture.composer.ui.wizard.project;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class ProjectTemplateGroup extends Observable implements Observer, IStringButtonAdapter, IDialogFieldListener  {

	protected SelectionButtonDialogField emptyProject;
	protected SelectionButtonDialogField projectFromTemplate;
	protected final StringButtonDialogField projectName;
	protected Shell shell;
	
	public ProjectTemplateGroup(Composite composite, Shell shell) {
		
		this.shell = shell;
		final int numColumns = 3;
		
		final Group group = new Group(composite, SWT.None);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(numColumns, false));
		group.setText("Project type");
		
		emptyProject = new SelectionButtonDialogField(SWT.RADIO);
		emptyProject.setDialogFieldListener(this);
		emptyProject.setLabelText("Create empty project");
		projectFromTemplate = new SelectionButtonDialogField(SWT.RADIO);
		projectFromTemplate.setDialogFieldListener(this);
		projectFromTemplate.setLabelText("Create new project from existing package");
		
		projectName = new StringButtonDialogField(this);
		projectName.setDialogFieldListener(this);
		projectName.setLabelText("Package name");
		projectName.setButtonLabel("Package name");
		
		emptyProject.doFillIntoGrid(group, numColumns);
		projectFromTemplate.doFillIntoGrid(group, numColumns);
		projectName.doFillIntoGrid(group, numColumns);
		
		emptyProject.setSelection(true);
		projectFromTemplate.setSelection(false);
		
		projectName.getChangeControl(null).setVisible(false);
		projectName.setEnabled(false);
		
	}
	
	@Override
	public void update(Observable observable, Object object) {
	}

	@Override
	public void dialogFieldChanged(DialogField field) {
		
		if (field == emptyProject) {
			if (emptyProject.isSelected()) {
				projectName.setEnabled(false);
			} else {
				projectName.setEnabled(true);
			}
		}

		fireEvent();
	}

	@Override
	public void changeControlPressed(DialogField field) {
		
	}
	
	protected void fireEvent() {
		setChanged();
		notifyObservers();
	}
	
	public boolean installFromTemplate() {
		return projectFromTemplate != null && projectFromTemplate.isSelected();
	}
}
