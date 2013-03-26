package com.dubture.composer.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.php.internal.debug.ui.preferences.phps.PHPsPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.internal.WorkbenchMessages;

import com.dubture.composer.core.log.Logger;

@SuppressWarnings("restriction")
public class MissingExecutableDialog extends ErrorDialog {

	public MissingExecutableDialog(Shell parentShell, String dialogTitle, String message, IStatus status,
			int displayMask) {
		super(parentShell, dialogTitle, message, status, displayMask);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = (Composite) super.createDialogArea(parent);

		Composite space = new Composite(main, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		gridData.heightHint = 1;
		gridData.widthHint = 1;
		space.setLayoutData(gridData);
		Link link = createPreferencesLink(main);
		link.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		return main;
	}

	private Link createPreferencesLink(Composite parent) {
		Link link = new Link(parent, SWT.NONE);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					PreferencesUtil.createPreferenceDialogOn(getShell(), PHPsPreferencePage.ID, new String[] {}, null);
				} catch (Exception e2) {
					Logger.logException(e2);
				}
			}
		});
		link.setText(WorkbenchMessages.ErrorLogUtil_ShowErrorLogHyperlink);
		link.setToolTipText(WorkbenchMessages.ErrorLogUtil_ShowErrorLogTooltip);
		Dialog.applyDialogFont(link);

		return link;
	}
}
