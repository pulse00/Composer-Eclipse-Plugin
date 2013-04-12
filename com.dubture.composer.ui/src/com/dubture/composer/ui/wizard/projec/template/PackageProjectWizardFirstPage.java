package com.dubture.composer.ui.wizard.projec.template;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.php.internal.ui.wizards.CompositeData;
import org.eclipse.php.internal.ui.wizards.DetectGroup;
import org.eclipse.php.internal.ui.wizards.LocationGroup;
import org.eclipse.php.internal.ui.wizards.NameGroup;
import org.eclipse.php.internal.ui.wizards.WizardFragment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.ui.ComposerUIPlugin;
import com.dubture.composer.ui.converter.String2KeywordsConverter;
import com.dubture.composer.ui.job.CreateProjectJob;
import com.dubture.composer.ui.wizard.project.ComposerProjectWizardFirstPage;
import com.dubture.composer.ui.wizard.project.Validator;
import com.dubture.composer.ui.wizard.project.VersionGroup;
import com.dubture.getcomposer.core.ComposerPackage;

@SuppressWarnings("restriction")
public class PackageProjectWizardFirstPage extends ComposerProjectWizardFirstPage {

	public PackageProjectWizardFirstPage() {
		super();
		setPageComplete(false);
		setTitle("Basic Composer Configuration");
		setDescription("Create a new project from existing package");
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
		settingsGroup = new ProjectTemplateGroup(composite, getShell());
		
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
	
	public boolean installFromTemplate() {
		return true;
	}
	
	@Override
	public void performFinish(IProgressMonitor monitor) {
		
		if (installFromTemplate()) {
			try {
				CreateProjectJob projectJob = new CreateProjectJob(nameGroup.getName(), ((ProjectTemplateGroup)settingsGroup).projectName.getText());
				projectJob.schedule();
				// we need to wait a moment otherwise composer screams the project directory is not empty
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
