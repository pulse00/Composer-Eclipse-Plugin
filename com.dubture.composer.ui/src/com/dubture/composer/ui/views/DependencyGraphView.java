package com.dubture.composer.ui.views;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.core.widgets.GraphNode;
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
import com.dubture.getcomposer.core.ComposerPackage;
import com.dubture.getcomposer.core.collection.ComposerPackages;

public class DependencyGraphView extends ViewPart implements
		IZoomableWorkbenchPart, ISelectionListener, IPartListener {
	public static String ID = "com.dubture.composer.ui.view.dependencyGraph";

	private IComposerProject composerProject;
	private IProject project;
	
	private GraphViewer viewer;
	private Action toggleDevAction;

	private boolean showDev = true;
	private GraphController graphController;
	
	public DependencyGraphView() {
		super();
	}
	

	@Override
	public void createPartControl(Composite parent) {
		/*
		getSite().getPage().addSelectionListener(this);
		getSite().getPage().addPartListener(this);
		selectionChanged(null, getSite().getPage().getSelection());
		if (project == null) {
			partActivated(getSite().getPage().getActivePart());
		}
		*/
		
		graphController = new GraphController(composerProject);
		viewer = new GraphViewer(parent, SWT.BORDER);
		viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		viewer.setContentProvider(graphController);
		viewer.setLabelProvider(graphController);
		viewer.setLayoutAlgorithm(setLayout());
		viewer.applyLayout();

		DevFilter filter = new DevFilter();
		ViewerFilter[] filters = new ViewerFilter[1];
		filters[0] = filter;
		viewer.setFilters(filters);
		
		parent.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				viewer.applyLayout();
			}
		});

		createActions();
		fillToolBar();
		update();
	}
	
	@Override
	public void dispose() {
		//getSite().getPage().removeSelectionListener(this);
		super.dispose();
	}

	protected ComposerPackage getSelectedPackage() {
		List<?> selection = viewer.getGraphControl().getSelection();

		if (selection != null && selection.size() == 1) {
			GraphNode node = (GraphNode) selection.get(0);
			return (ComposerPackage) node.getData();
		}

		return null;
	}

	protected void update() {
		if (composerProject != null && viewer != null && viewer != null && !viewer.getControl().isDisposed()) {
			ComposerPackages packages = composerProject.getAllInstalledPackages();
			packages.add(composerProject.getComposerPackage());
			viewer.setInput(packages);
			applyFilter(true);
		}
	}

	private void applyFilter(boolean showDev) {
		DevFilter filter = new DevFilter();

		if (showDev == false) {
			filter.hideDevPackages();
		}

		ViewerFilter[] filters = new ViewerFilter[1];
		filters[0] = filter;
		viewer.setFilters(filters);
		viewer.applyLayout();
	}

	private LayoutAlgorithm setLayout() {
		LayoutAlgorithm layout;
		layout = new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, 
				new	LayoutAlgorithm[] {
					new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), 
					new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) });
		return layout;
	}

	@Override
	public void setFocus() {

	}

	public void setProject(IProject project) {
		if (this.project == project) {
			return;
		}

		this.project = project;
		try {
			composerProject = ComposerPlugin.getDefault().getComposerProject(project);
			if (graphController != null) {
				graphController.setComposerProject(composerProject);
			}
		} catch (IOException e) {
			Logger.logException(e);
		}
		update();
	}

	private void fillToolBar() {
		/*
		ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(
				this);
		IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(toolbarZoomContributionViewItem);
		bars.getToolBarManager().add(toggleDevAction);
		*/
	}

	public void createActions() {
		toggleDevAction = new ToggleDevAction();
	}

	@Override
	public AbstractZoomableViewer getZoomableViewer() {
		return viewer;
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

	protected class ToggleDevAction extends Action {

		public ToggleDevAction() {
			super("Toggle dev packages");
			setDescription("Toggle dev packages");
			setToolTipText("Toggle dev packages");
			DLTKPluginImages.setLocalImageDescriptors(this,
					"th_showqualified.gif"); //$NON-NLS-1$
		}

		public void run() {
			showDev = !showDev;
			applyFilter(showDev);
		}
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object item = ((IStructuredSelection)selection).getFirstElement();

			IResource res = null;
			
			if (item instanceof IResource) {
				res = (IResource)item;
			} else if (item instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable)item;
				res = (IResource)adaptable.getAdapter(IResource.class);
			}
			
			if (res != null) {
				setProject(res.getProject());
			}
		}
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IEditorInput input = ((IEditorPart)part).getEditorInput();
			if (input instanceof IFileEditorInput) {
				setProject(((IFileEditorInput)input).getFile().getProject());
			}
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {}

	@Override
	public void partClosed(IWorkbenchPart part) {}

	@Override
	public void partDeactivated(IWorkbenchPart part) {}

	@Override
	public void partOpened(IWorkbenchPart part) {}

}
