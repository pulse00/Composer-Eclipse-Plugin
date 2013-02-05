package com.dubture.composer.ui.parts.composer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.getcomposer.ComposerPackage;
import org.getcomposer.MinimalPackage;
import org.getcomposer.packagist.PackageSearchListenerInterface;
import org.getcomposer.packagist.PackagistSearch;
import org.getcomposer.packagist.SearchResult;

import com.dubture.composer.ui.controller.IPackageCheckStateChangedListener;
import com.dubture.composer.ui.controller.PackageController;
import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.utils.WidgetFactory;

public class PackageSearch implements PackageSearchListenerInterface, IPackageCheckStateChangedListener {
	
	protected final static long QUERY_DELAY_MS = 300;
	
	protected FormToolkit toolkit;
	protected WidgetFactory factory;
	
	protected Text searchField;
	protected CheckboxTableViewer searchResults;
	protected PackageController searchController;
	protected Composite body;
	protected Composite pickedResults;
	protected Map<String, PackageSearchPart> packageControls = new HashMap<String, PackageSearchPart>();
	protected Button addButton;
	
	protected PackagistSearch downloader = new PackagistSearch();
	protected String currentQuery;
	protected String lastQuery;
	protected String shownQuery;
	protected String foundQuery;
	
	protected Thread resetThread;
	protected Thread queryThread;
	
	protected List<PackageSelectionFinishedListener> packageListeners = new ArrayList<PackageSelectionFinishedListener>();
	
	public PackageSearch (Composite parent, FormToolkit toolkit, String buttonText) {
		create(parent, toolkit, buttonText);
	}
	
	public PackageSearch (Composite parent, FormToolkit toolkit) {
		create(parent, toolkit, null);
	}
	
	public PackageSearch (Composite parent, String buttonText) {
		create(parent, null, buttonText);
	}
	
	public PackageSearch (Composite parent) {
		create(parent, null, null);
	}
	
	public void addPackageCheckStateChangedListener(IPackageCheckStateChangedListener listener) {
		if (searchController != null) {
			searchController.addPackageCheckStateChangedListener(listener);
		}
	}

	public void removePackageCheckStateChangedListener(IPackageCheckStateChangedListener listener) {
		if (searchController != null) {
			searchController.removePackageCheckStateChangedListener(listener);
		}
	}
	
	public void addPackageSelectionFinishedListener(PackageSelectionFinishedListener listener) {
		if (!packageListeners.contains(listener)) {
			packageListeners.add(listener);
		}
	}
	
	public void removePackageSelectionFinishedListener(PackageSelectionFinishedListener listener) {
		packageListeners.remove(listener);
	}
	
	protected void create(Composite parent, FormToolkit toolkit, String buttonText) {
		this.toolkit = toolkit;
		factory = new WidgetFactory(toolkit);
		
		body = factory.createComposite(parent);
		body.setLayout(new GridLayout());
		
		searchField = factory.createText(body, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
		searchField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		searchField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				searchTextChanged();
			}
		});
		searchField.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == SWT.ICON_CANCEL) {
					clearSearchText();
				}
			}
		});
		
		// create search results viewer
		int style = SWT.H_SCROLL | SWT.V_SCROLL;
		if (toolkit == null)
			style |= SWT.BORDER;
		else
			style |= toolkit.getBorderStyle();
		
		
		searchController = getSearchResultsController();
		searchController.addPackageCheckStateChangedListener(this);
		searchResults = CheckboxTableViewer.newCheckList(body, style);
		searchResults.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		searchResults.setCheckStateProvider(searchController);
		searchResults.addCheckStateListener(searchController);
		searchResults.setContentProvider(searchController);
		searchResults.setLabelProvider(searchController);
		searchResults.setInput(new ArrayList<ComposerPackage>());
		
		
		pickedResults = factory.createComposite(body);
		pickedResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout layout = new GridLayout();
		layout.marginTop = 0;
		layout.marginRight = -5;
		layout.marginBottom = 0;
		layout.marginLeft = -5;
		layout.verticalSpacing = FormLayoutFactory.SECTION_CLIENT_MARGIN_TOP;
		layout.horizontalSpacing = 0;
		pickedResults.setLayout(layout);

		if (buttonText != null) {
			addButton = factory.createButton(body);
			addButton.setText(buttonText);
			addButton.setEnabled(false);
			addButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
			addButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					notifyPackageSelectionFinishedListener();
				}
			});
		}
		
		// create downloader
		downloader.addPackageSearchListener(this);
	}
	
	protected void notifyPackageSelectionFinishedListener() {
		List<String> packages = getPackages();
		for (PackageSelectionFinishedListener listener : packageListeners) {
			listener.packagesSelected(packages);
		}
		clear();
	}
	
	protected void clearSearchText() {
		searchResults.setInput(null);
		downloader.abort();

		shownQuery = null;
		queryThread.interrupt();
		resetThread.interrupt();
	}
	
