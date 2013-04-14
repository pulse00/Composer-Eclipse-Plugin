package com.dubture.composer.ui.wizard.importer;

import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class WizardResourceImportPage extends WizardDataTransferPage {

	private String path;
	protected String projectName;
	private StringButtonDialogField locationPath;
	
	public WizardResourceImportPage(IWorkbench aWorkbench, IStructuredSelection selection, String[] strings) {
		super("Import composer project");
		setTitle("Import an existing composer project");
		setDescription("Importing an existing composer project will automatically setup your project.");
	}

	@Override
	public void handleEvent(Event event) {
		
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public String getPath() {
		return path;
	}

	@Override
	public void createControl(Composite parent) {

		Composite control = new Composite(parent, SWT.NONE);
		
		int numColumns = 3;
		GridLayoutFactory.fillDefaults().numColumns(numColumns).applyTo(control);
		
		final StringDialogField text = new StringDialogField();
		text.setLabelText("Project name");
		text.doFillIntoGrid(control, numColumns);
		LayoutUtil.setHorizontalGrabbing(text.getTextControl(null));
		
		text.setDialogFieldListener(new IDialogFieldListener() {
			@Override
			public void dialogFieldChanged(DialogField field) {
				projectName = text.getText();
				updatePageCompletion();
			}
		});
		

		locationPath = new StringButtonDialogField(new IStringButtonAdapter() {
			@Override
			public void changeControlPressed(DialogField field) {
				DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
				dialog.setMessage("Select an existing composer project");
				path = dialog.open();
				if (path != null) {
					locationPath.setText(path);
				}
				updatePageCompletion();
			}
		});
		
		locationPath.setLabelText("Path");
		locationPath.setButtonLabel("Browse");
		locationPath.doFillIntoGrid(control, numColumns);
		
		locationPath.getTextControl(null).setEnabled(false);
		
		
		LayoutUtil.setHorizontalGrabbing(locationPath.getTextControl(null));
		
		setControl(control);
		
		updatePageCompletion();
		
	}
	
	
	@Override
	protected boolean validateSourceGroup() {
		
		// TODO: validate
		return false;
	}

	@Override
	protected boolean allowNewContainerName() {
		return false;
	}
	
}
