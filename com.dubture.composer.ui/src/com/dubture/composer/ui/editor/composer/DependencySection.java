package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.getcomposer.VersionedPackage;
import org.getcomposer.collection.Dependencies;

import com.dubture.composer.ui.controller.DependencyController;
import com.dubture.composer.ui.dialogs.DependencyDialog;
import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.editor.TableSection;
import com.dubture.composer.ui.parts.TablePart;

public class DependencySection extends TableSection implements PropertyChangeListener {

	private Dependencies dependencies;
	private TableViewer dependencyViewer;

	private IAction editAction;
	private IAction removeAction;
	
	private static final int EDIT_INDEX = 0;
	private static final int REMOVE_INDEX = 1;
	
	public DependencySection(ComposerFormPage page, Composite parent, Dependencies dependencies, String title, String description, boolean expanded) {
		super(page, parent, Section.EXPANDED | Section.DESCRIPTION | Section.TWISTIE | Section.TITLE_BAR, new String[]{"Edit...", "Remove"});
		this.dependencies = dependencies;
		createClient(getSection(), page.getManagedForm().getToolkit(), title, description, expanded);
	}

	protected void createClient(final Section section, FormToolkit toolkit, String title, String description, boolean expanded) {
		section.setText(title);
		section.setDescription(description);
		section.setExpanded(expanded);
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessVerticalSpace = expanded;
		section.setLayoutData(gd);
		
		Composite container = createClientContainer(section, 2, toolkit);
		createViewerPartControl(container, SWT.MULTI, 2, toolkit);
		TablePart tablePart = getTablePart();
		DependencyController dependencyController = new DependencyController();
		dependencyViewer = tablePart.getTableViewer();
		dependencyViewer.setContentProvider(dependencyController);
		dependencyViewer.setLabelProvider(dependencyController);
		
		toolkit.paintBordersFor(container);
		section.setClient(container);
		

		dependencyViewer.setInput(dependencies);
		dependencies.addPropertyChangeListener(this);
		updateButtons();
		
		makeActions();
		updateMenu();
	}
	
	public void setExpanded(boolean expanded) {
		getSection().setExpanded(expanded);
		
		if (expanded) {
			((GridData)getSection().getLayoutData()).widthHint = 0;
		} else {
			((GridData)getSection().getLayoutData()).widthHint = SWT.DEFAULT;
		}
	}
	
	protected boolean createCount() {
		return true;
	}
	
	private void updateButtons() {
		ISelection selection = dependencyViewer.getSelection();
		
		TablePart tablePart = getTablePart();
		tablePart.setButtonEnabled(EDIT_INDEX, !selection.isEmpty());
		tablePart.setButtonEnabled(REMOVE_INDEX, !selection.isEmpty());
	}
	
	private void updateMenu() {
		IStructuredSelection selection = (IStructuredSelection)dependencyViewer.getSelection();
		
		editAction.setEnabled(selection.size() > 0);
		removeAction.setEnabled(selection.size() > 0);
	}

	public void refresh() {
		dependencyViewer.refresh();
		super.refresh();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		refresh();
	}
	
	protected void selectionChanged(IStructuredSelection sel) {
		updateButtons();
		updateMenu();
	}
	
	private void makeActions() {
		editAction = new Action("Edit...") {
			@Override
			public void run() {
				handleEdit();
			}
		};
		
		removeAction = new Action("Remove") {
			@Override
			public void run() {
				handleRemove();
			}
		};
	}
	
	@Override
	protected void fillContextMenu(IMenuManager manager) {
		manager.add(editAction);
		manager.add(removeAction);
	}
	
	private void handleEdit() {
		VersionedPackage dep = (VersionedPackage)((StructuredSelection)dependencyViewer.getSelection()).getFirstElement();
		DependencyDialog diag = new DependencyDialog(dependencyViewer.getTable().getShell(), dep.clone());
		if (diag.open() == Dialog.OK) {
			dep = diag.getDependency();
			refresh();
		}
	}
	
	private void handleRemove() {
		VersionedPackage dep = (VersionedPackage)((StructuredSelection)dependencyViewer.getSelection()).getFirstElement();
		MessageDialog diag = new MessageDialog(
				dependencyViewer.getTable().getShell(), 
				"Remove Dependency", 
				null, 
				"Do you really wan't to remove " + dep.getName() + "?", 
				MessageDialog.WARNING,
				new String[] {"Yes", "No"},
				0);
		
		if (diag.open() == Dialog.OK) {
			dependencies.remove(dep);
			refresh();
		}
	}
	
	@Override
	protected void buttonSelected(int index) {
		switch (index) {
			
		case EDIT_INDEX:
			handleEdit();
			break;
			
		case REMOVE_INDEX:
			handleRemove();
			break;
		}
	}

	protected void createClient(Section section, FormToolkit toolkit) {
	}
}
