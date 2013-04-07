package com.dubture.composer.ui.preferences;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.core.BuildpathEntry;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElement;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.preferences.WizardPropertyPage;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.ComposerPluginConstants;
import com.dubture.composer.core.internal.resources.ComposerProject;
import com.dubture.composer.core.log.Logger;

@SuppressWarnings("restriction")
public class BuildPathManagementPage extends WizardPropertyPage {

	private IScriptProject scriptProject;
	private BuildPathExclusionWizard wizard;

	
	@Override
	protected IWizard createWizard() {
		
		BPListElement elem = new BPListElement(scriptProject, IBuildpathEntry.BPE_SOURCE, false);
		try {
			ComposerProject composerProject = new ComposerProject(scriptProject.getProject());
			IPath path = scriptProject.getPath().append(composerProject.getVendorDir());
			elem.setPath(path);
			
			IEclipsePreferences projectPreferences = ComposerPlugin.getDefault().getProjectPreferences(scriptProject.getProject());
			String stored = projectPreferences.get(ComposerPluginConstants.BUILDPATH_INCLUDES_EXCLUDES, null);
			
			if (stored != null) {
				IBuildpathEntry buildpathEntry = scriptProject.decodeBuildpathEntry(stored);
				for (IPath includePath : buildpathEntry.getInclusionPatterns()) {
					elem.addToInclusion(path.append(includePath));
				}
				for (IPath excludePath : buildpathEntry.getExclusionPatterns()) {
					elem.addToExclusions(path.append(excludePath));
				}
			}
		} catch (IOException e) {
			Logger.logException(e);
		}
		
		return wizard = new BuildPathExclusionWizard(new BPListElement[]{}, elem);
	}
	
	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		scriptProject = (IScriptProject) element;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void applyChanges() {
		
		List<BPListElement> elements = wizard.getModifiedElements();
		BPListElement element = (BPListElement) elements.get(0);
		IBuildpathEntry buildpathEntry = element.getBuildpathEntry();
		
		if (buildpathEntry instanceof BuildpathEntry) {
			BuildpathEntry entry = (BuildpathEntry) buildpathEntry;
			String encodeBuildpathEntry = scriptProject.encodeBuildpathEntry(entry);
			IEclipsePreferences projectPreferences = ComposerPlugin.getDefault().getProjectPreferences(scriptProject.getProject());
			projectPreferences.put(ComposerPluginConstants.BUILDPATH_INCLUDES_EXCLUDES, encodeBuildpathEntry);
		}
	}
}
