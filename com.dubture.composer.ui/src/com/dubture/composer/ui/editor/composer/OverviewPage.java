/**
 * 
 */
package com.dubture.composer.ui.editor.composer;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.getcomposer.ComposerConstants;
import org.getcomposer.ComposerPackage;
import org.getcomposer.entities.Person;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.composer.ui.converter.Keywords2StringConverter;
import com.dubture.composer.ui.converter.String2KeywordsConverter;
import com.dubture.composer.ui.dialogs.AuthorDialog;
import com.dubture.composer.ui.editor.ComposerFormPage;

/**
 * @author Thomas Gossmann
 * 
 */
public class OverviewPage extends ComposerFormPage {
	private DataBindingContext m_bindingContext;
	private DataBindingContext bindingContext;

	public final static String ID = "com.dubture.composer.ui.editor.composer.OverviewPage";

	private ComposerPackage composerPackage;
	protected ComposerFormEditor editor;
	
	private Composite left;
	private Composite right;
	
	private Text name;
	private Text description;
	private Text type;
	private Text keywords;
	private Text homepage;
	private Text license;
	private Combo minimumStability;
	private TableViewer authorView;
	
	private Text email;
	private Text issues;
	private Text forum;
	private Text wiki;
	private Text irc;
	private Text source;
	
	private Button add;
	private Button edit;
	private Button remove;

	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public OverviewPage(ComposerFormEditor editor, String id, String title) {
		super(editor, id, title);
		this.editor = editor;
		composerPackage = editor.getComposerPackge();
		
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
		
		GeneralSection generalSection = new GeneralSection(this, left);
		
//		createGeneralSection(left, toolkit);
//		createAuthorsSection(left, toolkit);
//		createSupportSection(left, toolkit);
		
		right = toolkit.createComposite(form.getBody());
		right.setLayout(new TableWrapLayout());
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		createDependenciesSection(right, toolkit);
		createComposerSection(right, toolkit);
		
		
//		m_bindingContext = initDataBindings();
		
	}
	
	DataBindingContext getBindingContext() {
		return bindingContext;
	}

	private void createGeneralSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.EXPANDED | Section.DESCRIPTION | Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setText("General Information");
		section.setDescription("This section describes general information about this package.");

		Composite client = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		client.setLayout(layout);
		section.setClient(client);

		Label lblName = toolkit.createLabel(client, "Name (vendor/project):");
		lblName.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		name = toolkit.createText(client, composerPackage.getName());
		name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblDescription = toolkit.createLabel(client, "Description:");
		lblDescription.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		description = toolkit.createText(client, composerPackage.getDescription());
		description.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label = toolkit.createLabel(client, "Type:");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		type = toolkit.createText(client, composerPackage.getType());
		type.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblKeywords = toolkit.createLabel(client, "Keywords:");
		lblKeywords.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		keywords = toolkit.createText(client, StringUtils.join(composerPackage.getKeywords().toStringArray(), ","));
		keywords.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblHomepage = toolkit.createLabel(client, "Homepage:");
		lblHomepage.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		homepage = toolkit.createText(client, composerPackage.getHomepage());
		homepage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblLicense = toolkit.createLabel(client, "License:");
		lblLicense.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		license = toolkit.createText(client, "no yet.");
		license.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblMinimumStability = toolkit.createLabel(client, "Minimum Stability:");
		lblMinimumStability.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		minimumStability = new Combo(client, SWT.DROP_DOWN);
		minimumStability.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		minimumStability.setItems(ComposerConstants.STABILITIES);
		
