package com.dubture.composer.ui.parts.composer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Twistie;
import org.getcomposer.ComposerConstants;
import org.getcomposer.collection.Dependencies;

import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.utils.WidgetHelper;

public class DependencySearch extends PackageSearch {

	protected List<DependencySelectionFinishedListener> dependencyListeners = new ArrayList<DependencySelectionFinishedListener>();
	
	public DependencySearch (Composite parent, FormToolkit toolkit, String buttonText) {
		super(parent, toolkit, buttonText);
	}
	
	public DependencySearch (Composite parent, FormToolkit toolkit) {
		super(parent, toolkit);
	}
	
	public DependencySearch (Composite parent, String buttonText) {
		super(parent, buttonText);
	}
	
	public DependencySearch(Composite parent) {
		super(parent);
	}
	
	public void addDependencySelectionFinishedListener(DependencySelectionFinishedListener listener) {
		if (!dependencyListeners.contains(listener)) {
			dependencyListeners.add(listener);
		}
	}
	
	public void removeDependencySelectionFinishedListener(DependencySelectionFinishedListener listener) {
		dependencyListeners.remove(listener);
	}
 
	@Override
	protected void create(Composite parent, FormToolkit toolkit, String buttonText) {
		super.create(parent, toolkit, buttonText);
		
		if (addButton != null) {
			addButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					notifyDependencySelectionFinishedListener();
				}
			});
		}
		
		// test package part
		createPackagePart(pickedResults, "gossi/test");
	}
	
	protected void notifyDependencySelectionFinishedListener() {
		Dependencies deps = getDependencies();
		for (DependencySelectionFinishedListener listener : dependencyListeners) {
			listener.dependenciesSelected(deps);
		}
		clear();
	}
	
	public Dependencies getDependencies() {
		return null;
	}

	
	@Override
	protected DependencySearchPart createPackagePart(Composite parent, String name) {
		DependencySearchPart dsp = new DependencySearchPart(parent, toolkit, name);
		dsp.addToggleListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				getBody().layout(true, true);
			}
		});
		return dsp;
	}

}