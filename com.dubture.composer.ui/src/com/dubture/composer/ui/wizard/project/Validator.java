package com.dubture.composer.ui.wizard.project;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.ui.DLTKUIPlugin;

@SuppressWarnings("restriction")
public final class Validator implements Observer {
	
	private final ComposerProjectWizardFirstPage composerProjectWizardFirstPage;

	/**
	 * @param composerProjectWizardFirstPage
	 */
	Validator(ComposerProjectWizardFirstPage composerProjectWizardFirstPage) {
		this.composerProjectWizardFirstPage = composerProjectWizardFirstPage;
	}

	public void update(Observable o, Object arg) {
		final IWorkspace workspace = DLTKUIPlugin.getWorkspace();
		final String name = this.composerProjectWizardFirstPage.nameGroup.getName();
		// check whether the project name field is empty
		if (name.length() == 0) {
			this.composerProjectWizardFirstPage.setErrorMessage(null);
			this.composerProjectWizardFirstPage.setMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_enterProjectName);
			this.composerProjectWizardFirstPage.setPageComplete(false);
			return;
		}
		// check whether the project name is valid
		final IStatus nameStatus = workspace.validateName(name,
				IResource.PROJECT);
		if (!nameStatus.isOK()) {
			this.composerProjectWizardFirstPage.setErrorMessage(nameStatus.getMessage());
			this.composerProjectWizardFirstPage.setPageComplete(false);
			return;
		}
		// check whether project already exists
		final IProject handle = this.composerProjectWizardFirstPage.getProjectHandle();

		if (!this.composerProjectWizardFirstPage.isInLocalServer()) {
			if (handle.exists()) {
				this.composerProjectWizardFirstPage.setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_projectAlreadyExists);
				this.composerProjectWizardFirstPage.setPageComplete(false);
				return;
			}
		}

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		String newProjectNameLowerCase = name.toLowerCase();
		for (IProject currentProject : projects) {
			String existingProjectName = currentProject.getName();
			if (existingProjectName.toLowerCase().equals(
					newProjectNameLowerCase)) {
				this.composerProjectWizardFirstPage.setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_projectAlreadyExists);
				this.composerProjectWizardFirstPage.setPageComplete(false);
				return;
			}
		}

		final String location = this.composerProjectWizardFirstPage.PHPLocationGroup.getLocation()
				.toOSString();
		// check whether location is empty
		if (location.length() == 0) {
			this.composerProjectWizardFirstPage.setErrorMessage(null);
			this.composerProjectWizardFirstPage.setMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_enterLocation);
			this.composerProjectWizardFirstPage.setPageComplete(false);
			return;
		}
		// check whether the location is a syntactically correct path
		if (!Path.EMPTY.isValidPath(location)) {
			this.composerProjectWizardFirstPage.setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_invalidDirectory);
			this.composerProjectWizardFirstPage.setPageComplete(false);
			return;
		}
		// check whether the location has the workspace as prefix
		IPath projectPath = Path.fromOSString(location);
		if (!this.composerProjectWizardFirstPage.PHPLocationGroup.isInWorkspace()
				&& Platform.getLocation().isPrefixOf(projectPath)) {
			this.composerProjectWizardFirstPage.setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_cannotCreateInWorkspace);
			this.composerProjectWizardFirstPage.setPageComplete(false);
			return;
		}
		// If we do not place the contents in the workspace validate the
		// location.
		if (!this.composerProjectWizardFirstPage.PHPLocationGroup.isInWorkspace()) {
			IEnvironment environment = this.composerProjectWizardFirstPage.getEnvironment();
			if (EnvironmentManager.isLocal(environment)) {
				final IStatus locationStatus = workspace
						.validateProjectLocation(handle, projectPath);
				if (!locationStatus.isOK()) {
					this.composerProjectWizardFirstPage.setErrorMessage(locationStatus.getMessage());
					this.composerProjectWizardFirstPage.setPageComplete(false);
					return;
				}

				if (!this.composerProjectWizardFirstPage.canCreate(projectPath.toFile())) {
					this.composerProjectWizardFirstPage.setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_invalidDirectory);
					this.composerProjectWizardFirstPage.setPageComplete(false);
					return;
				}
			}
		}

		if (this.composerProjectWizardFirstPage.fragment != null) {
			this.composerProjectWizardFirstPage.fragment.getWizardModel().putObject("ProjectName",
					this.composerProjectWizardFirstPage.nameGroup.getName());
			if (!this.composerProjectWizardFirstPage.fragment.isComplete()) {
				this.composerProjectWizardFirstPage.setErrorMessage((String) this.composerProjectWizardFirstPage.fragment.getWizardModel()
						.getObject("ErrorMessage"));
				this.composerProjectWizardFirstPage.setPageComplete(false);
				return;
			}
		}

		this.composerProjectWizardFirstPage.setPageComplete(true);
		this.composerProjectWizardFirstPage.setErrorMessage(null);
		this.composerProjectWizardFirstPage.setMessage(null);
	}
}