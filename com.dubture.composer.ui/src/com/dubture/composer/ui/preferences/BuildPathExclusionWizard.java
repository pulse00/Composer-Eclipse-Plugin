package com.dubture.composer.ui.preferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElement;
import org.eclipse.dltk.internal.ui.wizards.buildpath.EditFilterWizard;

@SuppressWarnings("restriction")
public class BuildPathExclusionWizard extends EditFilterWizard {

	public BuildPathExclusionWizard(BPListElement[] existingEntries, BPListElement newEntry) {
		super(existingEntries, newEntry);
	}
	
	@Override
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		// avoid actually saving the filters to .buildpath
	}
}
