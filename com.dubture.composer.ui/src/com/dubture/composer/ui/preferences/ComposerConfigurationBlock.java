package com.dubture.composer.ui.preferences;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.SelectionButtonDialogFieldGroup;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.core.preferences.PHPexes;
import org.eclipse.php.internal.debug.ui.preferences.phps.PHPsPreferencePage;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.php.internal.ui.util.StatusInfo;
import org.eclipse.php.internal.ui.wizards.fields.ComboDialogField;
import org.eclipse.php.internal.ui.wizards.fields.DialogField;
import org.eclipse.php.internal.ui.wizards.fields.IDialogFieldListener;
import org.eclipse.php.internal.ui.wizards.fields.IStringButtonAdapter;
import org.eclipse.php.internal.ui.wizards.fields.StringButtonDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
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
public class ComposerConfigurationBlock extends OptionsConfigurationBlock implements IDialogFieldListener {

	private static final Key PHP_EXECUTABLE = getComposerCoreKey(Keys.PHP_EXECUTABLE);
	private static final Key COMPOSER_PHAR = getComposerCoreKey(Keys.COMPOSER_PHAR);
	private static final Key USE_PROJECT_PHAR = getComposerCoreKey(Keys.USE_PROJECT_PHAR);
	
	private ComboDialogField exes;
	private Button testButton;
	private PHPexes phpExes;
    private FontMetrics fontMetrics;	
	
	private SelectionButtonDialogFieldGroup buttonGroup;
	private StringButtonDialogField pharField;


