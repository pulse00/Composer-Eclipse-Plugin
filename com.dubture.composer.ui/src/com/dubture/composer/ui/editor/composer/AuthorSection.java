package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.getcomposer.collection.Persons;
import org.getcomposer.entities.Person;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.dialogs.PersonDialog;
import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.editor.TableSection;
import com.dubture.composer.ui.parts.TablePart;

public class AuthorSection extends TableSection implements PropertyChangeListener {

	private TableViewer authorViewer;
	
	private IAction addAction;
	private IAction editAction;
	private IAction removeAction;
	
	private static final int ADD_INDEX = 0;
	private static final int EDIT_INDEX = 1;
	private static final int REMOVE_INDEX = 2;

	class AuthorController extends LabelProvider implements ITableLabelProvider, IStructuredContentProvider {

		private Persons authors;
		private Image authorImage = ComposerUIPluginImages.AUTHOR.createImage();

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			authors = (Persons)newInput;
		}

		public Object[] getElements(Object inputElement) {
			return authors.toArray();
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return authorImage;
		}

		public String getColumnText(Object element, int columnIndex) {
			Person author = (Person)element;
			StringBuilder sb = new StringBuilder();
			sb.append(author.getName());
			
			// TODO: would be cool to have this in a decorator with hmm grey? text color
			if (author.getEmail() != null && author.getEmail().trim() != "" && !author.getEmail().trim().equals("")) {
				sb.append(" <" + author.getEmail().trim() + ">");
			}
			
			if (author.getHomepage() != null && author.getHomepage().trim() != "" && !author.getHomepage().trim().equals("")) {
				sb.append(" - " + author.getHomepage().trim());
			}
			
			return sb.toString();
		}
	}
	
	public AuthorSection(ComposerFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION, new String[]{"Add...", "Edit...", "Remove"});
		createClient(getSection(), page.getManagedForm().getToolkit());
	}
	

	@Override
	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText("Authors");
		section.setDescription("Honour the glorious authors of this package.");
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		Composite container = createClientContainer(section, 2, toolkit);
		createViewerPartControl(container, SWT.MULTI, 2, toolkit);
		TablePart tablePart = getTablePart();
		AuthorController authorController = new AuthorController();
		authorViewer = tablePart.getTableViewer();
		authorViewer.setContentProvider(authorController);
		authorViewer.setLabelProvider(authorController);
		
		toolkit.paintBordersFor(container);
		section.setClient(container);
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));

		authorViewer.setInput(composerPackage.getAuthors());
		composerPackage.getAuthors().addPropertyChangeListener("authors", this);
		updateButtons();
		
		makeActions();
		updateMenu();
	}
	
	protected boolean createCount() {
		return true;
	}
	
	private void updateButtons() {
		ISelection selection = authorViewer.getSelection();
		
		TablePart tablePart = getTablePart();
		tablePart.setButtonEnabled(EDIT_INDEX, !selection.isEmpty());
		tablePart.setButtonEnabled(REMOVE_INDEX, !selection.isEmpty());
	}
	
	private void updateMenu() {
		IStructuredSelection selection = (IStructuredSelection)authorViewer.getSelection();
		
		editAction.setEnabled(selection.size() > 0);
		removeAction.setEnabled(selection.size() > 0);
	}

	public void refresh() {
		authorViewer.refresh();
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
		addAction = new Action("Add...") {
			@Override
			public void run() {
				handleAdd();
			}
		};
		
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
		manager.add(addAction);
		manager.add(editAction);
		manager.add(removeAction);
	}
	
	private void handleAdd() {
		PersonDialog diag = new PersonDialog(authorViewer.getTable().getShell(), new Person());
		if (diag.open() == Dialog.OK) {
			composerPackage.getAuthors().add(diag.getPerson());
			refresh();
		}
	}
	
	private void handleEdit() {
		Person author = (Person)((StructuredSelection)authorViewer.getSelection()).getFirstElement();
		PersonDialog diag = new PersonDialog(authorViewer.getTable().getShell(), author.clone());
		if (diag.open() == Dialog.OK) {
			author = diag.getPerson();
			refresh();
		}
	}
	
	private void handleRemove() {
		Person author = (Person)((StructuredSelection)authorViewer.getSelection()).getFirstElement();
		MessageDialog diag = new MessageDialog(
				authorViewer.getTable().getShell(), 
				"Remove Author", 
				null, 
				"Do you really wan't to remove " + author.getName() + "?", 
				MessageDialog.WARNING,
				new String[] {"Yes", "No"},
				0);
		
		if (diag.open() == Dialog.OK) {
			composerPackage.getAuthors().remove(author);
			refresh();
		}
	}
	
	@Override
	protected void buttonSelected(int index) {
		switch (index) {
		case ADD_INDEX:
			handleAdd();
			break;
			
		case EDIT_INDEX:
			handleEdit();
			break;
			
		case REMOVE_INDEX:
			handleRemove();
			break;
		}
	}
}
