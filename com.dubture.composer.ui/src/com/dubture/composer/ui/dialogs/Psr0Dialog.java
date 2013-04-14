package com.dubture.composer.ui.dialogs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.ComposerUIPluginConstants;
import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.controller.PathController;
import com.dubture.composer.ui.utils.WidgetHelper;
import com.dubture.getcomposer.core.objects.Namespace;

public class Psr0Dialog extends Dialog {

	private Text namespaceControl;
	
	private Namespace namespace;
	private IProject project;
	
	private TableViewer pathViewer;

	public Psr0Dialog(Shell parentShell, Namespace namespace, IProject project) {
		super(parentShell);
		this.namespace = namespace;
		this.project = project;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Edit Namespace");
		getShell().setImage(ComposerUIPluginImages.EVENT.createImage());
		
		Composite contents = new Composite(parent, SWT.NONE);
		contents.setLayout(new GridLayout(3, false));
		GridData gd_contents = new GridData();
		gd_contents.widthHint = 350;
		contents.setLayoutData(gd_contents);
		
		
		Label lblEvent = new Label(contents, SWT.NONE);
		GridData gd_lblEvent = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblEvent.widthHint = ComposerUIPluginConstants.DIALOG_LABEL_WIDTH;
		lblEvent.setLayoutData(gd_lblEvent);
		lblEvent.setText("Namespace");
		
		namespaceControl = new Text(contents, SWT.BORDER);
		GridData gd_eventControl = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_eventControl.widthHint = ComposerUIPluginConstants.DIALOG_CONTROL_WIDTH;
		namespaceControl.setLayoutData(gd_eventControl);
		
		if (namespace.getNamespace() != null) {
			namespaceControl.setText(namespace.getNamespace());
		}
		
		namespaceControl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				namespace.setNamespace(namespaceControl.getText());
			}
		});
		
		
		Label lblHandler = new Label(contents, SWT.NONE);
		lblHandler.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		lblHandler.setText("Paths");
		
		PathController controller = new PathController();
		pathViewer = new TableViewer(contents, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.minimumHeight = 100;
		pathViewer.getTable().setLayoutData(gridData);
		pathViewer.setContentProvider(controller);
		pathViewer.setLabelProvider(controller);
		pathViewer.setInput(namespace.getPaths());
		
		Composite buttons = new Composite(contents, SWT.NONE);
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		buttons.setLayout(new GridLayout(1, false));
		
		WidgetHelper.trimComposite(buttons, 0);
		WidgetHelper.setMargin(buttons, -3, -3);
		WidgetHelper.setSpacing(buttons, -4, 0);
		
		Button btnEdit = new Button(buttons, SWT.NONE);
		btnEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnEdit.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		btnEdit.setText("Edit...");
		btnEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				List<IFolder> folders = new ArrayList<IFolder>();
				for (Object path : namespace.getPaths()) {
					IResource resource = project.findMember((String)path);
					if (resource != null && resource instanceof IFolder) {
						folders.add((IFolder) resource);
					}
				}
				CheckedTreeSelectionDialog dialog = ResourceDialog.createMulti(
						pathViewer.getTable().getShell(), 
						"Namespace Paths", 
						"Select folders:", 
						new Class[] {IFolder.class}, 
						project, folders);

				if (dialog.open() == Dialog.OK) {
					namespace.clear();
					for (Object result : dialog.getResult()) {
						if (result instanceof IFolder) {
							namespace.add(((IFolder)result).getProjectRelativePath().toString());
						}
					}
				}
			}
		});
		
		Button btnRemove = new Button(buttons, SWT.NONE);
		btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		btnRemove.setText("Remove");
		
		btnRemove.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = pathViewer.getSelection();
				if (!(selection instanceof StructuredSelection)) {
					return;
				}
				StructuredSelection s = (StructuredSelection) selection;
				for (Object o : s.toArray() ) {
					try {
						String item = (String) o;
						pathViewer.remove(item);
						namespace.remove(item);
					} catch (Exception e2) {
						Logger.logException(e2);
					}
				}
			}
		});
		
		namespace.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().contains("#")) {
					pathViewer.refresh();
				}
			}
		});
		
		return contents;		
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
