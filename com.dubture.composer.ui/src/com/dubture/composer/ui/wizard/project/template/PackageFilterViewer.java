package com.dubture.composer.ui.wizard.project.template;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.internal.p2.ui.discovery.util.ControlListItem;
import org.eclipse.equinox.internal.p2.ui.discovery.util.ControlListViewer;
import org.eclipse.equinox.internal.p2.ui.discovery.util.FilteredViewer;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.DiscoveryResources;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.wizard.project.template.PackagistItem.ICheckBoxListener;
import com.dubture.getcomposer.core.MinimalPackage;
import com.dubture.getcomposer.packages.AsyncPackagistSearch;
import com.dubture.getcomposer.packages.PackageSearchListenerInterface;
import com.dubture.getcomposer.packages.SearchResult;

@SuppressWarnings("restriction")
public class PackageFilterViewer extends FilteredViewer implements ICheckBoxListener {

	protected final static Object[] EMTPY = new Object[0];
	private DiscoveryResources resources;
	private Button showProjectsCheckbox;
	
	private PackagistContentProvider contentProvider;
	
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		doFind("");
	}
	
	@Override
	protected StructuredViewer doCreateViewer(Composite container) {
		
		resources = new DiscoveryResources(getControl().getDisplay());
		StructuredViewer viewer = new ControlListViewer(container, SWT.BORDER) {
			@Override
			protected ControlListItem<?> doCreateItem(Composite parent, Object element) {
				return doCreateViewerItem(parent, element);
			}
		};
		
		viewer.setContentProvider(contentProvider = new PackagistContentProvider());
		
		return viewer;
	}
	
	@Override
	protected void doCreateHeaderControls(Composite parent) {
		
		showProjectsCheckbox = new Button(parent, SWT.CHECK);
		showProjectsCheckbox.setSelection(true);
		showProjectsCheckbox.setText("Show projects only");
		showProjectsCheckbox.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				doFind(getFilterText());
			}
		});
	}
	
	protected ControlListItem<?> doCreateViewerItem(Composite parent, Object element) {
		if (element instanceof PackageFilterItem) {
			PackagistItem packagistItem = new PackagistItem(parent, SWT.NONE, resources, (PackageFilterItem) element);
			packagistItem.setCheckBoxListener(this);
			return packagistItem;
		}
		
		return null;
	}
	
	
	@Override
	protected void doFind(String text) {
		try {

			contentProvider.clear();
			viewer.setInput(contentProvider.packages);
			viewer.refresh();
			
			AsyncPackagistSearch search = new AsyncPackagistSearch();
			search.addPackageSearchListener(new PackageSearchListenerInterface() {
				@Override
				public void errorOccured(Exception e) {
					
				}
				@Override
				public void aborted(String url) {
					
				}
				@Override
				public void packagesFound(List<MinimalPackage> packages, String query, SearchResult result) {
					if (packages != null) {
						final List<PackageFilterItem> items = new ArrayList<PackageFilterViewer.PackageFilterItem>();
						for (MinimalPackage pkg : packages) {
							items.add(new PackageFilterItem(pkg));
						}
						
						getControl().getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								contentProvider.add(items);
								viewer.refresh();
							}
						});
					}
				}
			});
			
			if (showProjectsCheckbox.getSelection()) {
				search.setFilter("project");
			}
			search.search(text);
			
		} catch (Exception e) {
			Logger.logException(e);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void checked(PackageFilterItem item) {
		
		List<PackageFilterItem> input = (List<PackageFilterItem>) viewer.getInput();
		
		if (!item.isChecked()) {
			return;
		}
		
		for (PackageFilterItem filterItem : input) {
			
			if (filterItem == item) {
				continue;
			}
			filterItem.setChecked(false);
		}
		
		ScrolledComposite control = (ScrolledComposite) viewer.getControl();
		Point origin = control.getOrigin();
		viewer.refresh();
		control.setOrigin(origin);
		
	}

	@Override
	public void unchecked(PackagistItem item) {
		
	}
	
	protected static class PackagistContentProvider implements ITreeContentProvider {

		private List<PackageFilterItem> packages;
		
		public void dispose() {
			packages = null;
		}

		public Object[] getChildren(Object parentElement) {
			return null;
		}

		public Object[] getElements(Object inputElement) {
			if (packages != null) {
				List<Object> elements = new ArrayList<Object>();
				elements.addAll(packages);
				return elements.toArray(new Object[elements.size()]);
			}
			
			return EMTPY;
		}

		public Object getParent(Object element) {
			if (element instanceof PackageFilterItem) {
				return packages;
			}
			
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof PackageFilterItem) {
				return false;
			}
			
			if (packages != null) {
				return packages.size() > 0;
			}
			
			return false;
		}
		
		public void add(List<PackageFilterItem> items) {
			packages.addAll(items);
		}
		
		public void clear() {
			packages = new ArrayList<PackageFilterViewer.PackageFilterItem>();
		}

		@SuppressWarnings("unchecked")
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			packages = (List<PackageFilterItem>) newInput;
		}
	}

	protected static class PackageFilterItem {

		protected boolean isChecked;
		protected MinimalPackage item;
		protected String[] versions;
		
		public PackageFilterItem(MinimalPackage pkg) {
			isChecked = false;
			item = pkg;
		}
		
		public MinimalPackage getPackage() {
			return item;
		}
		
		public boolean isChecked() {
			return isChecked;
		}
		
		public void setChecked(boolean checked) {
			isChecked = checked;
		}

		public void setVersions(String[] versionInput) {
			versions = versionInput;
		}
		
		public String[] getVersions() {
			return versions;
		}
	}
}
