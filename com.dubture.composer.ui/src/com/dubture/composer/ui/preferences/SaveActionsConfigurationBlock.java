package com.dubture.composer.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.ComposerPreferenceConstants;

@SuppressWarnings("restriction")
public class SaveActionsConfigurationBlock extends OptionsConfigurationBlock {

	private static final Key buildpathKey = new Key(ComposerPlugin.ID, ComposerPreferenceConstants.SAVEACTION_BUILDPATH); 
	private static final Key updateKey = new Key(ComposerPlugin.ID, ComposerPreferenceConstants.SAVEACTION_UPDATE);
	
	public SaveActionsConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {
		
		super(context, project, getKeys(), container);
	}

	private static Key[] getKeys() {
		return new Key[] { buildpathKey, updateKey };
	}

	@Override
	protected Control createContents(Composite parent) {
		
		addCheckBox(parent, "Update Buildpath", buildpathKey, new String[] {"True", "False"}, 0);
		addCheckBox(parent, "Run Composer Update", updateKey, new String[] {"True", "False"}, 0);
		
		return parent;
	}
	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}

	@Override
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
	}

}
