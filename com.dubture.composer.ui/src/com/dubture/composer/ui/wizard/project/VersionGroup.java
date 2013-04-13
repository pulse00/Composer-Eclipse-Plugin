package com.dubture.composer.ui.wizard.project;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.environment.IEnvironmentUI;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.PHPInterpreterPreferencePage;
import org.eclipse.php.internal.ui.preferences.PHPVersionConfigurationBlock;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;

@SuppressWarnings("restriction")
public class VersionGroup extends Observable implements Observer, IStringButtonAdapter, IDialogFieldListener,
		SelectionListener {
	
	public PHPVersionConfigurationBlock fConfigurationBlock;
	
	private final AbstractWizardFirstPage composerProjectWizardFirstPage;
	private static final String DIALOGSTORE_LAST_EXTERNAL_LOC = DLTKUIPlugin.PLUGIN_ID + ".last.external.project"; //$NON-NLS-1$
	
	public VersionGroup(AbstractWizardFirstPage composerProjectWizardFirstPage, Composite composite) {
		this.composerProjectWizardFirstPage = composerProjectWizardFirstPage;
		final int numColumns = 3;
		final Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(this.composerProjectWizardFirstPage.initGridLayout(new GridLayout(numColumns, false), true));
		group.setText("");
		
		fConfigurationBlock = createConfigurationBlock(new IStatusChangeListener() {
			public void statusChanged(IStatus status) {
			}
		}, (IProject) null, null);
		fConfigurationBlock.createContents(group);
		fConfigurationBlock.setEnabled(true);
	}

	protected PHPVersionConfigurationBlock createConfigurationBlock(IStatusChangeListener listener,
			IProject project, IWorkbenchPreferenceContainer container) {
		return new PHPVersionConfigurationBlock(listener, project, container, true);
	}

	protected void fireEvent() {
		setChanged();
		notifyObservers();
	}

	@Override
	public void update(Observable o, Object arg) {
		fireEvent();
	}

	@Override	
	public void changeControlPressed(DialogField field) {
		IEnvironment environment = this.composerProjectWizardFirstPage.getEnvironment();
		IEnvironmentUI environmentUI = (IEnvironmentUI) environment.getAdapter(IEnvironmentUI.class);
		if (environmentUI != null) {
			String selectedDirectory = environmentUI.selectFolder(this.composerProjectWizardFirstPage.getShell());

			if (selectedDirectory != null) {
				// fLocation.setText(selectedDirectory);
				DLTKUIPlugin.getDefault().getDialogSettings().put(DIALOGSTORE_LAST_EXTERNAL_LOC, selectedDirectory);
			}
		}
	}

	@Override	
	public void dialogFieldChanged(DialogField field) {
		fireEvent();
	}

	@Override	
	public void widgetSelected(SelectionEvent e) {
		widgetDefaultSelected(e);
	}

	@SuppressWarnings("rawtypes")
	public void widgetDefaultSelected(SelectionEvent e) {
		String prefID = PHPInterpreterPreferencePage.PREF_ID;
		Map data = null;
		PreferencesUtil.createPreferenceDialogOn(this.composerProjectWizardFirstPage.getShell(), prefID, new String[] { prefID }, data).open();
	}

}