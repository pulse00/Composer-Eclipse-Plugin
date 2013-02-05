package com.dubture.composer.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.getcomposer.entities.Person;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;

public class PersonDialog extends Dialog {

	private Person person;
	private Text name;
	private Text email;
	private Text homepage;
	private Text text;
	
	/**
	 * @wbp.parser.constructor
	 */
	public PersonDialog(Shell parentShell, Person author) {
		super(parentShell);
		this.person = author;
	}

	public PersonDialog(IShellProvider parentShell, Person author) {
		super(parentShell);
		this.person = author;
	}
	
	public Person getPerson() {
		return person;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite contents = new Composite(parent, SWT.BORDER | SWT.NO_BACKGROUND | SWT.NO_FOCUS | SWT.NO_MERGE_PAINTS | SWT.NO_REDRAW_RESIZE | SWT.NO_RADIO_GROUP | SWT.EMBEDDED);
		contents.setLayout(new GridLayout(2, false));
		
		Label lblName = new Label(contents, SWT.NONE);
		GridData gd_lblName = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblName.widthHint = 100;
		lblName.setLayoutData(gd_lblName);
		lblName.setText("Name");
		
		name = new Text(contents, SWT.BORDER);
		GridData gd_name = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_name.widthHint = 150;
		name.setLayoutData(gd_name);
		
		Label lblEmail = new Label(contents, SWT.NONE);
		lblEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblEmail.setText("Email");
		
		email = new Text(contents, SWT.BORDER);
		email.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblHomepage = new Label(contents, SWT.NONE);
		lblHomepage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblHomepage.setText("Homepage");
		
		homepage = new Text(contents, SWT.BORDER);
		homepage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblRole = new Label(contents, SWT.NONE);
		lblRole.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblRole.setText("Role");
		
		text = new Text(contents, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		return contents;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(name);
		IObservableValue nameAuthorObserveValue = BeanProperties.value("name").observe(person);
		bindingContext.bindValue(observeTextNameObserveWidget, nameAuthorObserveValue, null, null);
		//
		IObservableValue observeTextEmailObserveWidget = WidgetProperties.text(SWT.Modify).observe(email);
		IObservableValue emailAuthorObserveValue = BeanProperties.value("email").observe(person);
		bindingContext.bindValue(observeTextEmailObserveWidget, emailAuthorObserveValue, null, null);
		//
		IObservableValue observeTextHomepageObserveWidget = WidgetProperties.text(SWT.Modify).observe(homepage);
		IObservableValue homepageAuthorObserveValue = BeanProperties.value("homepage").observe(person);
		bindingContext.bindValue(observeTextHomepageObserveWidget, homepageAuthorObserveValue, null, null);
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue rolePersonObserveValue = BeanProperties.value("role").observe(person);
		bindingContext.bindValue(observeTextTextObserveWidget, rolePersonObserveValue, null, null);
		//
		return bindingContext;
	}
}
