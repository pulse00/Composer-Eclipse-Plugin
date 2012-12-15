package com.dubture.composer.ui.parts.composer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.getcomposer.ComposerPackage;
import org.getcomposer.packagist.PackageSearchListenerInterface;
import org.getcomposer.packagist.PackagistSearch;
import org.getcomposer.packagist.SearchResult;

import com.dubture.composer.ui.controller.ITableController;
import com.dubture.composer.ui.controller.PackageController;

public class PackageSearch implements PackageSearchListenerInterface {
	
	protected final static long QUERY_DELAY_MS = 300;
	
	protected Text searchField;
	protected CheckboxTableViewer searchResults;
	protected PatternFilter searchFilter;
	protected ITableController searchController;
	protected Composite body;
	protected Composite pickedResults;
	protected PackagistSearch downloader = new PackagistSearch();
	protected String currentQuery;
	protected String lastQuery;
	protected String shownQuery;
	protected String foundQuery;
	protected Thread resetThread;
	protected Thread queryThread;
	
	public PackageSearch (Composite parent, FormToolkit toolkit) {
		create(parent, toolkit);
	}
	
	public PackageSearch (Composite parent) {
		create(parent, null);
	}
	
	private void create(Composite parent, FormToolkit toolkit) {
		body = createComposite(parent, toolkit);
		body.setLayout(new GridLayout());
		
		searchField = createText(body, toolkit, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);
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
		
		
		searchResults = CheckboxTableViewer.newCheckList(body, style);
		searchResults.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		searchController = getSearchResultsController();
		searchFilter = new PatternFilter();
		searchFilter.setIncludeLeadingWildcard(true);
		
		searchResults.setContentProvider(searchController);
		searchResults.setLabelProvider(searchController);
		searchResults.addFilter(searchFilter);
		searchResults.setInput(new ArrayList<ComposerPackage>());
		
		pickedResults = createComposite(body, toolkit);
		
		// create downloader
		downloader.addPackageSearchListener(this);
	}
	
	protected void clearSearchText() {
		searchFilter.setPattern(null);
		searchResults.setInput(null);
		downloader.abort();
		// hum, stop all threads
	}
	
//	protected void setPackages(String[] packages) {
//		searchResults.setInput(packages);
//		System.err.println("Packages: " + packages);
//	}
//	

	@Override
	public void packagesFound(final List<ComposerPackage> packages, String query, SearchResult result) {
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
					((PackageController)searchController).addPackages(packages);
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
//		searchFilter.setPattern(searchText);
		
		// kill previous downloader
		downloader.abort();
		
		if (currentQuery.isEmpty()) {
			clearSearchText();
			return;
		}
		
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

	protected ITableController getSearchResultsController() {
		return new PackageController();
	}
	
	public Composite getBody() {
		return body;
	}
	
	
	
	
	
	
	protected Composite createComposite(Composite parent, FormToolkit toolkit) {
		return createComposite(parent, toolkit, SWT.NONE);
	}
	
	protected Composite createComposite(Composite parent, FormToolkit toolkit, int style) {
		if (toolkit == null) {
			return new Composite(parent, style);
		} else {
			return toolkit.createComposite(parent, style);
		}
	}

	protected Text createText(Composite parent, FormToolkit toolkit) {
		return createText(parent, toolkit, SWT.DEFAULT);
	}
	
	protected Text createText(Composite parent, FormToolkit toolkit, int style) {
		if (toolkit == null) {
			return new Text(parent, style);
		} else {
			return toolkit.createText(parent, "", style);
		}
	}

}
