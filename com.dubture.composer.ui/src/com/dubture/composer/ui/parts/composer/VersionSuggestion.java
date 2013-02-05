package com.dubture.composer.ui.parts.composer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.getcomposer.ComposerConstants;

import com.dubture.composer.ui.utils.WidgetFactory;
import com.dubture.composer.ui.utils.WidgetHelper;

public class VersionSuggestion {

	private FormToolkit toolkit = null;
	private WidgetFactory factory = null;
	private Text version;
	private Composite body;
	
	private Button recentMinor;
	private Button recentMajor;
	private Button noConstraint;
	private Map<String, Button> constraintButtons = new HashMap<String, Button>();
	
	public VersionSuggestion(String name, Composite parent, Text version, FormToolkit toolkit) {
		this(name, parent, version, new WidgetFactory(toolkit));
		this.toolkit = toolkit;
	}
	
	public VersionSuggestion(String name, Composite parent, Text version, WidgetFactory factory) {
		this.factory = factory;
		this.version = version;
		// TODO: load package with versions
		create(parent, factory);
	}
	
	private void create(Composite parent, WidgetFactory factory) {
		body = factory.createComposite(parent);
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		body.setLayout(new GridLayout());
		body.setBackground(parent.getBackground());
		WidgetHelper.trimComposite(body, 0, 0, 0, 0, 0, 0);
		
		// suggestions
		Group suggestions = new Group(body, SWT.SHADOW_IN);
		suggestions.setText("Suggestions");
		suggestions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		suggestions.setLayout(new GridLayout(2, true));
		suggestions.setBackgroundMode(SWT.INHERIT_DEFAULT);
		WidgetHelper.trimComposite(suggestions, 0, 0, 0, 0, 0, 5);
//		makeSlim(suggestions);
		
		Composite major = factory.createComposite(suggestions, SWT.NO_BACKGROUND | SWT.TRANSPARENT);
		major.setLayout(new GridLayout());
		WidgetHelper.trimComposite(major, -5, -5);
//		makeSlim(major);
		
		Label recentMajorLbl = factory.createLabel(major, SWT.TRANSPARENT);
		recentMajorLbl.setText("Recent Major:");
		recentMajorLbl.setBackground(suggestions.getBackground());
		
		recentMajor = factory.createButton(major);
		recentMajor.setText("~ X");
		recentMajor.setAlignment(SWT.CENTER);
		recentMajor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Composite minor = factory.createComposite(suggestions, SWT.NO_BACKGROUND | SWT.TRANSPARENT);
		minor.setLayout(new GridLayout());
		WidgetHelper.trimComposite(minor, -5, -5);
//		makeSlim(minor);
		
		Label recentMinorLbl = factory.createLabel(minor, SWT.NO_BACKGROUND);
		recentMinorLbl.setText("Recent Minor:");
		recentMinorLbl.setBackground(suggestions.getBackground());
		
		recentMinor = factory.createButton(minor);
		recentMinor.setText("~ X.Y");
		recentMinor.setAlignment(SWT.CENTER);
		recentMinor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		// custom
		Group custom = new Group(body, SWT.SHADOW_ETCHED_IN);
		custom.setText("Custom");
		custom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		custom.setLayout(new GridLayout(2, false));
		custom.setBackgroundMode(SWT.INHERIT_DEFAULT);
		WidgetHelper.trimComposite(custom, 0, 0, 0, 0, 0, 5);
		
		TableViewer versions = new TableViewer(custom);
		versions.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Composite constraints = factory.createComposite(custom, SWT.NO_BACKGROUND);
		constraints.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		constraints.setLayout(new GridLayout(3, false));
		constraints.setBackgroundMode(SWT.INHERIT_DEFAULT);
		WidgetHelper.trimComposite(constraints, -5, -5, -5, -5, 0, 0);
//		makeSlim(constraints);
		
		Label constraintsLbl = factory.createLabel(constraints, SWT.NO_BACKGROUND | SWT.TRANSPARENT);
		constraintsLbl.setText("Constraints:");
		constraintsLbl.setBackground(custom.getBackground());
		constraintsLbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		noConstraint = factory.createButton(constraints, SWT.RADIO | SWT.NO_BACKGROUND);
		noConstraint.setText("None");
		noConstraint.setBackground(custom.getBackground());
		noConstraint.setSelection(true);
		
		for (String constraint : new String[]{"~", ">", ">=", "!=", "<", ">="}) {
			Button c = factory.createButton(constraints, SWT.RADIO | SWT.TRANSPARENT);
			c.setText(constraint);
			c.setBackground(custom.getBackground());
			constraintButtons.put(constraint, c);
		}
	}
	
	public Composite getBody() {
		return body;
	}
}
