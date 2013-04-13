package com.dubture.composer.ui.wizard.project;

import java.util.Observable;

import org.apache.commons.lang.WordUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.core.ComposerPluginConstants;
import com.dubture.composer.ui.ComposerUIPlugin;
import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;
import com.dubture.composer.ui.wizard.AbstractWizardSecondPage;
import com.dubture.getcomposer.core.objects.Namespace;

public class ComposerProjectWizardSecondPage extends AbstractWizardSecondPage {

	protected AutoloadGroup autoloadGroup;
	private AutoloadValidator validator;

	public ComposerProjectWizardSecondPage(AbstractWizardFirstPage mainPage) {
		super(mainPage, "Dependencies");
	}

	@Override
	public void createControl(Composite parent) {

		int numColumns = 1;
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout(numColumns, false));
		
		final Group group = new Group(composite, SWT.None);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(numColumns, false));
		group.setText("PSR-0");
		
		autoloadGroup = new AutoloadGroup(group, getShell());
		autoloadGroup.addObserver(this);
		
		validator = new AutoloadValidator(this);
		autoloadGroup.addObserver(validator);
		
		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ComposerUIPlugin.PLUGIN_ID + "." + "help_project_wizard_autoload");
		setControl(composite);
		((ComposerProjectWizardFirstPage)firstPage).settingsGroup.addObserver(this);		

	}

	@Override
	public void update(Observable observable, Object object) {
		
		if (observable instanceof BasicSettingsGroup) {
			ComposerProjectWizardFirstPage fPage = (ComposerProjectWizardFirstPage) firstPage;
			autoloadGroup.setNamespace(WordUtils.capitalize(fPage.settingsGroup.getVendor()));
			return;
		}
		
		updateNamespace(autoloadGroup.getNamespace());
	}
	
	protected void updateNamespace(String namespace) {

		Namespace ns = new Namespace();
		ns.setNamespace(namespace);
		ns.add(ComposerPluginConstants.DEFAULT_SRC_FOLDER);
		
		firstPage.getPackage().getAutoload().getPsr0().clear();
		firstPage.getPackage().getAutoload().getPsr0().add(ns);
	}


	@Override
	protected String getPageTitle() {
		return "Autoloading settings";
	}
	
	@Override
	protected String getPageDescription() {
		return "Setup autoloading for your project.";
	}

	@Override
	protected void finishPage(IProgressMonitor monitor) throws Exception {
		
		monitor.setTaskName("Creating project structure");
		addComposerJson(monitor);
		monitor.worked(4);
		
		monitor.setTaskName("Installing composer.phar");
		installComposer(monitor);
		monitor.worked(4);
		
		monitor.setTaskName("Dumping autoloader");
		dumpAutoload(monitor);
		monitor.worked(2);
	}
}
