package com.dubture.composer.ui.dialogs;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchMessages;

import com.dubture.composer.core.util.StringUtil;

@SuppressWarnings("restriction")
public class ComposerJobFailureDialog extends ErrorDialog {

	public ComposerJobFailureDialog(String message, IStatus status) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Composer problem", message, status,
				IStatus.ERROR | IStatus.OK);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = (Composite) super.createDialogArea(parent);

			Composite space = new Composite(main, SWT.NONE);
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
			gridData.heightHint = 1;
			gridData.widthHint = 1;
			space.setLayoutData(gridData);
			
			Link link = createShowErrorLogLink(main);
			link.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		return main;
	}	
	
	protected Control createMessageArea(Composite composite) {
		// create composite
		// create image
		Image image = getImage();
		if (image != null) {
			imageLabel = new Label(composite, SWT.NULL);
			image.setBackground(imageLabel.getBackground());
			imageLabel.setImage(image);
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING)
					.applyTo(imageLabel);
		}
		// create message
		if (message != null) {
			messageLabel = new Label(composite, getMessageLabelStyle());
			messageLabel.setText("Composer exited with an error");
			GridDataFactory
					.fillDefaults()
					.align(SWT.FILL, SWT.BEGINNING)
					.grab(true, false)
					.hint(
							convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH),
							SWT.DEFAULT).applyTo(messageLabel);
		}
		return composite;
	}
	
	
	private Link createShowErrorLogLink(Composite parent) {
		Link link = new Link(parent, SWT.NONE);
		link.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(e.text));
				} catch (Exception ce) {
					ce.printStackTrace();
				}
			}
		});
		link.setText(StringUtil.replaceLinksInComposerMessage(message));
		link.setToolTipText(WorkbenchMessages.ErrorLogUtil_ShowErrorLogTooltip);
		Dialog.applyDialogFont(link);
		return link;
	}	
}
