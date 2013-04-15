package com.dubture.composer.ui.editor.composer;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.resources.IComposerProject;
import com.dubture.composer.ui.controller.GraphController;
import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.toolbar.SearchControl;
import com.dubture.getcomposer.core.ComposerPackage;
import com.dubture.getcomposer.core.collection.ComposerPackages;

/**
 * @author Robert Gruendler <r.gruendler@gmail.com>
 */
public class DependencyGraphPage extends ComposerFormPage implements ModifyListener {

	public final static String ID = "com.dubture.composer.ui.editor.composer.DependencyGraphPage";
	protected ComposerFormEditor editor;
	private GraphController graphController;
	private GraphViewer viewer;
	private IComposerProject composerProject;
	private IProject project;
	private final SearchControl searchControl;
	
	
	public DependencyGraphPage(ComposerFormEditor editor, String id, String title, SearchControl searchControl) {
		super(editor, id, title);
		this.editor = editor;
		this.searchControl = searchControl;
		this.searchControl.addModifyListener(this);
	}
	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if (active) {
			editor.getHeaderForm().getForm().setText("Dependency Graph");
		}
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		try {
			createGraph(managedForm);
		} catch (IOException e) {
			Logger.logException(e);
		}
	}
	
	private void createGraph(IManagedForm managedForm) throws IOException {
		
		ScrolledForm form = managedForm.getForm();
		Composite body = form.getBody();
		body.setLayout(new FillLayout());
		
		project = getComposerEditor().getProject();
		composerProject = ComposerPlugin.getDefault().getComposerProject(project);
		graphController = new GraphController(composerProject);
		graphController.setComposerProject(composerProject);
		viewer = new GraphViewer(body, SWT.BORDER);
		viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		viewer.setContentProvider(graphController);
		viewer.setLabelProvider(graphController);
		viewer.setLayoutAlgorithm(setLayout());
		viewer.applyLayout();

		DevFilter filter = new DevFilter();
		ViewerFilter[] filters = new ViewerFilter[1];
		filters[0] = filter;
		viewer.setFilters(filters);
		
		update();
	}
	
	private LayoutAlgorithm setLayout() {
		LayoutAlgorithm layout;
		layout = new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, 
				new	LayoutAlgorithm[] {
					new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), 
					new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) });
		return layout;
	}
	
	private class DevFilter extends ViewerFilter {
		protected boolean showDev = true;

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (showDev == true) {
				return true;
			}

			if (element instanceof ComposerPackage) {
				return !composerProject.getComposerPackage().getRequireDev().has((ComposerPackage)element);
			}

			return true;
		}

		public void hideDevPackages() {
			showDev = false;
		}
	}
	
	protected void update() {
		if (composerProject != null && viewer != null && viewer != null && !viewer.getControl().isDisposed()) {
			ComposerPackages packages = composerProject.getAllInstalledPackages();
			packages.add(composerProject.getComposerPackage());
			viewer.setInput(packages);
			applyFilter(true);
		}
	}
	
	public void applyFilter(boolean showDev) {
		DevFilter filter = new DevFilter();

		if (showDev == false) {
			filter.hideDevPackages();
		}

		ViewerFilter[] filters = new ViewerFilter[1];
		filters[0] = filter;
		viewer.setFilters(filters);
		viewer.applyLayout();
	}

	@Override
	public void modifyText(ModifyEvent e) {
		long start = System.nanoTime();
		
		graphController.setFilterText(searchControl.getText());
		viewer.refresh();
		
		double elapsed = (System.nanoTime() - start) * 1.0e-9;
		
		System.err.println("refresh inaaa " + elapsed);
	}
}
