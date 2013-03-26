/**
 * 
 */
package com.dubture.composer.ui.editor.composer;

import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.editor.ComposerFormPage;

/**
 * @author Thomas Gossmann
 * 
 */
public class OverviewPage extends ComposerFormPage {

	public final static String ID = "com.dubture.composer.ui.editor.composer.OverviewPage";

	protected ComposerFormEditor editor;
	
	private Composite left;
	private Composite right;


	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public OverviewPage(ComposerFormEditor editor, String id, String title) {
		super(editor, id, title);
		this.editor = editor;
		
	}
	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		
		if (active) {
			editor.getHeaderForm().getForm().setText("Overview");
		}
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();

		TableWrapLayout layout = new TableWrapLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 2;
		form.getBody().setLayout(layout);
		
		left = toolkit.createComposite(form.getBody());
		left.setLayout(new TableWrapLayout());
		left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		new GeneralSection(this, left);
		new AuthorSection(this, left);
		new SupportSection(this, left);
		
		right = toolkit.createComposite(form.getBody());
		right.setLayout(new TableWrapLayout());
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		createDependenciesSection(right, toolkit);
		createComposerSection(right, toolkit);

		
	}

	private void createDependenciesSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.EXPANDED | Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setText("Dependencies");
		
		Composite client = toolkit.createComposite(section);
		section.setClient(client);
		client.setLayout(new TableWrapLayout());

		FormText dependencies = toolkit.createFormText(client, false);
		dependencies.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		dependencies.setText("<form>\n<p>This packages dependencies are made up in two pages:</p>\n\n<li style=\"image\" value=\"page\"><a href=\"com.dubture.composer.ui.editor.composer.DependencyPage\">Dependencies</a>: declares the dependencies this package may have.</li>\n<li style=\"image\" value=\"page\"><a href=\"com.dubture.composer.ui.editor.composer.DependencyGraphPage\">Dependency Graph</a>: shows the dependencies in a nice graph.</li>\n</form>", true, false);
		dependencies.setImage("page", ComposerUIPluginImages.PAGE.createImage());
		dependencies.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				editor.setActivePage(e.getHref().toString());
			}
		});
	}
	
	private void createComposerSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.EXPANDED | Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setText("Composer Information");
		
		Composite client = toolkit.createComposite(section);
		section.setClient(client);
		client.setLayout(new TableWrapLayout());

		FormText composer = toolkit.createFormText(client, false);
		composer.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composer.setText("<form>\n<p>Composer is a dependency manager for php.</p>\n\n<li style=\"image\" value=\"url\"><a href=\"http://getcomposer.org\">Composer</a>: Composer Homepage</li>\n<li style=\"image\" value=\"url\"><a href=\"http://getcomposer.org/doc/00-intro.md\">Getting Started</a> with Composer</li>\n<li style=\"image\" value=\"url\"><a href=\"http://getcomposer.org/doc/\">Documentation</a></li>\n<li style=\"image\" value=\"url\"><a href=\"http://getcomposer.org/doc/04-schema.md\">Schema Reference</a></li>\n<li style=\"image\" value=\"url\"><a href=\"http://github.com/composer/composer/issues\">Issues</a>: Report Issues</li>\n<li style=\"image\" value=\"url\"><a href=\"http://packagist.org\">Packagist</a>: Browse Packages</li>\n</form>", true, false);
		composer.setImage("url", ComposerUIPluginImages.BROWSER.createImage());
		composer.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				Program.launch(e.getHref().toString());
			}
		});
	}
}
