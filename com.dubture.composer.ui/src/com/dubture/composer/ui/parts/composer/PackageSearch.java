package com.dubture.composer.ui.parts.composer;

import java.io.IOException;
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
import org.getcomposer.packagist.SearchResultDownloader;

import com.dubture.composer.ui.controller.ITableController;
import com.dubture.composer.ui.controller.PackageController;

public class PackageSearch {
	
	protected Text searchField;
	protected CheckboxTableViewer searchResults;
	protected PatternFilter searchFilter;
	protected Composite body;
	protected Composite pickedResults;
	private Thread worker;

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
		
		ITableController controller = getSearchResultsController();
		searchFilter = new PatternFilter();
		searchFilter.setIncludeLeadingWildcard(true);
		
		searchResults.setContentProvider(controller);
		searchResults.setLabelProvider(controller);
		searchResults.addFilter(searchFilter);
		
		pickedResults = createComposite(body, toolkit);
	}
	
	protected void clearSearchText() {
		searchFilter.setPattern(null);
		searchResults.setInput(null);
		// hum, stop all threads
	}
	
	protected void setPackages(String[] packages) {
		searchResults.setInput(packages);
		System.err.println("Packages: " + packages);
	}
	
	protected void searchTextChanged() {
		final String searchText = searchField.getText();
//		searchFilter.setPattern(searchText);
		
		// kill previous thread
		if (worker != null) {
			worker.interrupt();
		}
		
		// run a new one
		worker = new Thread(new Runnable() {
			public void run() {
				try {
					SearchResultDownloader downloader = new SearchResultDownloader();
					List<ComposerPackage> results = downloader.searchPackages(searchText);
					List<String> packageNames = new ArrayList<String>();
					
					for (ComposerPackage pkg : results) {
						packageNames.add(pkg.getName());
					}
					
					final String[] packages = packageNames.toArray(new String[]{}); 
		
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							setPackages(packages);
						}
					});
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		worker.start();
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
