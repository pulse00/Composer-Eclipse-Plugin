package com.dubture.composer.ui.wizard;

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
abstract public class AbstractValidator implements Observer {

	protected AbstractWizardFirstPage firstPage;
	protected IWorkspace workspace;
	protected String name;
	protected IProject handle;
	protected String location;
	protected IEnvironment environment;

	/**
	 * @param composerProjectWizardFirstPage
	 */
	public AbstractValidator(AbstractWizardFirstPage composerProjectWizardFirstPage) {
		firstPage = composerProjectWizardFirstPage;
	}

	@Override
	public void update(Observable observable, Object object) {

		workspace = DLTKUIPlugin.getWorkspace();
		name = firstPage.nameGroup.getName();
		handle = firstPage.getProjectHandle();
		location = firstPage.PHPLocationGroup.getLocation().toOSString();
		environment = firstPage.getEnvironment();

		try {
			validateName();
			beginValidation();
			validateProjectNotExists();
			validateLocation();
			finishValidation();
		} catch (ValidationException e) {
			return;
		}

		firstPage.setPageComplete(true);
		firstPage.setErrorMessage(null);
		firstPage.setMessage(null);
	}

	/**
	 * Check whether the project name field is not empty and valid
	 * 
	 * @param name
	 * @throws ValidationException
	 */
	protected void validateName() throws ValidationException {
		if (name == null || name.length() == 0) {
			firstPage.setErrorMessage(null);
			firstPage.setMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_enterProjectName);
			firstPage.setPageComplete(false);
			throw new ValidationException();
		}

		final IStatus nameStatus = workspace.validateName(name, IResource.PROJECT);
		if (!nameStatus.isOK()) {
			firstPage.setErrorMessage(nameStatus.getMessage());
			firstPage.setPageComplete(false);
			throw new ValidationException();
		}
	}

	protected void validateProjectNotExists() throws ValidationException {
		// check whether project already exists
		if (!firstPage.isInLocalServer()) {
			if (handle.exists()) {
				firstPage.setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_projectAlreadyExists);
				firstPage.setPageComplete(false);
				throw new ValidationException();
			}
		}

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		String newProjectNameLowerCase = name.toLowerCase();
		for (IProject currentProject : projects) {
			String existingProjectName = currentProject.getName();
			if (existingProjectName.toLowerCase().equals(newProjectNameLowerCase)) {
				firstPage.setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_projectAlreadyExists);
				firstPage.setPageComplete(false);
				throw new ValidationException();
			}
		}
	}

	protected void validateLocation() throws ValidationException {
		// check whether location is empty
		if (location.length() == 0) {
			firstPage.setErrorMessage(null);
			firstPage.setMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_enterLocation);
			firstPage.setPageComplete(false);
			throw new ValidationException();
		}
		// check whether the location is a syntactically correct path
		if (!Path.EMPTY.isValidPath(location)) {
			firstPage.setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_invalidDirectory);
			firstPage.setPageComplete(false);
			throw new ValidationException();
		}

		// check whether the location has the workspace as prefix
		IPath projectPath = Path.fromOSString(location);
		if (!firstPage.PHPLocationGroup.isInWorkspace() && Platform.getLocation().isPrefixOf(projectPath)) {
			firstPage.setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_cannotCreateInWorkspace);
			firstPage.setPageComplete(false);
			throw new ValidationException();
		}

		// If we do not place the contents in the workspace validate the
		// location.
		if (!firstPage.PHPLocationGroup.isInWorkspace()) {
			if (EnvironmentManager.isLocal(environment)) {
				final IStatus locationStatus = workspace.validateProjectLocation(handle, projectPath);
				if (!locationStatus.isOK()) {
					firstPage.setErrorMessage(locationStatus.getMessage());
					firstPage.setPageComplete(false);
					throw new ValidationException();
				}

				if (!firstPage.canCreate(projectPath.toFile())) {
					firstPage.setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_invalidDirectory);
					firstPage.setPageComplete(false);
					throw new ValidationException();
				}
			}
		}
	}

	abstract protected void beginValidation() throws ValidationException;
	abstract protected void finishValidation() throws ValidationException;
}
