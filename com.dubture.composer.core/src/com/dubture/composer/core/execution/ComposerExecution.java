package com.dubture.composer.core.execution;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.dubture.composer.core.job.DownloadJob;
import com.dubture.composer.core.job.InstallJob;
import com.dubture.composer.core.job.SelfUpdateJob;
import com.dubture.composer.core.job.UpdateJob;
import com.dubture.composer.core.launch.PharNotFoundException;

/**
 * A wrapper class to run the composer jobs. It's purpose is to be used from handlers
 * or actions, instead of running the jobs directly
 * 
 * @author Thomas Gossmann
 */
public class ComposerExecution {

	private String json = null;
	private String phar = null;
	private IProject project;
	
	public ComposerExecution(IProject project) {
		this.project = project;
	}
	
	private String getComposerJson() {
		if (json == null) {
			IResource res = project.findMember("composer.json");
			if (res != null) {
				json = res.getLocation().toOSString();	
			}
		}
		return json;
	}
	
	private String getComposerPhar() {
		if (phar == null) {
			IResource res = project.findMember("composer.phar");
			if (res != null) {
				phar = res.getLocation().toOSString();	
			}
		}
		return phar;
	}
	
	private boolean canRunComposer() {
		return getComposerPhar() != null;
	}
	
	/**
	 * Install the composer requirements
	 * 
	 * @throws ComposerJsonNotFoundException 
	 * @throws PharNotFoundException 
	 */
	public void install() throws ComposerJsonNotFoundException, PharNotFoundException {
		if (!canRunComposer()) {
			throw new PharNotFoundException();
		}
		// no composer.json
		if (getComposerJson() == null) {
			throw new ComposerJsonNotFoundException("No composer.json found in " + project.getName());
		}
		
		InstallJob install = new InstallJob(project);
		install.setUser(true);
		install.schedule();
	}
	
	
	/**
	 * Updates the composer requirements
	 * 
	 * @throws ComposerJsonNotFoundException 
	 * @throws PharNotFoundException 
	 */
	public void update() throws ComposerJsonNotFoundException, PharNotFoundException {
		if (!canRunComposer()) {
			throw new PharNotFoundException();
		}
		// no composer.json
		if (getComposerJson() == null) {
			throw new ComposerJsonNotFoundException("No composer.json found in " + project.getName());
		}
		
		UpdateJob install = new UpdateJob(project);
		install.setUser(true);
		install.schedule();
	}
	
	/**
	 * Self-Updates composer
	 * 
	 * @throws ComposerJsonNotFoundException 
	 * @throws PharNotFoundException 
	 */
	public void selfUpdate() throws PharNotFoundException {
		if (!canRunComposer()) {
			throw new PharNotFoundException();
		}
		
		SelfUpdateJob install = new SelfUpdateJob(getComposerPhar());
		install.setUser(true);
		install.schedule();
	}
	
	public void downloadPhar() {
		DownloadJob downloader = new DownloadJob(project, "Downloading composer.phar");
		downloader.setUser(true);
		downloader.schedule();
	}
}
