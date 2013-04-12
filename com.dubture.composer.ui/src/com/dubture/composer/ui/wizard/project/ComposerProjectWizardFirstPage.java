package com.dubture.composer.ui.wizard.project;

import java.io.File;
import java.net.URI;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.ui.wizards.CompositeData;
import org.eclipse.php.internal.ui.wizards.DetectGroup;
import org.eclipse.php.internal.ui.wizards.IPHPProjectCreateWizardPage;
import org.eclipse.php.internal.ui.wizards.LocationGroup;
import org.eclipse.php.internal.ui.wizards.NameGroup;
import org.eclipse.php.internal.ui.wizards.WizardFragment;
import org.eclipse.php.internal.ui.wizards.WizardModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.ui.ComposerUIPlugin;
import com.dubture.composer.ui.converter.String2KeywordsConverter;
import com.dubture.getcomposer.core.ComposerPackage;

@SuppressWarnings("restriction")
public class ComposerProjectWizardFirstPage extends WizardPage implements IPHPProjectCreateWizardPage, Observer {

	public NameGroup nameGroup;
	public BasicSettingsGroup settingsGroup;
	public LocationGroup PHPLocationGroup;
	public VersionGroup versionGroup;
	
	protected String fInitialName;
	protected WizardFragment fragment;
	protected ComposerPackage composerPackage;
	protected String2KeywordsConverter keywordConverter;
	
	protected DetectGroup detectGroup;
	protected Validator pdtValidator;

	public ComposerProjectWizardFirstPage() {
		super("Basic Composer Configuration");
		setPageComplete(false);
		setTitle("Basic Composer Configuration");
		setDescription("Setup your new composer project");
	}
	
	

	@Override
	public void createControl(Composite parent) {

		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), false));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		fInitialName = "";
		// create UI elements
		nameGroup = new NameGroup(composite, fInitialName, getShell());
		settingsGroup = new BasicSettingsGroup(composite, getShell());
		
		nameGroup.addObserver(this);
		settingsGroup.addObserver(this);
		
		PHPLocationGroup = new LocationGroup(composite, nameGroup, getShell());
		
		CompositeData data = new CompositeData();
		data.setParetnt(composite);
		data.setSettings(getDialogSettings());
		data.setObserver(PHPLocationGroup);

		fragment = (WizardFragment) Platform.getAdapterManager().loadAdapter(data,
				ComposerProjectWizardFirstPage.class.getName());

		versionGroup = new VersionGroup(this, composite);
		detectGroup = new DetectGroup(composite, PHPLocationGroup, nameGroup);

		nameGroup.addObserver(PHPLocationGroup);

		PHPLocationGroup.addObserver(detectGroup);
		// initialize all elements
		nameGroup.notifyObservers();
		// create and connect validator
		pdtValidator = new Validator(this);

		nameGroup.addObserver(pdtValidator);
		settingsGroup.addObserver(pdtValidator);
		PHPLocationGroup.addObserver(pdtValidator);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ComposerUIPlugin.PLUGIN_ID + "." + "help_project_wizard_basic");
		
		setControl(composite);
		composerPackage = new ComposerPackage();
		keywordConverter = new String2KeywordsConverter(composerPackage);
		
	}

	@Override
	public void initPage() {
		
	}

	public GridLayout initGridLayout(GridLayout layout, boolean margins) {
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		if (margins) {
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}
		return layout;
	}

	public void performFinish(IProgressMonitor monitor) {
		
	}

	public IProject getProjectHandle() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(nameGroup.getName());
	}

	public IEnvironment getEnvironment() {
		return PHPLocationGroup.getEnvironment();
	}

	public boolean isInWorkspace() {
		return PHPLocationGroup.isInWorkspace();
	}

	public boolean isInLocalServer() {
		return PHPLocationGroup.isInLocalServer();
	}

	protected boolean canCreate(File file) {
		while (!file.exists()) {
			file = file.getParentFile();
			if (file == null)
				return false;
		}

		return file.canWrite();
	}

	public WizardModel getWizardData() {
		if (fragment != null) {
			return fragment.getWizardModel();
		}
		return null;
	}

	public URI getLocationURI() {
		IEnvironment environment = getEnvironment();
		return environment.getURI(PHPLocationGroup.getLocation());
	}

	public boolean getDetect() {
		return detectGroup.mustDetect();
	}

	public boolean hasPhpSourceFolder() {
		return true;
	}

	public boolean isDefaultVersionSelected() {
		return false;
	}

	public boolean getUseAspTagsValue() {
		return versionGroup != null && versionGroup.fConfigurationBlock.getUseAspTagsValue();
	}

	public PHPVersion getPHPVersionValue() {
		if (versionGroup != null) {
			return versionGroup.fConfigurationBlock.getPHPVersionValue();
		}
		return null;
	}


	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof BasicSettingsGroup) {
			if (settingsGroup.getVendor() != null && nameGroup.getName() != null) {
				composerPackage.setName(String.format("%s/%s", settingsGroup.getVendor(), nameGroup.getName()));
			}
			
			if (settingsGroup.getDescription().length() > 0) {
				composerPackage.setDescription(settingsGroup.getDescription());
			}
			
			if (settingsGroup.getLicense().length() > 0) {
				composerPackage.getLicense().clear();
				composerPackage.getLicense().add(settingsGroup.getLicense());
			}
			
			if (settingsGroup.getType().length() > 0) {
				composerPackage.setType(settingsGroup.getType());
			}
			
			if (settingsGroup.getKeywords().length() > 0) {
				keywordConverter.convert(settingsGroup.getKeywords());
				System.err.println(composerPackage.toJson());
			}
		}
	}
	
	public ComposerPackage getPackage() {
		return composerPackage;
	}
}
