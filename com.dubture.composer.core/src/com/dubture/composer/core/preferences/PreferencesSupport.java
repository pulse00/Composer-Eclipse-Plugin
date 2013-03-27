package com.dubture.composer.core.preferences;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferencesSupport {

	private HashMap<IProject, ProjectScope> projectToScope;
	private String nodeQualifier;
	private IPreferenceStore preferenceStore;

	/**
	 * Constructs a new PreferencesSupport.
	 * 
	 * @param nodeQualifier
	 *            A string qualifier for the node (for example:
	 *            PHPCorePlugin.ID)
	 * @param preferenceStore
	 *            The relevant preferences store.
	 */
	public PreferencesSupport(String nodeQualifier,
			IPreferenceStore preferenceStore) {
		this.nodeQualifier = nodeQualifier;
		this.preferenceStore = preferenceStore;
		projectToScope = new HashMap<IProject, ProjectScope>();
	}
	
	public String getProjectSpecificPreferencesValue(String key, String def,
			IProject project) {
		assert project != null;
		ProjectScope scope = (ProjectScope) projectToScope.get(project);
		if (scope == null) {
			scope = new ProjectScope(project);
			projectToScope.put(project, scope);
		}
		IEclipsePreferences node = scope.getNode(nodeQualifier);
		if (node != null) {
			return node.get(key, def);
		}
		return null;
	}
	
	
	public String getPreferencesValue(String key, String def, IProject project) {
		if (project == null) {
			return getWorkspacePreferencesValue(key);
		}
		String projectSpecificPreferencesValue = getProjectSpecificPreferencesValue(
				key, def, project);
		if (projectSpecificPreferencesValue == null) {
			return getWorkspacePreferencesValue(key);
		}

		return projectSpecificPreferencesValue;
	}
	
	public String getWorkspacePreferencesValue(String key) {
		return preferenceStore.getString(key);
	}

}
