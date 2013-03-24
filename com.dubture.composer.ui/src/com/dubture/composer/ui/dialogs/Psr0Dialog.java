package com.dubture.composer.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.dubture.composer.ui.ComposerUIPluginConstants;
import com.dubture.composer.ui.ComposerUIPluginImages;

public class Psr0Dialog extends Dialog {

	private Text namespaceControl;
	private Text pathsControl;
	
	private String namespace;
	private String paths;
	
	private boolean namespaceEnabled = true;
	private boolean pathsEnabled = true;
	
	public Psr0Dialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		getShell().setText("Edit psr-0 entry");
		getShell().setImage(ComposerUIPluginImages.EVENT.createImage());
		
		Composite contents = new Composite(parent, SWT.NONE);
		contents.setLayout(new GridLayout(2, false));
		
		Label lblEvent = new Label(contents, SWT.NONE);
		GridData gd_lblEvent = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblEvent.widthHint = ComposerUIPluginConstants.DIALOG_LABEL_WIDTH;
		lblEvent.setLayoutData(gd_lblEvent);
		lblEvent.setText("Namespace");
		
		namespaceControl = new Text(contents, SWT.BORDER);
		namespaceControl.setEnabled(pathsEnabled);
		GridData gd_eventControl = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_eventControl.widthHint = ComposerUIPluginConstants.DIALOG_CONTROL_WIDTH;
		namespaceControl.setLayoutData(gd_eventControl);
		
		namespaceControl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				namespace = namespaceControl.getText();
			}
		});
		
		if (namespace != null) {
			namespaceControl.setText(namespace);
		}
		
		Label lblHandler = new Label(contents, SWT.NONE);
		lblHandler.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblHandler.setText("Paths");
		
		pathsControl = new Text(contents, SWT.BORDER);
		pathsControl.setEnabled(namespaceEnabled);
		pathsControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		pathsControl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				paths = pathsControl.getText();
			}
		});
		
		if (paths != null) {
			pathsControl.setText(paths);
		}
		
		return contents;		
	}

	public String getNamespace() {
		if (namespace == null) {
			return "";
		}
		return namespace;
	}

	public String getPaths() {
		return paths;
	}

	public void setNamespace(String text) {
		this.namespace = text;
	}

	public void setPaths(String paths) {
		this.paths = paths;
	}
}
