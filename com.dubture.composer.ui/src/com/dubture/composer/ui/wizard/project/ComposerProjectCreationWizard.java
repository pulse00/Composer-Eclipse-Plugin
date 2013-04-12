package com.dubture.composer.ui.wizard.project;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.NewElementWizard;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.project.ProjectOptions;
import org.eclipse.php.internal.ui.wizards.WizardModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import com.dubture.composer.core.facet.FacetManager;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.editor.composer.ComposerFormEditor;

@SuppressWarnings("restriction")
public class ComposerProjectCreationWizard extends NewElementWizard implements INewWizard, IExecutableExtension {

	public static final String SELECTED_PROJECT = "SelectedProject";
	
	protected ComposerProjectWizardFirstPage firstPage;
	protected ComposerProjectWizardSecondPage secondPage;
	protected ComposerProjectWizardSecondPage lastPage;
	protected IConfigurationElement config;

	public ComposerProjectCreationWizard() {
		setDefaultPageImageDescriptor(ComposerUIPluginImages.CREATE_PROJECT);
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle("New Composer Project");
	}
	
	public void addPages() {
		super.addPages();
		
		firstPage = new ComposerProjectWizardFirstPage();
		addPage(firstPage);

		secondPage = new ComposerProjectWizardSecondPage(firstPage);
		addPage(secondPage);
		
		lastPage = secondPage;
	}
	
	
	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		this.config = config;
	}

	@Override
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		
		if (firstPage != null) {
			firstPage.performFinish(monitor);
		}
		if (secondPage != null) {
			secondPage.performFinish(monitor);
		}
	}

	@Override
	public IModelElement getCreatedElement() {
		return DLTKCore.create(firstPage.getProjectHandle());		
	}
	
	@Override
	public boolean performFinish() {
		
		boolean res = super.performFinish();
		if (res) {
			
			BasicNewProjectResourceWizard.updatePerspective(config);
			selectAndReveal(lastPage.getScriptProject().getProject());
			IProject project = lastPage.getScriptProject().getProject();
			PHPVersion version = firstPage.getPHPVersionValue();
			if (version == null) {
				version = ProjectOptions.getDefaultPhpVersion();
			}
			
			FacetManager.installFacets(project, version, null);
			WizardModel model = firstPage.getWizardData();

			Object eanblement = null;
			if (model != null) {
				eanblement = model
						.getObject("REMOTE_GROUP_REMOTE_PROJECT_ENABLED");
			}

			if (model != null && eanblement != null && (Boolean) eanblement) {

				model.putObject(SELECTED_PROJECT, lastPage.getScriptProject()
						.getProject());

				IRunnableWithProgress run = (IRunnableWithProgress) Platform
						.getAdapterManager().getAdapter(model,
								IRunnableWithProgress.class);

				if (run != null) {
					try {
						getContainer().run(true, false, run);
					} catch (InvocationTargetException e) {
						handleFinishException(getShell(), e);
						return false;
					} catch (InterruptedException e) {
						return false;
					}
				}
			}
			
			IFile json = project.getFile("composer.json");
			
			if (json != null) {
				try {
					IEditorInput editorInput = new FileEditorInput(json);
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					IWorkbenchPage page = window.getActivePage();
					page.openEditor(editorInput, ComposerFormEditor.ID);
				} catch (Exception e) {
					Logger.logException(e);
				}
			}
		}
		
		return res;
	}
	
	@Override
	public boolean performCancel() {
		secondPage.cancel();
		return super.performCancel();
	}
}
