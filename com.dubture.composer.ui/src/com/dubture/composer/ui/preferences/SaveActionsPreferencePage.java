package com.dubture.composer.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.php.internal.ui.preferences.PropertyAndPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.ui.ComposerUIPlugin;

@SuppressWarnings("restriction")
public class SaveActionsPreferencePage extends PropertyAndPreferencePage {
	
	public static final String PREF_ID = "com.dubture.composer.ui.preferences.ComposerSaveActionsPreferencePage";
	public static final String PROP_ID = "com.dubture.composer.ui.propertyPages.ComposerSaveActionsPreferencePage";

	private SaveActionsConfigurationBlock configurationBlock;

	public SaveActionsPreferencePage() {
		setTitle("Save Actions");
		setDescription(null);
		setPreferenceStore(ComposerPlugin.getDefault().getPreferenceStore());

	}

	@Override
	public void createControl(Composite parent) {

		IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
		
		configurationBlock = new SaveActionsConfigurationBlock(getNewStatusChangedListener(), getProject(), container);
		super.createControl(parent);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return ComposerPlugin.getDefault().getPreferenceStore();
	}
	
	protected void enableProjectSpecificSettings(
			boolean useProjectSpecificSettings) {
		if (configurationBlock != null) {
			configurationBlock
					.useProjectSpecificSettings(useProjectSpecificSettings);
		}
		super.enableProjectSpecificSettings(useProjectSpecificSettings);
	}
	

	@Override
	protected void performDefaults() {
		super.performDefaults();
		if (configurationBlock != null) {
			configurationBlock.performDefaults();
		}
	}

	@Override
	public boolean performOk() {
		if (configurationBlock != null && !configurationBlock.performOk()) {
			return false;
		}
		return super.performOk();
	}
	
	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), ComposerUIPlugin.PLUGIN_ID + "." + "help_project_wizard_basic");
	}
	
	@Override
	protected Control createPreferenceContent(Composite composite) {
		return configurationBlock.createContents(composite);
	}
	
	@Override
	protected boolean hasProjectSpecificOptions(IProject project) {
		return configurationBlock.hasProjectSpecificOptions(project);
	}

	@Override
	protected String getPreferencePageID() {
		return PREF_ID;
	}

	@Override
	protected String getPropertyPageID() {
		return PROP_ID;
	}
	
	public void dispose() {
		if (configurationBlock != null) {
			configurationBlock.dispose();
		}
		super.dispose();
	}
}
