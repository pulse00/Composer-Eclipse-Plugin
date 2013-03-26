package com.dubture.composer.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.php.internal.ui.util.StatusInfo;
import org.eclipse.php.internal.ui.wizards.fields.DialogField;
import org.eclipse.php.internal.ui.wizards.fields.IDialogFieldListener;
import org.eclipse.php.internal.ui.wizards.fields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.preferences.CorePreferenceConstants.Keys;

@SuppressWarnings("restriction")
public class ComposerConfigurationBlock extends OptionsConfigurationBlock {

	private static final Key PHP_EXECUTABLE = getComposerCoreKey(Keys.PHP_EXECUTABLE);

	private IStatus fTaskTagsStatus;

	private StringDialogField exec;

	public ComposerConfigurationBlock(IStatusChangeListener newStatusChangedListener, IProject project,
			IWorkbenchPreferenceContainer container) {
		super(newStatusChangedListener, project, getKeys(), container);
	}

	@Override
	protected Control createContents(Composite parent) {
		
        setShell(parent.getShell());
        GridData data = new GridData();
        data.horizontalSpan = 1;

        org.eclipse.swt.graphics.Rectangle rect = parent.getMonitor().getClientArea();
        data.widthHint = rect.width / 4;

        Label header = new Label(parent, SWT.WRAP | SWT.BORDER);
        header.setText("Foobar");
        header.setLayoutData(data);
        Composite markersComposite = createMarkersTabContent(parent);
        validateSettings(null, null, null);

        return markersComposite;
	}
	
    private Composite createMarkersTabContent(Composite folder) {

        exec = new StringDialogField();
        
		Composite inner = new Composite(folder, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 3;
		inner.setLayout(layout);

		exec.doFillIntoGrid(inner, 3);
		exec.setDialogFieldListener(new IDialogFieldListener() {
			@Override
			public void dialogFieldChanged(DialogField field) {
				System.err.println("changed");
			}
		});
        

        return inner;
    }
	

	@Override
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		if (changedKey != null) {
			if (PHP_EXECUTABLE.equals(changedKey)) {
				fTaskTagsStatus = new StatusInfo();
			} else {
				return;
			}
		} else {
			fTaskTagsStatus = new StatusInfo();
		}

		fContext.statusChanged(fTaskTagsStatus);
	}

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return new String[] { "Full build required" };
	}

	private static Key[] getKeys() {
		return new Key[] { PHP_EXECUTABLE };
	}

	protected final static Key getComposerCoreKey(String key) {
		return getKey(ComposerPlugin.ID, key);
	}

}