	public ComposerConfigurationBlock(IStatusChangeListener context, IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	@Override
	protected Control createContents(Composite parent) {
		
        setShell(parent.getShell());
        GridData data = new GridData();
        data.horizontalSpan = 1;

        org.eclipse.swt.graphics.Rectangle rect = parent.getMonitor().getClientArea();
        data.widthHint = rect.width / 4;

        GC gc = new GC(parent);
        gc.setFont(JFaceResources.getDialogFont());
        fontMetrics = gc.getFontMetrics();
        gc.dispose();
        
        Label header = new Label(parent, SWT.WRAP | SWT.BORDER);
        header.setText("Select the PHP executable to be used for running composer binaries.");
        header.setLayoutData(data);
        Composite markersComposite = createInnerContent(parent);
        validateSettings(null, null, null);

        return markersComposite;
	}
	
    private Composite createInnerContent(Composite folder) {
    	
		Composite result = new Composite(folder, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = 0;
		layout.verticalSpacing = convertVerticalDLUsToPixels(10);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.numColumns = 2;
		result.setLayout(layout);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;

		Group sourceFolderGroup = new Group(result, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3 ;
		sourceFolderGroup.setLayout(layout);
		sourceFolderGroup.setLayoutData(gd);
		sourceFolderGroup.setText("PHP executable");

		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		Link prefLink = new Link(sourceFolderGroup, SWT.WRAP);
		prefLink.setText("You can add PHP binaries in the <a>PHP Executables </a> preference page.");
		prefLink.setLayoutData(gridData);
		
		prefLink.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PreferencesUtil.createPreferenceDialogOn(getShell(), PHPsPreferencePage.ID, new String[]{}, null);
			};
		});
		
		Link helpLink = new Link(sourceFolderGroup, SWT.WRAP);
		helpLink.setLayoutData(gridData);
		helpLink.setText("See <a>phptherightway.com</a> if you need help installing the PHP cli.");
		helpLink.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL("http://www.phptherightway.com/#getting_started"));
				} catch (Exception e1) {
					Logger.logException(e1);
				}
			};
		});
		
		
		exes = new ComboDialogField(SWT.READ_ONLY);
		exes.setLabelText("PHP executable");
		int numColumns = 2;
		exes.doFillIntoGrid(sourceFolderGroup, numColumns);
		exes.setDialogFieldListener(this);
		
		createTestButton(sourceFolderGroup);
		
		Group pharGroup = new Group(result, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		pharGroup.setLayout(layout);
		pharGroup.setLayoutData(gd);
		pharGroup.setText("Composer binary");
    	
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		
		buttonGroup = new SelectionButtonDialogFieldGroup(SWT.RADIO, new String[]{"Download composer per project", "Use global composer"},  2);
		buttonGroup.setLabelText("Composer selection");
		buttonGroup.doFillIntoGrid(pharGroup, numColumns);
		buttonGroup.setDialogFieldListener(new org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener() {
			@Override
			public void dialogFieldChanged(org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField field) {
				pharField.setEnabled(buttonGroup.isSelected(1));
			}
		});
		
		pharField = new StringButtonDialogField(new IStringButtonAdapter() {
			@Override
			public void changeControlPressed(DialogField field) {
				FileDialog dialog = new FileDialog(getShell());
				String path = dialog.open();
				if (path != null) {
					pharField.setText(path);
				}
			}
		});
		
		pharField.setButtonLabel("Browse");
		
		boolean useProjectPhar = getBooleanValue(USE_PROJECT_PHAR);
		
		if (useProjectPhar) {
			pharField.setEnabled(false);
			buttonGroup.setSelection(0, true);
			buttonGroup.setSelection(1, false);
		} else {
			buttonGroup.setSelection(0, false);
			buttonGroup.setSelection(1, true);
		}
		
		pharField.setDialogFieldListener(this);
		pharField.setLabelText("Custom composer binary");
		pharField.doFillIntoGrid(pharGroup, 3);
		
		loadExecutables();
		loadPhar();
		
    	if (phpExes.getAllItems().length == 0) {
    		testButton.setEnabled(false);
    	}
		
        return result;
    }
    
    private void loadPhar() {
    	
    	String phar = getValue(COMPOSER_PHAR);
    	if (phar == null) {
    		return;
    	}
    	
    	pharField.setText(phar);
	}

	protected void loadExecutables() {

		phpExes = PHPexes.getInstance();
		String current = getValue(PHP_EXECUTABLE);
		List<String> items = new ArrayList<String>();
		
		int i=0;
		int select = -1;
		for (PHPexeItem item : phpExes.getAllItems()) {
			if (item.isDefault() && (current == null || current.length() == 0) || item.getExecutable().toString().equals(current)) {
				select = i;
			}
			i++;
			items.add(item.getName());
		}
		
		exes.setItems(items.toArray(new String[items.size()]));
		
		if (select > -1) {
			exes.selectItem(select);
		}
    }
    
    protected void createTestButton(Composite parent) {
    	
    	GridData gd = new GridData();
    	gd.horizontalSpan = 1;
    	testButton = new Button(parent, SWT.PUSH);
    	testButton.setLayoutData(gd);
    	
    	testButton.setText("Test selected PHP executable");
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
		
		StatusInfo status = new StatusInfo();
		
		if( phpExes.getAllItems().length == 0 ) {
			status = new StatusInfo(StatusInfo.WARNING, "No PHP executable configured. Composer dependencies cannot be managed properly.");
		}
		
		if (buttonGroup.isSelected(1)) {
			File file = new File(pharField.getText());
			if (!file.exists() || !file.canExecute()) {
				status = new StatusInfo(StatusInfo.WARNING, "The selected file is not a valid composer archive.");
			}
		}

		fContext.statusChanged(status);
	}

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}

	private static Key[] getKeys() {
		return new Key[] { PHP_EXECUTABLE, COMPOSER_PHAR, USE_PROJECT_PHAR };
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
		setValue(COMPOSER_PHAR, pharField.getText());
		setValue(USE_PROJECT_PHAR, buttonGroup.isSelected(0));
	}

	@Override
	public void dialogFieldChanged(DialogField field) {
		validateSettings(null, null, null);		
	}
	
    protected int convertVerticalDLUsToPixels(int dlus) {
        // test for failure to initialize for backward compatibility
        if (fontMetrics == null) {
			return 0;
		}
        return Dialog.convertVerticalDLUsToPixels(fontMetrics, dlus);
    }
    
    protected int convertHorizontalDLUsToPixels(int dlus) {
        // test for failure to initialize for backward compatibility
        if (fontMetrics == null) {
			return 0;
		}
        return Dialog.convertHorizontalDLUsToPixels(fontMetrics, dlus);
    }
}
