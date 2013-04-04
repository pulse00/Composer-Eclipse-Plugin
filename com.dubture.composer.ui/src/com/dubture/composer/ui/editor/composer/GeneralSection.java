package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.getcomposer.core.ComposerConstants;

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

	
	protected final String[] LICENSES = new String[] { 
			"MIT License",
			"Microsoft Public License (Ms-PL)",
			"GNU General Public License v2 (GPL-2)",
			"GNU General Public License v3 (GPL-3)",
			"Apache License 2.0 (Apache-2.0)",
			"Mozilla Public License 2.0 (MPL-2)",
			"GNU Lesser General Public License v2.1 (LGPL-2.1)",
			"GNU Lesser General Public License v3 (LGPL-3.0)",
			"BSD 3-Clause License (Revised)", "BSD 2-Clause License (FreeBSD)",
			"Zlib-Libpng License (Zlib)",
			"Common Development and Distribution License (CDDL-1.0)",
			"Academic Free License 3.0 (AFL)",
			"Artistic License 2.0 (Artistic)", 
			"PHP License 3.0 (PHP)",
			"Simple Public License 2.0 (SimPL)",
			"Eclipse Public License 1.0 (EPL-1.0)", "IPA Font License (IPA)",
			"IBM Public License 1.0 (IPL)",
			"Apple Public Source License 2.0 (APSL)",
			"University of Illinois - NCSA Open Source License (NCSA)",
			"Sleepycat License (Sleepycat)",
			"Do WTF You Want To Public License v2 (WTFPL-2.0)",
			"Free Art License (FAL)", "Boost Software License 1.0",
			"ISC License", "Beerware License", "Open Font License (OFL)",
			"Microsoft Reciprocal License (Ms-RL)", "Python License 2.0",
			"GNU Affero General Public License v3 (AGPL-3.0)",
			"OpenMRS Public License v1.1 (OMRS-1.1)",
			"GNU Free Documentation License v1.3 (FDL-1.3)",
			"Creative Commons Attribution (CC)",
			"Creative Commons Attribution Share Alike (CC-SA)",
			"Creative Commons Attribution NoDerivs (CC-ND)",
			"Creative Commons Attribution NonCommercial (CC-NC)",
			"Creative Commons Attribution NonCommercial ShareAlike (CC-NC-SA)",
			"Creative Commons Attribution NonCommercial NoDerivs (CC-NC-ND)" };

	public GeneralSection(ComposerFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION);
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
			String2KeywordsConverter converter;
			public void focusGained(FormEntry entry) {
				converter = new String2KeywordsConverter(composerPackage);
			}
			
			public void focusLost(FormEntry entry) {
				converter.convert(entry.getValue());
			}
		});
		composerPackage.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().startsWith("keywords")) {
					keywordsEntry.setValue(converter.convert(composerPackage.getKeywords()), true);
				}
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
		
		ControlDecoration decoration = new ControlDecoration(licenseEntry.getText(), SWT.TOP | SWT.LEFT);
		
        FieldDecoration indicator = FieldDecorationRegistry.getDefault().
                getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);

        decoration.setImage(indicator.getImage());
        decoration.setDescriptionText(indicator.getDescription() + "(Ctrl+Space)");
        decoration.setShowOnlyOnFocus(true);
		
		new AutoCompleteField(licenseEntry.getText(), new LicenseContentAdapter(), ComposerConstants.LICENSES);
		
		final License2StringConverter converter = new License2StringConverter();
		licenseEntry.setValue(converter.convert(composerPackage.getLicense()), true);
		
		licenseEntry.addFormEntryListener(new FormEntryAdapter() {
			String2LicenseConverter converter;
			public void focusGained(FormEntry entry) {
				converter = new String2LicenseConverter(composerPackage);
			}
			
			public void focusLost(FormEntry entry) {
				converter.convert(entry.getValue());			
			}
		});
		composerPackage.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().startsWith("license")) {
					licenseEntry.setValue(converter.convert(composerPackage.getLicense()), true);
				}
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
	
	private class LicenseContentAdapter extends TextContentAdapter {
		@Override
		public String getControlContents(Control control) {
			String text = ((Text)control).getText();
			String[] chunks = text.split(",");
			return chunks[chunks.length - 1].trim();
		}
		
		@Override
		public void setControlContents(Control control, String text,
				int cursorPosition) {

			String id = text.replaceAll(".+\\((.+)\\)$", "$1");

			String val = ((Text)control).getText();
			String[] chunks = val.split(",");
			chunks[chunks.length - 1] = id;
			val = StringUtils.join(chunks, ", ");
			cursorPosition = val.length();
			
			((Text) control).setText(val);
			((Text) control).setSelection(cursorPosition, cursorPosition);
		}
	}
}
