package com.dubture.composer.ui.parts;

public interface IComboFormEntryListener {

	/**
	* The value of the entry has been changed to be the text
	* in the text control (as a result of 'commit' action).
	* @param entry
	*/
	void textValueChanged(ComboFormEntry entry);
	

	void selectionChanged(ComboFormEntry entry);
}
