package com.dubture.composer.test;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.tests.model.AbstractModelTests;

public abstract class ComposerModelTests extends AbstractModelTests {

	public ComposerModelTests(String name) {
		super(ComposerCoreTestPlugin.PLUGIN_ID, name);
	}

	protected IScriptProject ensureScriptProject(String name) {
		IScriptProject prj = null;
		try {
			deleteProject(name);
			prj = setUpScriptProject(name);
		} catch (Exception e) {
		}
		
		return prj;
	}
}
