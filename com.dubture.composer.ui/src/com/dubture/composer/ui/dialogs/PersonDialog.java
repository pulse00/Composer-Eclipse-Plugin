package com.dubture.composer.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.getcomposer.entities.Person;

public class PersonDialog extends Dialog {

	private Person person;
	private Text name;
	private Text email;
	private Text homepage;
	private Text role;
	
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
		if (person.getName() != null) {
			name.setText(person.getName());
		}
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				person.setName(name.getText());
			}
		});
		
		Label lblEmail = new Label(contents, SWT.NONE);
		lblEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblEmail.setText("Email");
		
		email = new Text(contents, SWT.BORDER);
		email.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		if (person.getEmail() != null) {
			email.setText(person.getEmail());
		}
		email.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				person.setEmail(email.getText());
			}
		});
		
		Label lblHomepage = new Label(contents, SWT.NONE);
		lblHomepage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblHomepage.setText("Homepage");
		
		homepage = new Text(contents, SWT.BORDER);
		homepage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		if (person.getHomepage() != null) {
			homepage.setText(person.getHomepage());
		}
		homepage.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				person.setHomepage(homepage.getText());
			}
		});
		
		Label lblRole = new Label(contents, SWT.NONE);
		lblRole.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblRole.setText("Role");
		
		role = new Text(contents, SWT.BORDER);
		role.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		if (person.getRole() != null) {
			role.setText(person.getRole());
		}
		role.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				person.setRole(role.getText());
			}
		});
		
		
		return contents;
	}
}
