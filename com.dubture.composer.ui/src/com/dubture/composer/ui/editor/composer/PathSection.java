package com.dubture.composer.ui.editor.composer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.php.internal.ui.util.TypedViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.osgi.service.prefs.BackingStoreException;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.PreferenceHelper;
import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.editor.TableSection;
import com.dubture.composer.ui.parts.TablePart;

@SuppressWarnings("restriction")
public class PathSection extends TableSection {

	private boolean include;
	private List<String> paths = new ArrayList<String>();
	private String key;
	private IEclipsePreferences prefs;
	private IProject project;
	
	private TableViewer pathViewer;
	
	private IAction addAction;
	private IAction removeAction;
	
	private static final int ADD_INDEX = 0;
	private static final int REMOVE_INDEX = 1;

	class PathController extends LabelProvider implements IStructuredContentProvider {

		private List<String> paths;
		private Image pathImage;
		
		public PathController() {
			if (include) {
				pathImage = ComposerUIPluginImages.BUILDPATH_INCLUDE.createImage();
			} else {
				pathImage = ComposerUIPluginImages.BUILDPATH_EXCLUDE.createImage();
			}
		}

		@SuppressWarnings("unchecked")
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			paths = (List<String>)newInput;
		}

		public Object[] getElements(Object inputElement) {
			return paths.toArray();
		}
		
		@Override
		public Image getImage(Object element) {
			return pathImage;
		}
	}
	
	public PathSection(ComposerFormPage page, Composite parent, boolean include) {
		super(page, parent, Section.DESCRIPTION, new String[]{"Add...", "Remove"});
		this.include = include;
		project = getPage().getComposerEditor().getProject();
		prefs = ComposerPlugin.getDefault().getProjectPreferences(project);
		key = "buildpath." + (include ? "include" : "exclude");
		createClient(getSection(), page.getManagedForm().getToolkit(), include);
	}
	

	protected void createClient(Section section, FormToolkit toolkit, boolean include) {
		if (include) {
			section.setText("Include");
			section.setDescription("Select the folders you want to include in your buildpath.");
		} else {
			section.setText("Exclude");
			section.setDescription("Select the folders you want to exclude from your buildpath.");
		}

		paths.addAll(Arrays.asList(PreferenceHelper.deserialize(prefs.get(key, ""))));
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));

		Composite container = createClientContainer(section, 2, toolkit);
		createViewerPartControl(container, SWT.MULTI, 2, toolkit);
		TablePart tablePart = getTablePart();
		PathController pathController = new PathController();
		pathViewer = tablePart.getTableViewer();
		pathViewer.setContentProvider(pathController);
		pathViewer.setLabelProvider(pathController);
		
		toolkit.paintBordersFor(container);
		section.setClient(container);
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));

		pathViewer.setInput(paths);
		pathViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		updateButtons();
		
		makeActions();
		updateMenu();
	}
	
	protected boolean createCount() {
		return true;
	}
	
	private void updateButtons() {
		ISelection selection = pathViewer.getSelection();
		
		TablePart tablePart = getTablePart();
		tablePart.setButtonEnabled(REMOVE_INDEX, !selection.isEmpty());
	}
	
	private void updateMenu() {
		IStructuredSelection selection = (IStructuredSelection)pathViewer.getSelection();

		removeAction.setEnabled(selection.size() > 0);
	}

	public void refresh() {
		pathViewer.refresh();
		super.refresh();
		getPage().getComposerEditor().setDirty(true);
	}
	
	protected void selectionChanged(IStructuredSelection sel) {
		updateButtons();
		updateMenu();
	}
	
	private void makeActions() {
		addAction = new Action("Add...") {
			public void run() {
				handleAdd();
			}
		};

		removeAction = new Action("Remove") {
			public void run() {
				handleRemove();
			}
		};
	}
	
	@Override
	protected void fillContextMenu(IMenuManager manager) {
		manager.add(addAction);
		manager.add(removeAction);
	}
	
	private void handleAdd() {
		CheckedTreeSelectionDialog dialog = new CheckedTreeSelectionDialog(
				pathViewer.getTable().getShell(), 
				new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		
		dialog.addFilter(new TypedViewerFilter(new Class[] { IFolder.class }));
		dialog.setTitle("Add to " + (include ? "Include" : "Exclude") + " Path");
		dialog.setMessage("Select folders:");
		dialog.setInput(project);
		dialog.setHelpAvailable(false);
		
		if (dialog.open() == Dialog.OK) {
			for (Object result : dialog.getResult()) {
				if (result instanceof IFolder) {
					paths.add(((IFolder)result).getProjectRelativePath().toString());
				}
			}
			refresh();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void handleRemove() {
		StructuredSelection selection = ((StructuredSelection)pathViewer.getSelection());
		Iterator<Object> it = selection.iterator();
		String[] names = new String[selection.size()];
		List<String> folders = new ArrayList<String>();

		for (int i = 0; it.hasNext(); i++) {
			String path = (String)it.next();
			folders.add(path);
			names[i] = path;
		}

		MessageDialog diag = new MessageDialog(
				pathViewer.getTable().getShell(), 
				"Remove Author" + (selection.size() > 1 ? "s" : ""), 
				null, 
				"Do you really wan't to remove " + StringUtils.join(names, ", ") + "?", 
				MessageDialog.WARNING,
				new String[] {"Yes", "No"},
				0);
		
		if (diag.open() == Dialog.OK) {
			for (String path : folders) {
				paths.remove(path);
			}
			refresh();
		}
	}
	
	@Override
	protected void buttonSelected(int index) {
		switch (index) {
		case ADD_INDEX:
			handleAdd();
			break;
			
		case REMOVE_INDEX:
			handleRemove();
			break;
		}
	}
	
	public void save() {
		prefs.put(key, PreferenceHelper.serialize(paths.toArray(new String[]{})));
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void createClient(Section section, FormToolkit toolkit) {}

}
