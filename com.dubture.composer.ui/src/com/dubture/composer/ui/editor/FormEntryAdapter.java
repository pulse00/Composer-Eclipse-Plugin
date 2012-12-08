/*******************************************************************************
 *  Copyright (c) 2003, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *  Source:
 *     https://github.com/eclipse/eclipse.pde.ui/blob/master/ui/org.eclipse.pde.ui/src/org/eclipse/pde/internal/ui/editor/FormEntryAdapter.java
 *******************************************************************************/
package com.dubture.composer.ui.editor;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import com.dubture.composer.ui.parts.FormEntry;
import com.dubture.composer.ui.parts.IFormEntryListener;

public class FormEntryAdapter implements IFormEntryListener {
	protected IActionBars actionBars;

	public FormEntryAdapter(IActionBars actionBars) {
		this.actionBars = actionBars;
	}

	public FormEntryAdapter() {
	}

	public void focusGained(FormEntry entry) {
		ITextSelection selection = new TextSelection(1, 1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.newparts.IFormEntryListener#textDirty(org.eclipse.pde.internal.ui.newparts.FormEntry)
	 */
	public void textDirty(FormEntry entry) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.newparts.IFormEntryListener#textValueChanged(org.eclipse.pde.internal.ui.newparts.FormEntry)
	 */
	public void textValueChanged(FormEntry entry) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.newparts.IFormEntryListener#browseButtonSelected(org.eclipse.pde.internal.ui.newparts.FormEntry)
	 */
	public void browseButtonSelected(FormEntry entry) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.events.HyperlinkListener#linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent)
	 */
	public void linkEntered(HyperlinkEvent e) {
		if (actionBars == null)
			return;
		IStatusLineManager mng = actionBars.getStatusLineManager();
		mng.setMessage(e.getLabel());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.events.HyperlinkListener#linkExited(org.eclipse.ui.forms.events.HyperlinkEvent)
	 */
	public void linkExited(HyperlinkEvent e) {
		if (actionBars == null)
			return;
		IStatusLineManager mng = actionBars.getStatusLineManager();
		mng.setMessage(null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.events.HyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
	 */
	public void linkActivated(HyperlinkEvent e) {
	}

	public void selectionChanged(FormEntry entry) {
		ITextSelection selection = new TextSelection(1, 1);
	}

	public void focusLost(FormEntry entry) {
	}
}