		if (composerPackage.getMinimumStability() != null) {
			minimumStability.select(minimumStability.indexOf(composerPackage.getMinimumStability()));
		}
	}
	
	private void createAuthorsSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.EXPANDED | Section.DESCRIPTION | Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setText("Authors");
		section.setDescription("Honour the glorious authors of this package.");
		
		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout(2, false));
		section.setClient(client);
		
		AuthorController authorController = new AuthorController();
		authorView = new TableViewer(client, SWT.BORDER | SWT.V_SCROLL);
		authorView.setContentProvider(authorController);
		authorView.setLabelProvider(authorController);
		authorView.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		authorView.setInput(composerPackage.getAuthors());
		
		Composite buttons = toolkit.createComposite(client);
		buttons.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		buttons.setLayout(new GridLayout(1, false));
		
		add = toolkit.createButton(buttons, "Add...", SWT.NONE);
		add.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		add.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				AuthorDialog diag = new AuthorDialog(add.getShell(), new Person());
				if (diag.open() == Dialog.OK) {
					composerPackage.getAuthors().add(diag.getAuthor());
					authorView.refresh();
				}
			}
		});
		
		edit = toolkit.createButton(buttons, "Edit...", SWT.PUSH);
		edit.setEnabled(false);
		edit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		edit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				Person author = (Person)((StructuredSelection)authorView.getSelection()).getFirstElement();
				AuthorDialog diag = new AuthorDialog(edit.getShell(), author.clone());
				if (diag.open() == Dialog.OK) {
					author = diag.getAuthor();
					authorView.refresh();
				}
			}
		});
		remove = toolkit.createButton(buttons, "Remove", SWT.PUSH);
		remove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		remove.setEnabled(false);
		remove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				Person author = (Person)((StructuredSelection)authorView.getSelection()).getFirstElement();
				MessageDialog diag = new MessageDialog(
						remove.getShell(), 
						"Remove Author", 
						null, 
						"Do you really wan't to remove " + author.getName() + "?", 
						MessageDialog.WARNING,
						new String[] {"Yes", "No"},
						0);
				
				if (diag.open() == Dialog.OK) {
					composerPackage.getAuthors().remove(author);
					authorView.refresh();
				}
			}
		});
		
		
		authorView.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = ((StructuredSelection)event.getSelection());
				boolean enabled = selection.size() > 0;
				edit.setEnabled(enabled);
				remove.setEnabled(enabled);
			}
		});
	}
	
	private void createSupportSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.EXPANDED | Section.DESCRIPTION | Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setText("Support");
		section.setDescription("Provide your end-users support options.");

		Composite client = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		client.setLayout(layout);
		section.setClient(client);

		Label lblEmail = toolkit.createLabel(client, "Email:");
		lblEmail.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		email = toolkit.createText(client, composerPackage.getSupport().getEmail());
		email.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblIssues = toolkit.createLabel(client, "Issues:");
		lblIssues.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		issues = toolkit.createText(client, composerPackage.getSupport().getIssues());
		issues.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblForum = toolkit.createLabel(client, "Forum:");
		lblForum.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		forum = toolkit.createText(client, composerPackage.getSupport().getForum());
		forum.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblWiki = toolkit.createLabel(client, "Wiki:");
		lblWiki.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		wiki = toolkit.createText(client, composerPackage.getSupport().getWiki());
		wiki.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblIrc = toolkit.createLabel(client, "Irc:");
		lblIrc.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		irc = toolkit.createText(client, composerPackage.getSupport().getIrc());
		irc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSource = toolkit.createLabel(client, "Source:");
		lblSource.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		source = toolkit.createText(client, composerPackage.getSupport().getSource());
		source.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
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

	class AuthorController extends LabelProvider implements ITableLabelProvider, IStructuredContentProvider {

		private List<Person> authors;
		private Image authorImage = ComposerUIPluginImages.AUTHOR.createImage();

		@SuppressWarnings("unchecked")
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			authors = (List<Person>)newInput;
		}

		public Object[] getElements(Object inputElement) {
			return authors.toArray();
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return authorImage;
		}

		public String getColumnText(Object element, int columnIndex) {
			Person author = (Person)element;
			StringBuilder sb = new StringBuilder();
			sb.append(author.getName());
			
			// TODO: would be cool to have this in a decorator with hmm grey? text color
			if (author.getEmail() != null && author.getEmail() != "") {
				sb.append(" <" + author.getEmail() + ">");
			}
			
			if (author.getHomepage() != null && author.getHomepage() != "") {
				sb.append(" - " + author.getHomepage());
			}
			
			return sb.toString();
		}
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(name);
		IObservableValue namePhpPackageObserveValue = PojoProperties.value("name").observe(composerPackage);
		bindingContext.bindValue(observeTextNameObserveWidget, namePhpPackageObserveValue, null, null);
		//
		IObservableValue observeTextDescriptionObserveWidget = WidgetProperties.text(SWT.Modify).observe(description);
		IObservableValue descriptionPhpPackageObserveValue = PojoProperties.value("description").observe(composerPackage);
		bindingContext.bindValue(observeTextDescriptionObserveWidget, descriptionPhpPackageObserveValue, null, null);
		//
		IObservableValue observeTextTypeObserveWidget = WidgetProperties.text(SWT.Modify).observe(type);
		IObservableValue typePhpPackageObserveValue = PojoProperties.value("type").observe(composerPackage);
		bindingContext.bindValue(observeTextTypeObserveWidget, typePhpPackageObserveValue, null, null);
		//
		IObservableValue observeTextKeywordsObserveWidget = WidgetProperties.text(SWT.Modify).observe(keywords);
		IObservableValue keywordsPhpPackageObserveValue = PojoProperties.value("keywords").observe(composerPackage);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new String2KeywordsConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new Keywords2StringConverter());
		bindingContext.bindValue(observeTextKeywordsObserveWidget, keywordsPhpPackageObserveValue, strategy, strategy_1);
		//
		IObservableValue observeTextHomepageObserveWidget = WidgetProperties.text(SWT.Modify).observe(homepage);
		IObservableValue homepagePhpPackageObserveValue = PojoProperties.value("homepage").observe(composerPackage);
		bindingContext.bindValue(observeTextHomepageObserveWidget, homepagePhpPackageObserveValue, null, null);
		//
		IObservableValue observeTextMinimumStabilityObserveWidget = WidgetProperties.text().observe(minimumStability);
		IObservableValue minimumStabilityPhpPackageObserveValue = PojoProperties.value("minimumStability").observe(composerPackage);
		bindingContext.bindValue(observeTextMinimumStabilityObserveWidget, minimumStabilityPhpPackageObserveValue, null, null);
		//
		IObservableValue observeTextEmailObserveWidget = WidgetProperties.text(SWT.Modify).observe(email);
		IObservableValue emailPhpPackagegetSupportObserveValue = PojoProperties.value("email").observe(composerPackage.getSupport());
		bindingContext.bindValue(observeTextEmailObserveWidget, emailPhpPackagegetSupportObserveValue, null, null);
		//
		IObservableValue observeTextIssuesObserveWidget = WidgetProperties.text(SWT.Modify).observe(issues);
		IObservableValue issuesPhpPackagegetSupportObserveValue = PojoProperties.value("issues").observe(composerPackage.getSupport());
		bindingContext.bindValue(observeTextIssuesObserveWidget, issuesPhpPackagegetSupportObserveValue, null, null);
		//
		IObservableValue observeTextForumObserveWidget = WidgetProperties.text(SWT.Modify).observe(forum);
		IObservableValue forumPhpPackagegetSupportObserveValue = PojoProperties.value("forum").observe(composerPackage.getSupport());
		bindingContext.bindValue(observeTextForumObserveWidget, forumPhpPackagegetSupportObserveValue, null, null);
		//
		IObservableValue observeTextWikiObserveWidget = WidgetProperties.text(SWT.Modify).observe(wiki);
		IObservableValue wikiPhpPackagegetSupportObserveValue = PojoProperties.value("wiki").observe(composerPackage.getSupport());
		bindingContext.bindValue(observeTextWikiObserveWidget, wikiPhpPackagegetSupportObserveValue, null, null);
		//
		IObservableValue observeTextIrcObserveWidget = WidgetProperties.text(SWT.Modify).observe(irc);
		IObservableValue ircPhpPackagegetSupportObserveValue = PojoProperties.value("irc").observe(composerPackage.getSupport());
		bindingContext.bindValue(observeTextIrcObserveWidget, ircPhpPackagegetSupportObserveValue, null, null);
		//
		IObservableValue observeTextSourceObserveWidget = WidgetProperties.text(SWT.Modify).observe(source);
		IObservableValue sourcePhpPackagegetSupportObserveValue = PojoProperties.value("source").observe(composerPackage.getSupport());
		bindingContext.bindValue(observeTextSourceObserveWidget, sourcePhpPackagegetSupportObserveValue, null, null);
		//
		return bindingContext;
	}
}
