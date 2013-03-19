package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.getcomposer.core.collection.JsonArray;
import org.getcomposer.core.objects.Scripts;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.dialogs.ScriptDialog;
import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.editor.TreeSection;
import com.dubture.composer.ui.parts.TreePart;

public class ScriptsSection extends TreeSection implements PropertyChangeListener {

	private TreeViewer scriptsViewer;
	
	private IAction addAction;
	private IAction editAction;
	private IAction removeAction;
	
	private static final int ADD_INDEX = 0;
	private static final int EDIT_INDEX = 1;
	private static final int REMOVE_INDEX = 2;

	class ScriptsController extends StyledCellLabelProvider implements ITreeContentProvider {

		private Scripts scripts;
		private Image eventImage = ComposerUIPluginImages.EVENT.createImage();
		private Image scriptImage = ComposerUIPluginImages.SCRIPT.createImage();

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			scripts = (Scripts)newInput;
		}
		
		public String getText(Object element) {
			return element.toString();
		}
		
		public void update(ViewerCell cell) {
			Object obj = cell.getElement();
			String text = getText(obj);
			
			StyledString styledString = new StyledString(text);
			
			if (Arrays.asList(Scripts.getEvents()).contains(text)) {
				int count = composerPackage.getScripts().getAsArray(text).size();
				styledString.append(" (" + count + ")", StyledString.COUNTER_STYLER);
				
				cell.setImage(eventImage);
			} else {
				cell.setImage(scriptImage); 
			}
			
			cell.setText(styledString.toString());
			cell.setStyleRanges(styledString.getStyleRanges());
			
			super.update(cell);
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}
		
		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Scripts) {
				Scripts scripts = (Scripts) parentElement;
				List<String> children = new ArrayList<String>();
				
				for (String event : Scripts.getEvents()) {
					if (scripts.has(event)) {
						children.add(event);
					}
				}
				
				return children.toArray();
			} else {
				String text = parentElement.toString();
				if (Arrays.asList(Scripts.getEvents()).contains(text)) {
					return scripts.getAsArray(text).toArray();
				}
			}
			
			return new Object[]{};
		}

		@Override
		public Object getParent(Object element) {
			TreeItem item = null;
			for (TreeItem ri : scriptsViewer.getTree().getItems()) {
				for (TreeItem i : ri.getItems()) {
					if (i.getData() == element) {
						item = i;
						break;
					}
				}
			}
			
			if (item != null) {
				TreeItem parent = item.getParentItem();
				if (parent == null) {
					return scripts;
				}
				
				if (parent.getData() != null) {
					return parent.getData();
				}
				
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
	}
	
	public ScriptsSection(ComposerFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION, new String[]{"Add...", "Edit...", "Remove"});
		createClient(getSection(), page.getManagedForm().getToolkit());
	}
	
	@Override
	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText("Scripts");
		section.setDescription("Manage the scripts for your package.");
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		Composite container = createClientContainer(section, 2, toolkit);
		createViewerPartControl(container, SWT.SINGLE, 2, toolkit);
		TreePart treePart = getTreePart();
		ScriptsController scriptsController = new ScriptsController();
		scriptsViewer = treePart.getTreeViewer();
		scriptsViewer.setContentProvider(scriptsController);
		scriptsViewer.setLabelProvider(scriptsController);
		
		toolkit.paintBordersFor(container);
		section.setClient(container);
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));

		scriptsViewer.setInput(composerPackage.getScripts());
		composerPackage.addPropertyChangeListener(this);
		updateButtons();
		
		makeActions();
		updateMenu();
	}
	
	protected boolean createCount() {
		return true;
	}
	
	private void updateButtons() {
		ISelection selection = scriptsViewer.getSelection();
		
		TreePart treePart = getTreePart();
		treePart.setButtonEnabled(EDIT_INDEX, !selection.isEmpty());
		treePart.setButtonEnabled(REMOVE_INDEX, !selection.isEmpty());
	}
	
	private void updateMenu() {
		IStructuredSelection selection = (IStructuredSelection)scriptsViewer.getSelection();
		
		editAction.setEnabled(selection.size() > 0);
		removeAction.setEnabled(selection.size() > 0);
	}

	public void refresh() {
		scriptsViewer.refresh();
		super.refresh();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().startsWith("scripts")) { 
			refresh();
		}
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
		ScriptDialog diag = new ScriptDialog(scriptsViewer.getTree().getShell());
		
		if (!scriptsViewer.getSelection().isEmpty()) {
			Object element = ((StructuredSelection)scriptsViewer.getSelection()).getFirstElement();
			ScriptsController controller = (ScriptsController)scriptsViewer.getLabelProvider();
			String text = controller.getText(element);
			if (Arrays.asList(Scripts.getEvents()).contains(text)) {
				diag.setEvent(text);
			}
		}
		
		if (diag.open() == Dialog.OK) {
			composerPackage.getScripts().getAsArray(diag.getEvent()).add(diag.getHandler());
			refresh();
		}
	}
	
	private void handleEdit() {
		Object element = ((StructuredSelection)scriptsViewer.getSelection()).getFirstElement();
		ScriptsController controller = (ScriptsController)scriptsViewer.getLabelProvider();
		String text = controller.getText(element);
		ScriptDialog diag = new ScriptDialog(scriptsViewer.getTree().getShell());
		
		// edit event
		if (Arrays.asList(Scripts.getEvents()).contains(text)) {
			diag.setEvent(text);
			diag.setHandlerEnabled(false);
			if (diag.open() == Dialog.OK) {
				String event = diag.getEvent();
				if (!event.equalsIgnoreCase(text)) {
					composerPackage.getScripts().set(event, 
							composerPackage.getScripts().getAsArray(text));
					composerPackage.getScripts().remove(text);
				}
			}
		} 
		
		// edit handler
		else {
			String event = controller.getText(controller.getParent(element));
			diag.setEvent(event);
			diag.setHandler(text);
			diag.setEventEnabled(false);
			if (diag.open() == Dialog.OK) {
				String handler = diag.getHandler();
				if (!handler.equalsIgnoreCase(text)) {
					JsonArray events = composerPackage.getScripts().getAsArray(event);
					events.replace(text, handler);
				}
			}
		}
	}
	
	private void handleRemove() {
		Object element = ((StructuredSelection)scriptsViewer.getSelection()).getFirstElement();
		ScriptsController controller = (ScriptsController)scriptsViewer.getLabelProvider();
		String text = controller.getText(element);
		
		// remove event
		if (Arrays.asList(Scripts.getEvents()).contains(text)) {
			MessageDialog diag = new MessageDialog(
				scriptsViewer.getTree().getShell(), 
				"Remove Event", 
				null, 
				"Do you really wan't to remove " + text + "?", 
				MessageDialog.WARNING,
				new String[] {"Yes", "No"},
				0);

			if (diag.open() == Dialog.OK) {
				composerPackage.getScripts().remove(text);
			}
		} 
		
		// remove handler
		else {
			String event = controller.getText(controller.getParent(element));
			
			MessageDialog diag = new MessageDialog(
					scriptsViewer.getTree().getShell(), 
					"Remove Event", 
					null, 
					"Do you really wan't to remove " + text + " in " + event + "?", 
					MessageDialog.WARNING,
					new String[] {"Yes", "No"},
					0);
			
			if (diag.open() == Dialog.OK) {
				JsonArray events = composerPackage.getScripts().getAsArray(event);
				events.remove(text);
			}
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
