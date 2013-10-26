package com.dubture.composer.ui.actions;

import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.action.Action;

import com.dubture.composer.ui.editor.composer.DependencyGraphPage;

public class ToggleDevAction extends Action {
	
	public static final String ID = "composer.dpg.toggle";
	
	private boolean showDev;
	private DependencyGraphPage graphPage;
	

	public ToggleDevAction(DependencyGraphPage graphPage) {
		super("Toggle dev packages");
		this.graphPage = graphPage;
		
		setDescription("Toggle dev packages");
		setToolTipText("Toggle dev packages");
		setId(ID);
		DLTKPluginImages.setLocalImageDescriptors(this, "th_showqualified.gif"); //$NON-NLS-1$
	}

	public void run() {
		showDev = !showDev;
		graphPage.applyFilter(showDev);
	}
}
