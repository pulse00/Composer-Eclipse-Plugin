/**
 * 
 */
package com.dubture.composer.ui.editor.composer;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.getcomposer.core.Author;
import org.getcomposer.core.PackageInterface;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.dialogs.AuthorDialog;
import org.eclipse.swt.layout.FillLayout;

/**
 * @author Thomas Gossmann
 * 
 */
public class DependenciesPage extends FormPage {

	public final static String ID = "com.dubture.composer.ui.editor.composer.DependencyPage";

	private PackageInterface phpPackage;
	protected ComposerEditor editor;

	protected Composite left;
	protected Composite right;

	protected Section requireSection;
	protected TableViewer requireView;
	protected Button requireEdit;
	protected Button requireRemove;
	
	protected Section requireDevSection;
	protected TableViewer requireDevView;
	protected Button requireDevEdit;
	protected Button requireDevRemove;
	
	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public DependenciesPage(ComposerEditor editor, String id, String title) {
		super(editor, id, title);
		this.editor = editor;
		phpPackage = editor.getPHPPackge();
	}
	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		
		if (active) {
			editor.getHeaderForm().getForm().setText("Dependencies");
		}
	}


	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		managedForm.getForm().getBody().setLayout(new FillLayout(SWT.HORIZONTAL));
		
		left = toolkit.createComposite(form.getBody());
		
		createRequireSection(left, toolkit);
		createRequireDevSection(left, toolkit);
		
		right = toolkit.createComposite(form.getBody());
		right.setLayout(new TableWrapLayout());
		
	}

	private void createRequireSection(Composite parent, FormToolkit toolkit) {
		left.setLayout(new GridLayout(1, false));
		requireSection = toolkit.createSection(parent, Section.EXPANDED | Section.DESCRIPTION | Section.TWISTIE | Section.TITLE_BAR);
		requireSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		requireSection.setText("Require");
		requireSection.setDescription("The dependencies for your package.");
		requireSection.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				requireDevSection.setExpanded(!e.getState());
				((GridData)requireSection.getLayoutData()).grabExcessVerticalSpace = e.getState();
				((GridData)requireDevSection.getLayoutData()).grabExcessVerticalSpace = !e.getState();
			}
			
			public void expansionStateChanged(ExpansionEvent e) {
			}
		});

		Composite client = toolkit.createComposite(requireSection);
		requireSection.setClient(client);

		DependencyController controller = new DependencyController();
		GridLayout gl_client = new GridLayout();
		gl_client.numColumns = 2;
		client.setLayout(gl_client);
		requireView = new TableViewer(client, SWT.BORDER | SWT.V_SCROLL);
		requireView.setContentProvider(controller);
		requireView.setLabelProvider(controller);
		requireView.setInput(phpPackage.getRequire());
		requireView.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite buttons = toolkit.createComposite(client);
		buttons.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		buttons.setLayout(new GridLayout(1, false));
		
		requireEdit = toolkit.createButton(buttons, "Edit...", SWT.PUSH);
		requireEdit.setEnabled(false);
		requireEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		requireEdit.addMouseListener(new EditMouseAdapter());
		
		requireRemove = toolkit.createButton(buttons, "Remove", SWT.PUSH);
		requireRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		requireRemove.setEnabled(false);
		requireRemove.addMouseListener(new RemoveMouseAdapter());
		
		requireView.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = ((StructuredSelection)event.getSelection());
				boolean enabled = selection.size() > 0;
				requireEdit.setEnabled(enabled);
				requireRemove.setEnabled(enabled);
			}
		});
	}
	
	private void createRequireDevSection(Composite parent, FormToolkit toolkit) {
		requireDevSection = toolkit.createSection(parent, Section.DESCRIPTION | Section.TWISTIE | Section.TITLE_BAR);
		requireDevSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		requireDevSection.setText("Require (Development)");
		requireDevSection.setExpanded(false);
		requireDevSection.setDescription("The development dependencies for your package.");
		requireDevSection.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				requireSection.setExpanded(!e.getState());
				((GridData)requireSection.getLayoutData()).grabExcessVerticalSpace = !e.getState();
				((GridData)requireDevSection.getLayoutData()).grabExcessVerticalSpace = e.getState();
			}
			
			public void expansionStateChanged(ExpansionEvent e) {}
		});

		Composite client = toolkit.createComposite(requireDevSection);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		client.setLayout(layout);
		requireDevSection.setClient(client);

		DependencyController controller = new DependencyController();
		requireDevView = new TableViewer(client, SWT.BORDER | SWT.V_SCROLL);
		requireDevView.setContentProvider(controller);
		requireDevView.setLabelProvider(controller);
		requireDevView.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		requireDevView.setInput(phpPackage.getRequireDev());
		
		Composite buttons = toolkit.createComposite(client);
		buttons.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		buttons.setLayout(new GridLayout(1, false));
		
		requireDevEdit = toolkit.createButton(buttons, "Edit...", SWT.PUSH);
		requireDevEdit.setEnabled(false);
		requireDevEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		requireDevEdit.addMouseListener(new EditMouseAdapter());
		
		requireDevRemove = toolkit.createButton(buttons, "Remove", SWT.PUSH);
		requireDevRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		requireDevRemove.setEnabled(false);
		requireDevRemove.addMouseListener(new RemoveMouseAdapter());
		
		requireDevView.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = ((StructuredSelection)event.getSelection());
				boolean enabled = selection.size() > 0;
				requireDevEdit.setEnabled(enabled);
				requireDevRemove.setEnabled(enabled);
			}
		});
	}
	
	class EditMouseAdapter extends MouseAdapter {
		
	}
	
	class RemoveMouseAdapter extends MouseAdapter {
		
	}

	class DependencyController extends LabelProvider implements ITableLabelProvider, IStructuredContentProvider {

		private Map<String,String> authors;
		private Image authorImage = ComposerUIPluginImages.AUTHOR.createImage();

		@SuppressWarnings("unchecked")
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			authors = (Map<String,String>)newInput;
		}

		public Object[] getElements(Object inputElement) {
			return authors.keySet().toArray();
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return authorImage;
		}

		public String getColumnText(Object element, int columnIndex) {
//			Map<String,String> author = (Map<String, String>)element;
//			StringBuilder sb = new StringBuilder();
//			sb.append(author.getName());
//			
//			// TODO: would be cool to have this in a decorator with hmm grey? text color
//			if (author.getEmail() != null && author.getEmail() != "") {
//				sb.append(" <" + author.getEmail() + ">");
//			}
//			
//			if (author.getHomepage() != null && author.getHomepage() != "") {
//				sb.append(" - " + author.getHomepage());
//			}
			
			return null;
		}
	}
}
