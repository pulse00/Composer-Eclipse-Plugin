package com.dubture.composer.ui.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.php.internal.ui.util.TypedViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.osgi.service.prefs.BackingStoreException;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.PreferenceHelper;
import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.utils.WidgetHelper;

@SuppressWarnings("restriction")
public class BuildPathManagementPage extends PropertyPage {
	
	private PathSection includes;
	private PathSection excludes;
	
	public BuildPathManagementPage() {
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		WidgetHelper.setMargin(container, 0, 0);
		WidgetHelper.setSpacing(container, 5, 0);
		WidgetHelper.trimComposite(container, 0, 0, 5);
		
		Label description = new Label(container, SWT.WRAP);
		description.setText(
				"Eclipse Composer Plugin can take care of your build path, " +
				"whenever your dependencies change through command line or in the composer editor. " +
				"The composer build path manager takes care of keeping your project build path in sync " +
				"with the installed packages. It's done by scanning all installed packages, reading each " +
				"composer.json file, finding the the paths for their sources and puts them " +
				"into your build path configuration. Additionally, you can select folders to include " +
				"rsp. exclude in your build path below.");
		
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.widthHint = 350;
		description.setLayoutData(gd);
		
		Link activation = new Link(container, SWT.WRAP);
		activation.setText("Activate this feature by enable/disable the <a>Composer Build Path " +
				"Management</a> Builder.");
		activation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer)getContainer();
				container.openPage("org.eclipse.ui.externaltools.propertypages.BuilderPropertyPage", null);
				// Builders Page ID from:
				// http://grepcode.com/file/repository.grepcode.com/java/eclipse.org/3.5.2/org.eclipse.ui/externaltools/3.2.0/plugin.xml?av=f
			};
		});
		
		Composite paths = new Composite(container, SWT.NONE);
		paths.setLayout(new GridLayout());
		paths.setLayoutData(new GridData(GridData.FILL_BOTH));
		WidgetHelper.setMargin(paths, 0, 0);
		WidgetHelper.setSpacing(paths, 5, 0);
		
		IProject project = null;
		if (getElement() instanceof IProject) {
			project = (IProject) getElement();
		} else if (getElement() instanceof IScriptProject) {
			project = ((IScriptProject) getElement()).getProject();
		}
		
		includes = new PathSection(paths, project, true);
		excludes = new PathSection(paths, project, false);
		
		noDefaultAndApplyButton();
		
		return container;
	}
	
	

	@Override
	public boolean performOk() {
		if (includes != null) {
			includes.save();
		}
		
		if (excludes != null) {
			excludes.save();
		}

		return super.performOk();
	}
	
	private class PathSection {
		
		private IAction addAction;
		private IAction removeAction;
		
		private TableViewer pathViewer;
		private Button addButton;
		private Button removeButton;
		
		private IProject project;
		private IEclipsePreferences prefs;
		private String key;
		private List<String> paths = new ArrayList<String>();
		
		class PathController extends LabelProvider implements IStructuredContentProvider {

			private List<String> paths;
			private Image pathImage;
			
			public PathController(boolean include) {
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
		
		public PathSection(Composite parent, IProject project, boolean include) {
			this.project = project;
			key = "buildpath." + (include ? "include" : "exclude");
			prefs = ComposerPlugin.getDefault().getProjectPreferences(project);
			paths.addAll(Arrays.asList(PreferenceHelper.deserialize(prefs.get(key, ""))));
			makeActions();
			createClient(parent, include);
		}
		
		protected void createClient(Composite parent, boolean include) {
			Composite container = new Composite(parent, SWT.NONE);
			container.setLayout(new GridLayout());
			container.setLayoutData(new GridData(GridData.FILL_BOTH));
			WidgetHelper.setMargin(container, 0, 0);
			WidgetHelper.setSpacing(container, 5, 5);
			
			Label heading = new Label(container, SWT.NONE);
			heading.setLayoutData(new GridData(GridData.FILL));
			heading.setText(include ? "Include" : "Exclude" + ":");
			
			Composite contents = new Composite(container, SWT.NONE);
			contents.setLayout(new GridLayout(2, false));
			contents.setLayoutData(new GridData(GridData.FILL_BOTH));
			WidgetHelper.setMargin(contents, 0, 2);
			WidgetHelper.setSpacing(contents, 5, 5);
			
			PathController controller = new PathController(include);
			pathViewer = new TableViewer(contents);
			pathViewer.setContentProvider(controller);
			pathViewer.setLabelProvider(controller);
			pathViewer.setInput(paths);
			pathViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					updateMenu();
					updateButtons();
				}
			});
			
			Table table = pathViewer.getTable();
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.minimumHeight = 100;
			table.setLayoutData(gd);
			WidgetHelper.trimComposite(table, 0, 0, 0, 10);
			
			
			// menu
			MenuManager popupMenuManager = new MenuManager();
			IMenuListener listener = new IMenuListener() {
				public void menuAboutToShow(IMenuManager mng) {
					fillContextMenu(mng);
				}
			};
			popupMenuManager.addMenuListener(listener);
			popupMenuManager.setRemoveAllWhenShown(true);
			
			Menu menu = popupMenuManager.createContextMenu(table);
			table.setMenu(menu);
			
			// buttons
			Composite buttons = new Composite(contents, SWT.NONE);
			gd = new GridData(GridData.BEGINNING, GridData.FILL_VERTICAL, false, true);
			gd.widthHint = 80;
			buttons.setLayoutData(gd);
			buttons.setLayout(new GridLayout());
			WidgetHelper.setMargin(buttons, -4, -4);
			WidgetHelper.setSpacing(buttons, -5, 0);
			
			addButton = new Button(buttons, SWT.PUSH);
			addButton.setText("Add...");
			addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			addButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					handleAdd();
				}
			});
			
			removeButton = new Button(buttons, SWT.PUSH);
			removeButton.setText("Remove");
			removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			removeButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					handleRemove();
				}
			});
			
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
		
		private void updateButtons() {
			ISelection selection = pathViewer.getSelection();
			
			removeButton.setEnabled(!selection.isEmpty());
		}
		
		private void updateMenu() {
			IStructuredSelection selection = (IStructuredSelection)pathViewer.getSelection();

			removeAction.setEnabled(selection.size() > 0);
		}
		
		private void handleAdd() {
			CheckedTreeSelectionDialog dialog = new CheckedTreeSelectionDialog(
					pathViewer.getTable().getShell(), 
					new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
			
			dialog.addFilter(new TypedViewerFilter(new Class[] { IFolder.class }));
			dialog.setTitle("Tree Selection");
			dialog.setMessage("Select the elements from the tree:");
			dialog.setInput(project);
			dialog.setHelpAvailable(false);
			
			if (dialog.open() == Dialog.OK) {
				for (Object result : dialog.getResult()) {
					if (result instanceof IFolder) {
						paths.add(((IFolder)result).getProjectRelativePath().toString());
					}
				}
				pathViewer.refresh();
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
					"Remove Path" + (selection.size() > 1 ? "s" : ""), 
					null, 
					"Do you really wan't to remove " + StringUtils.join(names, ", ") + "?", 
					MessageDialog.WARNING,
					new String[] {"Yes", "No"},
					0);
			
			if (diag.open() == Dialog.OK) {
				for (String path : folders) {
					paths.remove(path);
				}
				pathViewer.refresh();
			}
		}
		
		protected void fillContextMenu(IMenuManager manager) {
			manager.add(addAction);
			manager.add(removeAction);
		}
		
		public void save() {
			prefs.put(key, PreferenceHelper.serialize(paths.toArray(new String[]{})));
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
	}
}
