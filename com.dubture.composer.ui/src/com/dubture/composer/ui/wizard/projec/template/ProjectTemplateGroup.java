package com.dubture.composer.ui.wizard.projec.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.dltk.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.wizard.project.BasicSettingsGroup;
import com.dubture.getcomposer.core.ComposerPackage;
import com.dubture.getcomposer.core.MinimalPackage;
import com.dubture.getcomposer.core.RepositoryPackage;
import com.dubture.getcomposer.core.collection.Versions;
import com.dubture.getcomposer.packages.AsyncPackagistDownloader;
import com.dubture.getcomposer.packages.AsyncPackagistSearch;
import com.dubture.getcomposer.packages.PackageListenerInterface;
import com.dubture.getcomposer.packages.PackageSearchListenerInterface;
import com.dubture.getcomposer.packages.SearchResult;

@SuppressWarnings("restriction")
public class ProjectTemplateGroup extends BasicSettingsGroup  {

	protected StringDialogField projectName;
	protected ComboDialogField versionSelector;
	protected Shell shell;
	protected AutoCompleteField autoCompleteField;
	protected List<MinimalPackage> currentResults;
	private AsyncPackagistSearch downloader;
	
	public ProjectTemplateGroup(Composite composite, Shell shell) {
		super(composite, shell);
	}
	
	@Override
	public void createControl(Composite composite, Shell shell) {
		
		this.shell = shell;
		final int numColumns = 3;
		
		final Group group = new Group(composite, SWT.None);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(numColumns, false));
		group.setText("Search packages");
		
		projectName = new StringDialogField();
		projectName.setDialogFieldListener(this);
		projectName.setLabelText("Package name");
		projectName.doFillIntoGrid(group, numColumns);
		LayoutUtil.setHorizontalGrabbing(projectName.getTextControl(null));
		versionSelector = new ComboDialogField(SWT.READ_ONLY);
		versionSelector.setLabelText("Package version");
		versionSelector.doFillIntoGrid(group, numColumns);
		
		autoCompleteField = new AutoCompleteField(projectName.getTextControl(), new TextContentAdapter(), new String[]{});
		
		projectName.getTextControl().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (currentResults == null) {
					return;
				}
				updateVersionSelector();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
			}
		});
		
		downloader = new AsyncPackagistSearch();
		downloader.addPackageSearchListener(new PackageSearchListenerInterface() {
			
			@Override
			public void errorOccured(Exception e) {
				
			}
			
			@Override
			public void aborted(String url) {
				
			}
			
			@Override
			public void packagesFound(List<MinimalPackage> packages, String query, SearchResult result) {
				System.err.println("PACKAGES FOUND " + result.total);
				List<String> searchResult = new ArrayList<String>();
				for (MinimalPackage pkg : packages) {
					searchResult.add(pkg.getName());
				}
				currentResults = packages;
				autoCompleteField.setProposals(searchResult.toArray(new String[searchResult.size()]));
			}
		});
	}
	
	public void updateVersionSelector() {
		try {
			AsyncPackagistDownloader dl = new AsyncPackagistDownloader();
			dl.addPackageListener(new PackageListenerInterface() {
				
				@Override
				public void errorOccured(Exception e) {
					
				}
				
				@Override
				public void aborted(String url) {
					
				}
				
				@Override
				public void packageLoaded(RepositoryPackage repositoryPackage) {
					Versions versions = repositoryPackage.getVersions();
					final List<String> versionNames = new ArrayList<String>();
					for (Entry<String, ComposerPackage> version : versions) {
						versionNames.add(version.getValue().getVersion());
					}
					
					Display.getDefault().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							versionSelector.setItems(versionNames.toArray(new String[versionNames.size()]));
							versionSelector.refresh();
							versionSelector.selectItem(0);
						}
					});
				}
			});
			
			dl.loadPackage(projectName.getText());
			
		} catch (Exception e) {
			Logger.logException(e);
		}
	}
	
	
	@Override
	public void dialogFieldChanged(DialogField field) {
		if(field == projectName && projectName.getText() != null && projectName.getText().length() > 2) {
			System.err.println("#### download");
			downloader.search(projectName.getText());
		}
	}

	protected void fireEvent() {
		setChanged();
		notifyObservers();
	}
}
