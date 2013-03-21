package com.dubture.composer.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import com.dubture.composer.core.builder.AutomatedBuildpathBuilder;

public class ComposerNature implements IProjectNature {
	public static final String NATURE_ID = "com.dubture.composer.core.composerNature";

	private IProject project;

	@Override
	public void configure() throws CoreException {

		// install builder
		IProjectDescription description = project.getDescription();
		final ICommand buildCommand = description.newCommand();
		buildCommand.setBuilderName(AutomatedBuildpathBuilder.ID);

		final List<ICommand> commands = new ArrayList<ICommand>();
		commands.add(buildCommand);
		commands.addAll(Arrays.asList(description.getBuildSpec()));

		description
				.setBuildSpec(commands.toArray(new ICommand[commands.size()]));
		project.setDescription(description, null);
	}

	@Override
	public void deconfigure() throws CoreException {

		// uninstall builder
		final IProjectDescription description = project.getDescription();
		final List<ICommand> commands = new ArrayList<ICommand>();
		commands.addAll(Arrays.asList(description.getBuildSpec()));

		for (final ICommand buildSpec : description.getBuildSpec()) {
			if (AutomatedBuildpathBuilder.ID.equals(buildSpec.getBuilderName())) {
				// remove builder
				commands.remove(buildSpec);
			}
		}

		description
				.setBuildSpec(commands.toArray(new ICommand[commands.size()]));
		project.setDescription(description, null);
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}
}
