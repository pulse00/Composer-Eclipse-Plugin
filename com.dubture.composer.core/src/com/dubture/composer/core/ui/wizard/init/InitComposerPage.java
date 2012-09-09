package com.dubture.composer.core.ui.wizard.init;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ui.wizards.NewElementWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.getcomposer.core.Author;
import org.getcomposer.core.PHPPackage;

public class InitComposerPage extends NewElementWizardPage
{
    
    private PHPPackage phpPackage;
    
    private List<Text> inputfields = new ArrayList<Text>();

    public InitComposerPage()
    {
        super("Initialize composer for this project");
        setPhpPackage(new PHPPackage());
        setTitle("Initialize this project");
        setDescription("Runs composer.phar init on this project to create an initial composer.json file");
        setPageComplete(false);
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
            } else if ("stability".equals(widget.getData())) {
                getPhpPackage().minimumStability = widget.getText();
            }

            check();
        }
        
        @Override
        public void keyPressed(KeyEvent e) { }
    };
    
    private void check()
    {
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
        
        if (author.name == null || author.name.length() == 0 || author.email == null || author.email.length() == 0) {
            setErrorMessage("Author information invalid");
            setPageComplete(false);
            return;
        }
        
        setErrorMessage(null);
        setPageComplete(true);

    }
    
    private void addInput(Composite parent, String label, String key) 
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
                check();
            }
            
            @Override
            public void focusGained(FocusEvent e)
            {
                
            }
        });
        
        input.setLayoutData(data);
        input.setData(key);
        
        inputfields.add(input);
    }
    

    @Override
    public void createControl(Composite container)
    {
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);
        
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        container.setLayoutData(gd);
        
        addInput(container, "Package name (<vendor>/<name>)", "name");
        addInput(container, "Description", "description");
        addInput(container, "Author", "author");
        addInput(container, "Email", "email");  
        addInput(container, "Homepage", "homepage");
        addInput(container, "Minimum stability", "stability");
        
        setControl(container);
        setPageComplete(false);
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
