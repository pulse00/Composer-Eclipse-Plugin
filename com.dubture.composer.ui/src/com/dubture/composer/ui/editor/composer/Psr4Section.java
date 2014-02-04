package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ResourceTransfer;

import com.dubture.composer.ui.controller.PsrController;
import com.dubture.composer.ui.dialogs.PsrDialog;
import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.editor.TreeSection;
import com.dubture.composer.ui.parts.TreePart;
import com.dubture.getcomposer.core.collection.Psr;
import com.dubture.getcomposer.core.objects.Namespace;

public class Psr4Section extends TreeSection implements PropertyChangeListener {

	protected TreeViewer psr4Viewer;

	private IAction addAction;
	private IAction editAction;
	private IAction removeAction;

	private static final int ADD_INDEX = 0;
	private static final int EDIT_INDEX = 1;
	private static final int REMOVE_INDEX = 2;
	
	private Psr psr4;

	public Psr4Section(ComposerFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION, new String[] { "Add...", "Edit...", "Remove" });
		createClient(getSection(), page.getManagedForm().getToolkit());
		
		psr4 = composerPackage.getAutoload().getPsr4();
	}

	@Override
	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText("psr-4");
		section.setDescription("Manage the psr-4 settings for your package.");
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessVerticalSpace = true;
		section.setLayoutData(gd);

		Composite container = createClientContainer(section, 2, toolkit);
		createViewerPartControl(container, SWT.SINGLE, 2, toolkit);
		TreePart treePart = getTreePart();
		PsrController controller = new PsrController(treePart.getTreeViewer());
		psr4Viewer = treePart.getTreeViewer();
		psr4Viewer.setContentProvider(controller);
		psr4Viewer.setLabelProvider(controller);

		Transfer[] transferTypes = new Transfer[] { ResourceTransfer.getInstance() };
		int types = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK | DND.DROP_DEFAULT;
		psr4Viewer.addDropSupport(types, transferTypes, new PathDropAdapter(psr4Viewer));

		toolkit.paintBordersFor(container);
		section.setClient(container);
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));

		psr4Viewer.setInput(composerPackage.getAutoload().getPsr4());
		composerPackage.getAutoload().addPropertyChangeListener(this);

		updateButtons();
		makeActions();
		updateMenu();
	}

	private void updateButtons() {
		ISelection selection = psr4Viewer.getSelection();

		TreePart treePart = getTreePart();
		treePart.setButtonEnabled(ADD_INDEX, enabled);
		treePart.setButtonEnabled(EDIT_INDEX, !selection.isEmpty() && enabled);
		treePart.setButtonEnabled(REMOVE_INDEX, !selection.isEmpty() && enabled);
	}

	private void updateMenu() {
		IStructuredSelection selection = (IStructuredSelection) psr4Viewer.getSelection();
		editAction.setEnabled(selection.size() > 0);
		removeAction.setEnabled(selection.size() > 0);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		updateButtons();
		
		refresh();
		psr4Viewer.getTree().setEnabled(enabled);
	}

	public void refresh() {
		psr4Viewer.refresh();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().startsWith("psr-4")) {
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
		PsrDialog dialog = new PsrDialog(psr4Viewer.getTree().getShell(), 
				new Namespace(), 
				getPage().getComposerEditor().getProject());

		if (dialog.open() == Dialog.OK) {
			psr4.add(dialog.getNamespace());
		}
	}

	private void handleEdit() {
		
		Namespace namespace = null;
		Object element = ((StructuredSelection) psr4Viewer.getSelection()).getFirstElement();
		
		// get parent if element is string
		if (element instanceof String) {
			element = ((PsrController)psr4Viewer.getContentProvider()).getParent(element);
		}
		
		if (element instanceof Namespace) {
			namespace = ((Namespace) element).clone();
		}
				 
		if (namespace != null) {
			PsrDialog diag = new PsrDialog(psr4Viewer.getTree().getShell(), 
					namespace.clone(), 
					getPage().getComposerEditor().getProject());
			
			if (diag.open() == Dialog.OK) {
				Namespace nmspc = psr4.get(diag.getNamespace().getNamespace());
				nmspc.clear();
				nmspc.addPaths(diag.getNamespace().getPaths());
			}
		}
	}

	private void handleRemove() {
		Object element = ((StructuredSelection) psr4Viewer.getSelection()).getFirstElement();
		
		if (element instanceof Namespace) {
			psr4.remove((Namespace) element);
		} else if (element instanceof String) {
			Namespace namespace = (Namespace)((PsrController)psr4Viewer.getContentProvider()).getParent(element);
			namespace.remove((String)element);
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
	
	private class PathDropAdapter extends ViewerDropAdapter {

		private Namespace target;
		
		public PathDropAdapter(Viewer viewer) {
			super(viewer);
		}

		@Override
		public boolean performDrop(Object data) {
			if (data instanceof IResource[]) {
				IResource[] resources = (IResource[]) data;
				
				List<IFolder> folders = new ArrayList<IFolder>();

				for (IResource resource : resources) {
					if (resource instanceof IFolder) {
						folders.add((IFolder) resource);
					}
				}
				
				for (IFolder folder : folders) {
					target.add(folder.getProjectRelativePath().toString());
				}
				return false;
			}
			
			return false;
		}

		@Override
		public boolean validateDrop(Object target, int operation, TransferData transferType) {
			
			if (target instanceof Namespace) {
				this.target = (Namespace) target;
				return true;
			}
			
			return false;
		}
	}
}
