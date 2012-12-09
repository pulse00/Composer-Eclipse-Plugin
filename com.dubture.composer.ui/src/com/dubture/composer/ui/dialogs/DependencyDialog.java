package com.dubture.composer.ui.dialogs;

import java.io.IOException;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.getcomposer.collection.Versions;
import org.getcomposer.entities.Dependency;
import org.getcomposer.packagist.PackagistDownloader;

public class DependencyDialog extends Dialog {

	private Dependency dependency;
	private Text name;
	private Text version;
	private List list;
	private Versions versions;
	
	/**
	 * @wbp.parser.constructor
	 * @param parentShell
	 * @param dependency
	 */
	public DependencyDialog(Shell parentShell, Dependency dependency) {
		super(parentShell);
		this.dependency = dependency;
		initialize();
	}

	public DependencyDialog(IShellProvider parentShell, Dependency dependency) {
		super(parentShell);
		this.dependency = dependency;
		initialize();
	}
	
	private void initialize() {
		String name = dependency.getName();
		if (name != null && name.trim() != "" && !name.trim().equals("") && !name.trim().equals("php")) {
			PackagistDownloader downloader = new PackagistDownloader(name);
			try {
				versions = downloader.getPackage().getVersions();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Dependency getDependency() {
		return dependency;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite contents = new Composite(parent, SWT.BORDER | SWT.NO_BACKGROUND | SWT.NO_FOCUS | SWT.NO_MERGE_PAINTS | SWT.NO_REDRAW_RESIZE | SWT.NO_RADIO_GROUP | SWT.EMBEDDED);
		contents.setLayout(new GridLayout(2, false));
		
		Label lblName = new Label(contents, SWT.NONE);
		GridData gd_lblName = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblName.widthHint = 100;
		lblName.setLayoutData(gd_lblName);
		lblName.setText("Name");
		
		name = new Text(contents, SWT.BORDER);
		GridData gd_name = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_name.widthHint = 150;
		name.setLayoutData(gd_name);
		
		Label lblVersion = new Label(contents, SWT.NONE);
		lblVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblVersion.setText("Version");
		
		version = new Text(contents, SWT.BORDER);
		version.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblAvailableVersions = new Label(contents, SWT.NONE);
		lblAvailableVersions.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblAvailableVersions.setText("Available Versions");
		
		Label lblSelectingOneWill = new Label(contents, SWT.NONE);
		lblSelectingOneWill.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblSelectingOneWill.setText("Selecting one will set your version to the selection");
		
		list = new List(contents, SWT.BORDER | SWT.V_SCROLL);
		list.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
		list.setItems(versions.toArray());
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (list.getSelectionCount() > 0) {
					version.setText(list.getSelection()[0]);
				}
			}
		});
		
		return contents;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(name);
		IObservableValue nameDependencyObserveValue = BeanProperties.value("name").observe(dependency);
		bindingContext.bindValue(observeTextNameObserveWidget, nameDependencyObserveValue, null, null);
		//
		IObservableValue observeTextVersionObserveWidget = WidgetProperties.text(SWT.Modify).observe(version);
		IObservableValue versionDependencyObserveValue = BeanProperties.value("version").observe(dependency);
		bindingContext.bindValue(observeTextVersionObserveWidget, versionDependencyObserveValue, null, null);
		//
		return bindingContext;
	}
}
