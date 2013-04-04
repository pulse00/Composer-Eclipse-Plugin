package com.dubture.composer.ui.editor.composer;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class LicenseContentAdapter extends TextContentAdapter {
	
	@Override
	public String getControlContents(Control control) {
		String text = ((Text)control).getText();
		String[] chunks = text.split(",");
		return chunks[chunks.length - 1].trim();
	}
	
	@Override
	public void setControlContents(Control control, String text,
			int cursorPosition) {

		String id = text.replaceAll(".+\\((.+)\\)$", "$1");

		String val = ((Text)control).getText();
		String[] chunks = val.split(",");
		chunks[chunks.length - 1] = id;
		val = StringUtils.join(chunks, ", ");
		cursorPosition = val.length();
		
		((Text) control).setText(val);
		((Text) control).setSelection(cursorPosition, cursorPosition);
	}
}