//	protected void setPackages(String[] packages) {
//		searchResults.setInput(packages);
//		System.err.println("Packages: " + packages);
//	}
//	

	@Override
	public void packagesFound(final List<MinimalPackage> packages, String query, SearchResult result) {
		// TODO: why this has to be done in a runnable, obviously yes, results are coming from another thread
		foundQuery = query;
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				boolean change = false;
				
				if (currentQuery.isEmpty()) {
					return;
				}
				
				if (shownQuery == null ||
						(!shownQuery.equals(foundQuery) && currentQuery.equals(foundQuery))) {
					searchResults.setInput(packages);
					change = true;
				}
				
				else if (shownQuery.equals(foundQuery)) {
					searchController.addPackages(packages);
					searchResults.refresh();
					change = true;
				}
				
				if (change) {
					shownQuery = foundQuery;
				}
			}
		});
	}
	
	protected void searchTextChanged() {
		currentQuery = searchField.getText();
		
		if (currentQuery.isEmpty()) {
			clearSearchText();
			return;
		}
		
		// kill previous downloader
		downloader.abort();
		
		// run a new one
		if (queryThread == null || !queryThread.isAlive() || queryThread.isInterrupted()) {
			startQuery();
			queryThread = new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(QUERY_DELAY_MS);
						
						startQuery();
						queryThread.interrupt();
					} catch (InterruptedException e) {
					}
				}
			});
			queryThread.start();
		}
	}
	
	protected void startQuery() {
		if (lastQuery == currentQuery) {
			return;
		}
		downloader.searchPackagesAsync(currentQuery);
		
		if (resetThread != null) {
			resetThread.interrupt();
		}
		resetThread = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1500);

					if (shownQuery.equals(currentQuery)) {
						shownQuery = null;
					}
				} catch (InterruptedException e) {
				}
			}
		});
		resetThread.start();
		lastQuery = currentQuery;
	}

	protected PackageController getSearchResultsController() {
		return new PackageController();
	}
	
	public Composite getBody() {
		return body;
	}

	@Override
	public void packageCheckStateChanged(String name, boolean checked) {
		if (checked) {
			packageControls.put(name, connectPackagePart(createPackagePart(pickedResults, name)));
		} else {
			packageControls.remove(name).dispose();
		}
		if (addButton != null) {
			addButton.setEnabled(searchController.getCheckedPackagesCount() > 0);
		}
		getBody().layout(true, true);
	}
	
	private PackageSearchPart connectPackagePart(final PackageSearchPart psp) {
		psp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				searchController.setChecked(psp.getName(), psp.isChecked());
				searchResults.refresh();
			}
		});
		return psp;
	}
	
	protected PackageSearchPart createPackagePart(Composite parent, final String name) {
		return new PackageSearchPart(parent, toolkit, name);
	}
	
	public List<String> getPackages() {
		return searchController.getCheckedPackages();
	}
	
	public void clear() {
		searchController.clear();
		packageControls.clear();
		for (Control child : pickedResults.getChildren()) {
			child.dispose();
		}
		if (addButton != null) {
			addButton.setEnabled(false);
		}
		searchResults.refresh();
		getBody().layout(true, true);
	}

}
