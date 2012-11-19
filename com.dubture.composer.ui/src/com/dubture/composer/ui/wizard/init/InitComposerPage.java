package com.dubture.composer.ui.wizard.init;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.ui.wizards.NewElementWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.getcomposer.ComposerPackage;
import org.getcomposer.collection.Persons;
import org.getcomposer.entities.Person;

/**
 * 
 * @author "Robert Gruendler <r.gruendler@gmail.com>"
 *
 */
public class InitComposerPage extends NewElementWizardPage
{
    
    private ComposerPackage composerPackage;
    
    private List<Text> inputfields = new ArrayList<Text>();
    
    private String EMAIL_PATTERN = 
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    
    private IScriptProject project;

    public InitComposerPage(IScriptProject project)
    {
        super("Initialize composer for this project");
        setComposerPackage(new ComposerPackage());
        setTitle("Initialize this project");
        setDescription("Runs composer.phar init on this project to create an initial composer.json file");
        setPageComplete(false);
        this.project = project;
    }
    
    protected VerifyListener verifyListener = new VerifyListener()
    {
        
        @Override
        public void verifyText(VerifyEvent e)
        {
//            updateStatus(new Status(Status.ERROR, "invalid init options", 1, "", null));
        }
    };
    
    protected KeyListener changeListener = new KeyListener()
    {
        
        @Override
        public void keyReleased(KeyEvent e)
        {
            if (!(e.widget instanceof Text)) {
                return;
            }
            
            Text widget = (Text) e.widget;
            
            if ("name".equals(widget.getData())) {
                getComposerPackage().setName(widget.getText());
            } else if ("description".equals(widget.getData())) {
                getComposerPackage().setDescription(widget.getText());
            } else if ("author".equals(widget.getData())) {
                
                Persons authors = getComposerPackage().getAuthors();
                if (authors == null || authors.size() == 0) {
                    authors.add(new Person(""));
                }
                
                Person author = authors.get(0);
                author.setName(widget.getText());
//                getComposerPackage().setPersons(authors);
            } else if ("email".equals(widget.getData())) {
                
            	Persons authors = getComposerPackage().getAuthors();
                if (authors == null || authors.size() == 0) {
                    authors.add(new Person(""));
                }
                
                Person author = authors.get(0);
                author.setEmail(widget.getText());
//                getComposerPackage().setPersons(authors);
                
            } else if ("page".equals(widget.getData())) {
                getComposerPackage().setHomepage(widget.getText());
            }
            
            validate();
        }
        
        @Override
        public void keyPressed(KeyEvent e) { }
    };

    private Combo stabilityCombo;
    
    private void validate()
    {
        System.err.println("validate");
        if (composerPackage.getName() == null || composerPackage.getName().length() == 0) {
            setErrorMessage("Vendor name missing");
            setPageComplete(false);
            return;
        }
        
        Persons authors = composerPackage.getAuthors();
        
        if (authors == null || authors.size() == 0) {
            setErrorMessage("Author information missing");
            setPageComplete(false);
            return;
        }
        
        Person author = authors.get(0);
        
        if (author.getName() == null || author.getName().length() == 0) {
            setErrorMessage("Author name missing");
            setPageComplete(false);
            return;
        }
        
        if (author.getEmail() == null || author.getEmail().length() == 0) {
            setErrorMessage("Author email missing");
            setPageComplete(false);
            return;
        }
        
        matcher = pattern.matcher(author.getEmail());
        if (matcher.matches() == false) {
            setErrorMessage("Not a valid email adress");
            setPageComplete(false);
            return;
        }
        
        setErrorMessage(null);
        setPageComplete(true);

    }
    
    private void addInput(Composite parent, String label, String key, String defaultInput) 
    {
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.widthHint = 200;
        
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        
        Label nameLabel = new Label(parent, SWT.NONE);
        nameLabel.setText(label);
        nameLabel.setLayoutData(gd);
        Text input = new Text(parent, SWT.SINGLE | SWT.BORDER);
        input.addVerifyListener(verifyListener);
        input.addKeyListener(changeListener);
        input.addFocusListener(new FocusListener()
        {
            
            @Override
            public void focusLost(FocusEvent e)
            {
                validate();
            }
            
            @Override
            public void focusGained(FocusEvent e)
            {
            }
        });
        
        input.setLayoutData(data);
        input.setData(key);
        
        if (defaultInput != null) {
            input.setText(defaultInput);
        }
        
        inputfields.add(input);
    }
    
    private void addInput(Composite parent, String label, String key) {
        addInput(parent, label, key, null);
    }
    
    

    @Override
    public void createControl(Composite container)
    {
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);
        
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        container.setLayoutData(gd);

        String defaultAuthor = System.getProperty("user.name");
        String defaultPackageName =  defaultAuthor + "/" + project.getProject().getName();
        composerPackage.setName(defaultPackageName);
        composerPackage.getAuthors().add(new Person(defaultAuthor));
        addInput(container, "Package name (<vendor>/<name>)", "name", defaultPackageName);
        addInput(container, "Description", "description");
        addInput(container, "Author", "author", defaultAuthor);
        addInput(container, "Email", "email");  
        addInput(container, "Homepage", "homepage");
        
        addStability(container);
        
        setControl(container);
        setPageComplete(false);
    }

    private void addStability(Composite parent)
    {
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.widthHint = 200;
        
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        
        Label nameLabel = new Label(parent, SWT.NONE);
        nameLabel.setText("Minimum stability");
        nameLabel.setLayoutData(gd);
        
        stabilityCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        stabilityCombo.setLayoutData(data);
        stabilityCombo.setItems(new String[]{"dev", "alpha", "beta", "RC", "stable"});
        stabilityCombo.select(4);
        stabilityCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                composerPackage.setMinimumStability(stabilityCombo.getItem(stabilityCombo.getSelectionIndex()));
            }
        });
        
        composerPackage.setMinimumStability("stable");
        
    }

    public ComposerPackage getComposerPackage()
    {
        return composerPackage;
    }

    public void setComposerPackage(ComposerPackage phpPackage)
    {
        this.composerPackage = phpPackage;
    }
}
