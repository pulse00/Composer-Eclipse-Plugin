package com.dubture.composer.ui.editor.composer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.getcomposer.core.collection.Psr0;
import org.getcomposer.core.objects.Autoload;
import org.getcomposer.core.objects.Namespace;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.ComposerSection;
import com.dubture.composer.ui.editor.FormEntryAdapter;
import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.parts.FormEntry;

public class Psr0Section extends ComposerSection {

	private FormEntry namespaceEntry;
	private FormEntry folderEntry;

	public Psr0Section(ComposerFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION);
		createClient(getSection(), page.getManagedForm().getToolkit());
	}

	@Override
	protected void createClient(Section section, FormToolkit toolkit) {

		section.setText("psr-0");
		section.setDescription("Configure autoloading of your package.");
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createSectionClientTableWrapLayout(false, 2));
		section.setClient(client);

		createPsr0Entry(client, toolkit);

		FormText composer = toolkit.createFormText(client, false);
		composer.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composer.setText(
				"<form>\n<p>Learn more about psr-0.</p>\n\n<li style=\"image\" value=\"url\"><a href=\"http://getcomposer.org/doc/04-schema.md#psr-0\">Composer</a>: psr-0 documentation</li>\n<li style=\"image\" value=\"url\"><a href=\"https://github.com/php-fig/fig-standards/blob/master/accepted/PSR-0.md\">Getting Started</a> with psr-0</li>\n</form>",
				true, false);
		composer.setImage("url", ComposerUIPluginImages.BROWSER.createImage());
		composer.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				Program.launch(e.getHref().toString());
			}
		});

	}

	private void createPsr0Entry(Composite client, FormToolkit toolkit) {

		namespaceEntry = new FormEntry(client, toolkit, "namespace", null, false);
		folderEntry = new FormEntry(client, toolkit, "folder", null, false);

		setFormValues();

		namespaceEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				updateModel();
			}
		});

		folderEntry.addFormEntryListener(new FormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				updateModel();
			}
		});

		composerPackage.getAutoload().addPropertyChangeListener("psr-0", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				setFormValues();
			}
		});
	}

	protected void updateModel() {
		if (namespaceEntry.getValue().isEmpty() && folderEntry.getValue().isEmpty()) {
			composerPackage.remove("autoload");
		} else {
			composerPackage.remove("autoload");
			Autoload autoload = new Autoload();
			Psr0 prs = new Psr0();
			Namespace ns = new Namespace();
			ns.setNamespace(namespaceEntry.getValue());
			ns.add(folderEntry.getValue());
			prs.add(ns);
			composerPackage.set("autoload", autoload);
			composerPackage.getAutoload().setPsr0(prs);
		}
	}

	protected void setFormValues() {
		Autoload autoload = composerPackage.getAutoload();
		if (autoload != null && autoload.getPsr0() != null) {
			namespaceEntry.setValue("" + autoload.getNamespace().getNamespace(), true);
			folderEntry.setValue("" + autoload.getPsr0Path(), true);
		}
	}
}
