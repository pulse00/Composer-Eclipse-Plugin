package com.dubture.composer.ui.editor.composer;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.getcomposer.ComposerPackage;

import com.dubture.composer.ui.editor.ComposerFormPage;
import com.dubture.composer.ui.editor.ComposerSection;
import com.dubture.composer.ui.editor.FormLayoutFactory;

public class AuthorSection extends ComposerSection {

	ComposerPackage composerPackage;
	DataBindingContext bindingContext;

	public AuthorSection(ComposerFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION);
		composerPackage = page.getComposerEditor().getComposerPackge();
		bindingContext = new DataBindingContext();
		createClient(getSection(), page.getManagedForm().getToolkit());
	}

	@Override
	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText("Authors");
		section.setDescription("Honour the glorious authors of this package.");
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createSectionClientTableWrapLayout(false, 2));
		section.setClient(client);
		
		
	}
}
