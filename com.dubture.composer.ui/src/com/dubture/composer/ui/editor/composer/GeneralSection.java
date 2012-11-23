package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.getcomposer.ComposerConstants;
import org.getcomposer.ComposerPackage;

import com.dubture.composer.ui.converter.Keywords2StringConverter;
import com.dubture.composer.ui.converter.License2StringConverter;
import com.dubture.composer.ui.converter.String2KeywordsConverter;
import com.dubture.composer.ui.converter.String2LicenseConverter;
import com.dubture.composer.ui.editor.ComboFormEntryAdapter;
import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.ComposerSection;
import com.dubture.composer.ui.editor.FormEntryAdapter;
import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.parts.ComboFormEntry;
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
		createStabilityEntry(client, toolkit);
	}

	private void createNameEntry(Composite client, FormToolkit toolkit) {
		final FormEntry nameEntry = new FormEntry(client, toolkit, "Name", null, false);
		nameEntry.setValue(composerPackage.getName(), true);
		
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
		descriptionEntry.setValue(composerPackage.getDescription(), true);
		
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
		typeEntry.setValue(composerPackage.getType(), true);
		
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
		
		final Keywords2StringConverter converter = new Keywords2StringConverter();
		
		keywordsEntry.setValue(converter.convert(composerPackage.getKeywords()), true);
		
		keywordsEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				String2KeywordsConverter converter = new String2KeywordsConverter();
				composerPackage.set("keywords", converter.convert(entry.getValue()));
			}
		});
		composerPackage.addPropertyChangeListener("keywords", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				keywordsEntry.setValue(converter.convert(composerPackage.getKeywords()), true);
			}
		});
	}
	
	private void createHomepageEntry(Composite client, FormToolkit toolkit) {
		final FormEntry homepageEntry = new WeblinkFormEntry(client, toolkit, "Homepage");
		homepageEntry.setValue(composerPackage.getHomepage());
		
		homepageEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				if (entry.getValue() != "") {
					composerPackage.set("homepage", entry.getValue());
				}
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
		
		final License2StringConverter converter = new License2StringConverter();
		
		licenseEntry.setValue(converter.convert(composerPackage.getLicense()), true);
		
		
		licenseEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				String2LicenseConverter converter = new String2LicenseConverter();
				converter.setComposerPackage(composerPackage);
				converter.convert(entry.getValue());
//				composerPackage.set("license", converter.convert(entry.getValue()));
			}
		});
		composerPackage.addPropertyChangeListener("license", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				licenseEntry.setValue(converter.convert(composerPackage.getLicense()), true);
			}
		});
		
	}
	
	private void createStabilityEntry(Composite client, FormToolkit toolkit) {
		final ComboFormEntry minimumStabilityEntry = new ComboFormEntry(client, toolkit, "Minimum Stability");
		minimumStabilityEntry.getComboPart().setItems(ComposerConstants.STABILITIES);
		minimumStabilityEntry.setValue(composerPackage.getMinimumStability(), true);
		
		minimumStabilityEntry.addComboFormEntryListener(new ComboFormEntryAdapter() {
			public void selectionChanged(ComboFormEntry entry) {
				composerPackage.set("minimum-stability", entry.getValue());
			}
		});
		composerPackage.addPropertyChangeListener("minimum-stability", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				minimumStabilityEntry.setValue(composerPackage.getMinimumStability(), true);
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
