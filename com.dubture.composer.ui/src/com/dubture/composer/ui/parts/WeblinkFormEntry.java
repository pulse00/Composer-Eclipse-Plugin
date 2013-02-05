package com.dubture.composer.ui.parts;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.dubture.composer.ui.editor.FormEntryAdapter;

public class WeblinkFormEntry extends FormEntry {

	public WeblinkFormEntry(Composite parent, FormToolkit toolkit, String labelText) {
		super(parent, toolkit, labelText, null, true);
		
		addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				try {
					Hyperlink link = (Hyperlink)entry.getLabel();
					URL url = new URL(entry.getValue());
					link.setHref(url);
				} catch (MalformedURLException e) {
//					e.printStackTrace();
				}
			}
			
			public void linkActivated(HyperlinkEvent e) {
				if (e.getHref() != null && e.getHref().toString() != null) {
					Program.launch(e.getHref().toString());
				}
			}

		});
	}
}
