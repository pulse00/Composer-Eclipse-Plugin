package com.dubture.composer.ui.wizard.init;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.internal.preferences.InstancePreferences;
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
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.getcomposer.core.Author;
import org.getcomposer.core.PHPPackage;

/**
 * 
 * @author "Robert Gruendler <r.gruendler@gmail.com>"
 *
 */
public class InitComposerPage extends NewElementWizardPage
{
    
    private PHPPackage phpPackage;
    
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
        setPhpPackage(new PHPPackage());
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
                getPhpPackage().name = widget.getText();
            } else if ("description".equals(widget.getData())) {
                getPhpPackage().description = widget.getText();
            } else if ("author".equals(widget.getData())) {
                
                Author[] authors = getPhpPackage().getAuthors();
                if (authors == null || authors.length == 0) {
                    authors = new Author[]{new Author("")};
                }
                
                Author author = authors[0];
                author.name= widget.getText();
                getPhpPackage().authors = new Author[]{author};
            } else if ("email".equals(widget.getData())) {
                
                Author[] authors = getPhpPackage().getAuthors();
                if (authors == null || authors.length == 0) {
                    authors = new Author[]{new Author("")};
                }
                
                Author author = authors[0];
                author.email= widget.getText();
                getPhpPackage().authors = new Author[]{author};
                
            } else if ("page".equals(widget.getData())) {
                getPhpPackage().homepage = widget.getText();
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
        if (phpPackage.getName() == null || phpPackage.getName().length() == 0) {
            setErrorMessage("Vendor name missing");
            setPageComplete(false);
            return;
        }
        
        Author[] authors = phpPackage.getAuthors();
        
        if (authors == null || authors.length == 0) {
            setErrorMessage("Author information missing");
            setPageComplete(false);
            return;
        }
        
        Author author = authors[0];
        
        if (author.name == null || author.name.length() == 0) {
            setErrorMessage("Author name missing");
            setPageComplete(false);
            return;
        }
        
        if (author.email == null || author.email.length() == 0) {
            setErrorMessage("Author email missing");
            setPageComplete(false);
            return;
        }
        
        matcher = pattern.matcher(author.email);
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
        phpPackage.name = defaultPackageName;
        phpPackage.authors = new Author[]{new Author(defaultAuthor)};
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
                phpPackage.minimumStability = stabilityCombo.getItem(stabilityCombo.getSelectionIndex());
            }
        });
        
        phpPackage.minimumStability = "stable";
        
    }

    public PHPPackage getPhpPackage()
    {
        return phpPackage;
    }

    public void setPhpPackage(PHPPackage phpPackage)
    {
        this.phpPackage = phpPackage;
    }
}
