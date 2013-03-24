package com.dubture.composer.ui.editor.composer.autoload;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.part.ResourceTransfer;
import org.getcomposer.core.collection.JsonArray;
import org.getcomposer.core.collection.Psr0;
import org.getcomposer.core.objects.Namespace;

import com.dubture.composer.ui.dialogs.Psr0Dialog;
import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.editor.TreeSection;
import com.dubture.composer.ui.parts.TreePart;

public class Psr0Section extends TreeSection implements PropertyChangeListener {

	protected TreeViewer psr0Viewer;

	private IAction addAction;
	private IAction editAction;
	private IAction removeAction;

	private static final int ADD_INDEX = 0;
	private static final int EDIT_INDEX = 1;
	private static final int REMOVE_INDEX = 2;

	public Psr0Section(ComposerFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION, new String[] { "Add...", "Edit...", "Remove" });
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
		Psr0ContentProvider controller = new Psr0ContentProvider(this);
		psr0Viewer = treePart.getTreeViewer();
		psr0Viewer.setContentProvider(controller);
		psr0Viewer.setLabelProvider(controller);

		Transfer[] transferTypes = new Transfer[] { ResourceTransfer.getInstance() };
		int types = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK | DND.DROP_DEFAULT;
		psr0Viewer.addDropSupport(types, transferTypes, new PathDropAdapter(psr0Viewer, this));

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
			for (String nsp : composerPackage.getAutoload().getPsr0()) {
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
		IStructuredSelection selection = (IStructuredSelection) psr0Viewer.getSelection();
		editAction.setEnabled(selection.size() > 0);
		removeAction.setEnabled(selection.size() > 0);
	}

	public void refresh() {
		
		Object[] elements = psr0Viewer.getExpandedElements();
		TreePath[] treePaths = psr0Viewer.getExpandedTreePaths();
		psr0Viewer.refresh();
		psr0Viewer.setExpandedElements(elements);
		psr0Viewer.setExpandedTreePaths(treePaths);
//		super.refresh();
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
				element = (NamespaceModel) ((StructuredSelection) psr0Viewer.getSelection()).getFirstElement();
				// TODO: get parent when user clicks on a path inside the tree
				// view
			} catch (ClassCastException e) {
				return;
			}

			System.err.println(element.key);
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
			element = (NamespaceModel) ((StructuredSelection) psr0Viewer.getSelection()).getFirstElement();
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

		Object element = ((StructuredSelection) psr0Viewer.getSelection()).getFirstElement();
		
		if (element instanceof NamespaceModel) {
			removeNamespace((NamespaceModel) element);
		} else if (element instanceof NamespacePath) {
			
			NamespacePath path = (NamespacePath) element;
			NamespaceModel namespaceModel = path.getParent();
			composerPackage.getAutoload().getPsr0().get(namespaceModel.key).remove(path.toString());
			refresh();
		}
	}

	private void removeNamespace(NamespaceModel element) {
		Psr0 psr0 = composerPackage.getAutoload().getPsr0();
		if (psr0.has(element.key)) {
			psr0.remove(element.key);
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

	public void dropTargetReceived(NamespaceModel target, List<IFolder> folders) {
		
		Psr0 psr0 = composerPackage.getAutoload().getPsr0();
		Namespace namespace = psr0.get(target.key);
		
		for (IFolder folder : folders) {
			namespace.add(folder.getFullPath().removeFirstSegments(1).toString());
		}

		refresh();
	}
}
