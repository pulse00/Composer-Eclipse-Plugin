package com.dubture.composer.ui.preferences;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.core.preferences.PHPexes;
import org.eclipse.php.internal.debug.ui.preferences.phps.PHPsPreferencePage;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.php.internal.ui.util.StatusInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.launch.execution.ExecutionResponseAdapter;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.preferences.CorePreferenceConstants.Keys;

@SuppressWarnings("restriction")
public class ComposerConfigurationBlock extends OptionsConfigurationBlock {

	private static final Key PHP_EXECUTABLE = getComposerCoreKey(Keys.PHP_EXECUTABLE);
	private IStatus fTaskTagsStatus;
	private Combo exes;
	
	private Button testButton;
	private PHPexes phpExes;

	public ComposerConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	@Override
	protected Control createContents(Composite parent) {
		
        setShell(parent.getShell());
        GridData data = new GridData();
        data.horizontalSpan = 1;

        org.eclipse.swt.graphics.Rectangle rect = parent.getMonitor().getClientArea();
        data.widthHint = rect.width / 4;

        Label header = new Label(parent, SWT.WRAP | SWT.BORDER);
        header.setText("Select the php binary to be used for executing composer phars.");
        header.setLayoutData(data);
        Composite markersComposite = createInnerContent(parent);
        validateSettings(null, null, null);

        return markersComposite;
	}
	
    private Composite createInnerContent(Composite folder) {
    	
		Composite inner = new Composite(folder, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		inner.setLayout(layout);
		
		Link prefLink = new Link(inner, SWT.WRAP);
		prefLink.setText("You can add php binaries in the <a>PHP Executables </a> preference page.");
		prefLink.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PreferencesUtil.createPreferenceDialogOn(getShell(), PHPsPreferencePage.ID, new String[]{}, null);
			};
		});
		
		Link helpLink = new Link(inner, SWT.WRAP);
		helpLink.setText("If you don't know where to find your executable or need help installing PHP on your system,\nsee <a>http://www.phptherightway.com/#getting_started</a> for details.");
		helpLink.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(e.text));
				} catch (Exception e1) {
					Logger.logException(e1);
				}
			};
		});
		
		
		exes = new Combo(inner, SWT.READ_ONLY);
		exes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setValue(PHP_EXECUTABLE, exes.getText());
			}
		});
		
		loadExecutables();
		createTestButton(inner);
		
        return inner;
    }
    
    protected void loadExecutables() {

		phpExes = PHPexes.getInstance();
		String current = getValue(PHP_EXECUTABLE);
		List<String> items = new ArrayList<String>();
		
		int i=0;
		int select = -1;
		for (PHPexeItem item : phpExes.getAllItems()) {
			if (item.isDefault() && (current == null || current.length() == 0) || item.getName().equals(current)) {
				select = i;
			}
			i++;
			items.add(item.getName());
		}
		
		exes.setItems(items.toArray(new String[items.size()]));
		
		if (select > -1) {
			exes.select(select);
		}
		
    }
    
    protected void createTestButton(Composite parent) {
    	
    	testButton = new Button(parent, SWT.PUSH);
    	testButton.setText("Test selected executable");
    	testButton.addSelectionListener(new SelectionAdapter() {
    		
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    			try {
    				String current = exes.getText();
    				PHPexeItem phPexeItem = null;
    				
    				for(PHPexeItem i : phpExes.getAllItems()) {
    					if (current.equals(i.getName())) {
    						phPexeItem = i;
    						break;
    					}
    				}
	    			
	    			if (phPexeItem == null) {
	    				Logger.log(Logger.WARNING, "No executable selected");
	    				return;
	    			}
	    			
					ExecutionResponseAdapter adapter = new ExecutionResponseAdapter() {
						public void executionFailed(final String response, Exception exception) {
							getShell().getDisplay().asyncExec(new Runnable() {
								@Override
								public void run() {
									String message = "PHP binary execution failed.";
									if (response != null && response.length() > 0) {
										message += " Reason: " + response;
									} else {
									}
									MessageDialog.openInformation(getShell(), "Execution test", message);									
								}
							});
						};
						public void executionFinished(final String response, int exitValue) {
							getShell().getDisplay().asyncExec(new Runnable() {
								@Override
								public void run() {
									String message = "PHP binary executed successfully.";
									if (response != null && response.length() > 0) {
										message += " Detected PHP version: " + response;
									} else {
										message += " Unable to determine PHP version.";
									}
									MessageDialog.openInformation(getShell(), "Execution test", message);									
								}
							});
						};
					};
					
					new Thread(new ExecutableTester(phPexeItem, adapter)).run();
					
				} catch (Exception ex) {
					Logger.logException(ex);
				}
    		}
		});
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
		return null;
	}

	private static Key[] getKeys() {
		return new Key[] { PHP_EXECUTABLE };
	}

	protected final static Key getComposerCoreKey(String key) {
		return getKey(ComposerPlugin.ID, key);
	}
	
	@Override
	public boolean performApply() {
		saveExecutable();
		return super.performApply();
	}
	
	@Override
	public boolean performOk() {
		saveExecutable();
		return super.performOk();
	}
	
	protected void saveExecutable() {
		
		String selected = exes.getText();
		String executable = null;
		
		for (PHPexeItem exe : phpExes.getAllItems()) {
			if (exe.getName().equals(selected)) {
				executable = exe.getExecutable().getAbsolutePath();
			}
		}

		setValue(PHP_EXECUTABLE, executable);
	}
}
