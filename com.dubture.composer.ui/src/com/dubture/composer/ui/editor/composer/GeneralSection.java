package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.getcomposer.ComposerPackage;
import org.getcomposer.collection.GenericArray;

import com.dubture.composer.ui.converter.Keywords2StringConverter;
import com.dubture.composer.ui.converter.License2StringConverter;
import com.dubture.composer.ui.converter.String2KeywordsConverter;
import com.dubture.composer.ui.converter.String2LicenseConverter;
import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.ComposerSection;
import com.dubture.composer.ui.editor.FormEntryAdapter;
import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.parts.FormEntry;
import com.dubture.composer.ui.parts.WeblinkFormEntry;

public class GeneralSection extends ComposerSection {

	ComposerPackage composerPackage;
	DataBindingContext bindingContext;

	public GeneralSection(ComposerFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION);
		composerPackage = page.getComposerEditor().getComposerPackge();
		bindingContext = new DataBindingContext();
		createClient(getSection(), page.getManagedForm().getToolkit());
	}

	@Override
	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText("General Information");
		section.setDescription("This section describes general information about this package.");
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createSectionClientTableWrapLayout(false, 2));
		section.setClient(client);
		
		createNameEntry(client, toolkit);
		createDescriptionEntry(client, toolkit);
		createTypeEntry(client, toolkit);
		createKeywordsEntry(client, toolkit);
		createHomepageEntry(client, toolkit);
		createLicenseEntry(client, toolkit);
	}

	private void createNameEntry(Composite client, FormToolkit toolkit) {
		final FormEntry nameEntry = new FormEntry(client, toolkit, "Name", null, false);
		nameEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				composerPackage.set("name", entry.getValue());
			}
		});
		composerPackage.addPropertyChangeListener("name", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				nameEntry.setValue(composerPackage.getName(), true);
			}
		});
	}
	
	private void createDescriptionEntry(Composite client, FormToolkit toolkit) {
		final FormEntry descriptionEntry = new FormEntry(client, toolkit, "Description", null, false);
		descriptionEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				composerPackage.set("description", entry.getValue());
			}
		});
		composerPackage.addPropertyChangeListener("description", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				descriptionEntry.setValue(composerPackage.getDescription(), true);
			}
		});
	}
	
	private void createTypeEntry(Composite client, FormToolkit toolkit) {
		final FormEntry typeEntry = new FormEntry(client, toolkit, "Type", null, false);
		typeEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				composerPackage.set("type", entry.getValue());
			}
		});
		composerPackage.addPropertyChangeListener("type", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				typeEntry.setValue(composerPackage.getType(), true);
			}
		});
	}
	
	private void createKeywordsEntry(Composite client, FormToolkit toolkit) {
		final FormEntry keywordsEntry = new FormEntry(client, toolkit, "Keywords", null, false);
		keywordsEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				String2KeywordsConverter converter = new String2KeywordsConverter();
				composerPackage.set("keywords", (GenericArray)converter.convert(entry.getValue()));
			}
		});
		composerPackage.addPropertyChangeListener("keywords", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				Keywords2StringConverter converter = new Keywords2StringConverter();
				keywordsEntry.setValue((String)converter.convert(composerPackage.getKeywords()), true);
			}
		});
	}
	
	private void createHomepageEntry(Composite client, FormToolkit toolkit) {
		final FormEntry homepageEntry = new WeblinkFormEntry(client, toolkit, "Homepage");
		homepageEntry.setValue(composerPackage.getHomepage());
		
		homepageEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				composerPackage.set("homepage", entry.getValue());
			}
		});
		composerPackage.addPropertyChangeListener("homepage", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				homepageEntry.setValue(composerPackage.getHomepage(), true);
			}
		});
	}
	
	private void createLicenseEntry(Composite client, FormToolkit toolkit) {
		final FormEntry licenseEntry = new FormEntry(client, toolkit, "License", null, false);
		licenseEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				String2LicenseConverter converter = new String2LicenseConverter();
				composerPackage.set("license", (GenericArray)converter.convert(entry.getValue()));
			}
		});
		composerPackage.addPropertyChangeListener("license", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				License2StringConverter converter = new License2StringConverter();
				licenseEntry.setValue((String)converter.convert(composerPackage.getLicense()), true);
			}
		});
		
	}
	
//	private void createBinding(String property, Control control) {
//		createBinding(property, control, null, null);
//	}
//	
//	private void createBinding(String property, Control control, UpdateValueStrategy control2model, UpdateValueStrategy model2control) {
//		IObservableValue observedControl = WidgetProperties.text(SWT.Modify).observe((Text)control);
//		IObservableValue observedValue = PojoProperties.value(property).observe(composerPackage);
//		bindingContext.bindValue(observedControl, observedValue, control2model, model2control);
//	}

}
