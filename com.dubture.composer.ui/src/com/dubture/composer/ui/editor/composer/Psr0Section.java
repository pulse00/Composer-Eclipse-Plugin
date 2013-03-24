package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
import org.getcomposer.core.collection.Psr0;
import org.getcomposer.core.objects.Namespace;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.dialogs.Psr0Dialog;
import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.editor.TreeSection;
import com.dubture.composer.ui.parts.TreePart;

public class Psr0Section extends TreeSection implements PropertyChangeListener {

	private TreeViewer psr0Viewer;
	
	private IAction addAction;
	private IAction editAction;
	private IAction removeAction;
	
	private static final int ADD_INDEX = 0;
	private static final int EDIT_INDEX = 1;
	private static final int REMOVE_INDEX = 2;

	public Psr0Section(ComposerFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION, new String[]{"Add...", "Edit...", "Remove"});
		createClient(getSection(), page.getManagedForm().getToolkit());
	}

	@Override
	protected void createClient(Section section, FormToolkit toolkit) {

		section.setText("psr-0");
		section.setDescription("Manage the psr-0 settings for your package.");
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		Composite container = createClientContainer(section, 2, toolkit);
		createViewerPartControl(container, SWT.SINGLE, 2, toolkit);
		TreePart treePart = getTreePart();
		Psr0Controller controller = new Psr0Controller();
		psr0Viewer = treePart.getTreeViewer();
		psr0Viewer.setContentProvider(controller);
		psr0Viewer.setLabelProvider(controller);
		
		toolkit.paintBordersFor(container);
		section.setClient(container);
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));

		psr0Viewer.setInput(composerPackage.getAutoload().getPsr0());
		composerPackage.addPropertyChangeListener(this);
		
		updateButtons();
		makeActions();
		updateMenu();
	}

	private void updateModel(String namespaceName, String paths) {

		Namespace ns = new Namespace();
		ns.setNamespace(namespaceName);
		
		for (Object p : getPaths(paths)) {
			ns.add((String) p);
		}
		
		if (composerPackage.getAutoload().getPsr0().has(namespaceName) == false) {
			composerPackage.getAutoload().getPsr0().add(ns);
		} else {
			
			Namespace toBeRemoved = null;
			for( String nsp : composerPackage.getAutoload().getPsr0()) {
				Namespace namespace = composerPackage.getAutoload().getPsr0().get(nsp);
				
				if (namespaceName.equals(namespace.getNamespace())) {
					toBeRemoved = namespace;
					break;
				}
			}
			
			if (toBeRemoved != null) {
				composerPackage.getAutoload().getPsr0().remove(toBeRemoved);
			}
			
			composerPackage.getAutoload().getPsr0().add(ns);
		}
		
		System.err.println("aha");
		System.err.println(composerPackage.toString());
	}
	
	private JsonArray getPaths(String paths) {

		JsonArray array = new JsonArray();
		
		if (paths.contains(",")) {
			String[] separatedPaths = paths.split(",");
			for (String path : separatedPaths) {
				array.add(path);
			}
		} else {
			array.add(paths);
		}
		
		return array;
	}

	private void updateButtons() {
		ISelection selection = psr0Viewer.getSelection();
		
		TreePart treePart = getTreePart();
		treePart.setButtonEnabled(EDIT_INDEX, !selection.isEmpty());
		treePart.setButtonEnabled(REMOVE_INDEX, !selection.isEmpty());
	}
	
	private void updateMenu() {
		IStructuredSelection selection = (IStructuredSelection)psr0Viewer.getSelection();
		editAction.setEnabled(selection.size() > 0);
		removeAction.setEnabled(selection.size() > 0);
	}

	public void refresh() {
		psr0Viewer.refresh();
		super.refresh();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().startsWith("psr-0")) { 
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
		
		Psr0Dialog dialog = new Psr0Dialog(psr0Viewer.getTree().getShell());
		
		if (!psr0Viewer.getSelection().isEmpty()) {
			
			NamespaceModel element = null;
			try {
				element = (NamespaceModel) ((StructuredSelection)psr0Viewer.getSelection()).getFirstElement();
			// TODO: get parent when user clicks on a path inside the tree view
			} catch (ClassCastException e) {
				return;
			}
			
			dialog.setNamespace(element.key);
			dialog.setPaths(element.getPathsAsString());
		}
		
		if (dialog.open() == Dialog.OK) {
			updateModel(dialog.getNamespace(), dialog.getPaths());
			refresh();
		}
	}
	
	private void handleEdit() {
		
		Psr0Dialog diag = new Psr0Dialog(psr0Viewer.getTree().getShell());
		NamespaceModel element = null;
		try {
			element = (NamespaceModel) ((StructuredSelection)psr0Viewer.getSelection()).getFirstElement();
		// TODO: get parent when user clicks on a path inside the tree view
		} catch (ClassCastException e) {
			return;
		}
		
		diag.setNamespace(element.key);
		diag.setPaths(element.getPathsAsString());
		
		if (diag.open() == Dialog.OK) {
			updateModel(diag.getNamespace(), diag.getPaths());
			refresh();
		}
	}
	
	private void handleRemove() {
		
		NamespaceModel element = null;
		
		try {
			element = (NamespaceModel) ((StructuredSelection)psr0Viewer.getSelection()).getFirstElement();
		// TODO: get parent when user clicks on a path inside the tree view
		} catch (ClassCastException e) {
			return;
		}
		
		MessageDialog diag = new MessageDialog(
				psr0Viewer.getTree().getShell(), 
				"Remove Namespace", 
				null, 
				"Do you really wan't to remove " + element.key + "?", 
				MessageDialog.WARNING,
				new String[] {"Yes", "No"},
				0);
		
		if (diag.open() == Dialog.OK) {
			Psr0 psr0 = composerPackage.getAutoload().getPsr0();
			if (psr0.has(element.key)) {
				psr0.remove(element.key);
				refresh();
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
	
	
	class Psr0Controller extends StyledCellLabelProvider implements ITreeContentProvider {

		private Psr0 psr0;
		private Image namespaceImage = ComposerUIPluginImages.NAMESPACE.createImage();
		private Image pathsImage = ComposerUIPluginImages.PACKAGE_FOLDER.createImage();
		
		public String getText(Object element) {
			return element.toString();
		}
		
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			psr0 = (Psr0)newInput;
			
		}
		
		public void update(ViewerCell cell) {
			Object obj = cell.getElement();
			String text = getText(obj);
			
			StyledString styledString = new StyledString(text);
			
			if (obj instanceof NamespaceModel) {
				NamespaceModel model = (NamespaceModel) obj;
				int count = model.namespace.size();
				styledString.append(" (" + count + ")", StyledString.COUNTER_STYLER);
				cell.setImage(namespaceImage);
			} else {
				cell.setImage(pathsImage); 
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

			if (parentElement instanceof Psr0) {
				Psr0 psr0 = (Psr0) parentElement;
				
				List<NamespaceModel> elements = new ArrayList<NamespaceModel>();
				
				Iterator<String> iterator = psr0.iterator();
				
				while(iterator.hasNext()) {
					String next = iterator.next();
					elements.add(new NamespaceModel(next, psr0.get(next)));
				}
				
				return elements.toArray();
			} else if (parentElement instanceof NamespaceModel) {
				NamespaceModel model =  (NamespaceModel) parentElement;
				return model.namespace.getAll().toArray();
			}
			
			return new Object[]{};
		}

		@Override
		public Object getParent(Object element) {
			TreeItem item = null;
			for (TreeItem ri : psr0Viewer.getTree().getItems()) {
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
					return psr0;
				}
				
				if (parent.getData() != null) {
					return parent.getData();
				}
			}
			
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			Object[] children = getChildren(element);
			return children != null && children.length > 0;
		}
	}
	
	private class NamespaceModel {

		public String key;
		public Namespace namespace;
		
		public NamespaceModel(String key, Namespace value) {
			this.key = key;
			this.namespace = value;
		}
		
		public String getPathsAsString() {

			StringBuilder builder = new StringBuilder();
			
			Object[] array = namespace.getAll().toArray();
			List<Object> list = new ArrayList<Object>(Arrays.asList(array));
			builder.append( list.remove(0));

			for(Object s : list) {
			    builder.append( ", ");
			    builder.append( ((String)s).trim());
			}

			String result = builder.toString();			
			return result;
		}

		@Override
		public String toString() {
			return key;
		}
	}
}
