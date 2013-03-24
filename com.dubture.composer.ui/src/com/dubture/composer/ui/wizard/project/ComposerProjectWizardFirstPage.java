package com.dubture.composer.ui.wizard.project;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.environment.IEnvironment;
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

@SuppressWarnings("restriction")
public class ComposerProjectWizardFirstPage extends WizardPage implements IPHPProjectCreateWizardPage {

	NameGroup nameGroup;
	LocationGroup PHPLocationGroup;
	protected String fInitialName;
	private DetectGroup detectGroup;
	private Validator pdtValidator;
	protected WizardFragment fragment;
	public VersionGroup versionGroup;

	protected ComposerProjectWizardFirstPage() {
		super("Basic setup");
		setPageComplete(false);
		setTitle("Basic setup");
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
		PHPLocationGroup.addObserver(pdtValidator);

		setControl(composite);
	}

	@Override
	public void initPage() {
		// TODO Auto-generated method stub

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

}
