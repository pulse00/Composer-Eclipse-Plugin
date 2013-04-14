package com.dubture.composer.ui.wizard.importer;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class WizardResourceImportPage extends WizardDataTransferPage {

	private String path;
	protected String projectName;
	
	public WizardResourceImportPage(IWorkbench aWorkbench, IStructuredSelection selection, String[] strings) {
		super("Import composer project");
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
		
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(control);
		
		final Text text = new Text(control, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(text);
		text.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				projectName = text.getText();
				
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		
		Button selector = new Button(control, SWT.PUSH);
		selector.setText("Select");
		GridDataFactory.fillDefaults().span(1, 1).applyTo(selector);
		
		selector.addSelectionListener(new SelectionAdapter() {


			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
				dialog.setMessage("Select an existing composer project");
				path = dialog.open();
			}
		});
		
		setControl(control);
		
	}

	@Override
	protected boolean allowNewContainerName() {
		return false;
	}
	
}
