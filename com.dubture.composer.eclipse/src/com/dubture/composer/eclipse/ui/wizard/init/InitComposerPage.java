package com.dubture.composer.eclipse.ui.wizard.init;

import org.eclipse.dltk.ui.wizards.NewElementWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.getcomposer.core.Author;
import org.getcomposer.core.PHPPackage;

public class InitComposerPage extends NewElementWizardPage
{
    
    private PHPPackage phpPackage;
    private Text nameInput;
    private Text descInput;
    private Text authorInput;
    private Text pageInput;
    private Text requireInput;
    private Text requireDevInput;
    private Text stabilityInput;

    public InitComposerPage()
    {
        super("Initialize composer for this project");
        setPhpPackage(new PHPPackage());
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
            if (e.widget == nameInput) {
                getPhpPackage().name = nameInput.getText();
            } else if (e.widget == descInput) {
                getPhpPackage().description = descInput.getText();
            } else if (e.widget == authorInput) {
                
                Author[] authors = getPhpPackage().getAuthors();
                if (authors == null || authors.length == 0) {
                    authors = new Author[]{new Author("")};
                }
                
                Author author = authors[0];
                author.name= authorInput.getText();
                getPhpPackage().authors = new Author[]{author};
            } else if (e.widget == emailInput) {
                
                Author[] authors = getPhpPackage().getAuthors();
                if (authors == null || authors.length == 0) {
                    authors = new Author[]{new Author("")};
                }
                
                Author author = authors[0];
                author.email= emailInput.getText();
                getPhpPackage().authors = new Author[]{author};
                
            } else if (e.widget == pageInput) {
                getPhpPackage().homepage = pageInput.getText();
            } else if (e.widget == requireInput) {
                
            } else if (e.widget == requireDevInput) {
                
            } else if (e.widget == stabilityInput) {
                getPhpPackage().minimumStability = stabilityInput.getText();
            }
        }
        
        @Override
        public void keyPressed(KeyEvent e)
        {
            // TODO Auto-generated method stub
            
            
        }
    };
    private Text emailInput;

    @Override
    public void createControl(Composite parent)
    {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);
        
        Label nameLabel = new Label(container, SWT.NONE);
        nameLabel.setText("Package name (<vendor>/):");
        nameInput = new Text(container, SWT.NORMAL);
        nameInput.addVerifyListener(verifyListener);
        nameInput.addKeyListener(changeListener);

        Label descriptionLabel = new Label(container, SWT.NONE);
        descriptionLabel.setText("Description:");
        descInput = new Text(container, SWT.NORMAL);
        descInput.addVerifyListener(verifyListener);
        descInput.addKeyListener(changeListener);

        
        Label authorLabel = new Label(container, SWT.NONE);
        authorLabel.setText("Author:");
        authorInput = new Text(container, SWT.NORMAL);
        authorInput.addVerifyListener(verifyListener);
        authorInput.addKeyListener(changeListener);
        
        Label emailLabel = new Label(container, SWT.NONE);
        emailLabel.setText("Email:");
        emailInput = new Text(container, SWT.NORMAL);
        emailInput.addVerifyListener(verifyListener);
        emailInput.addKeyListener(changeListener);
        
        
        Label homepageLabel = new Label(container, SWT.NONE);
        homepageLabel.setText("Homepage:");
        pageInput = new Text(container, SWT.NORMAL);
        pageInput.addVerifyListener(verifyListener);
        pageInput.addKeyListener(changeListener);

        /*
        Label requireLabel = new Label(container, SWT.NONE);
        requireLabel.setText("Require:");
        requireInput = new Text(container, SWT.NORMAL);
        requireInput.addVerifyListener(verifyListener);
        requireInput.addKeyListener(changeListener);
        
        Label requireDevLabel = new Label(container, SWT.NONE);
        requireDevLabel.setText("Require-dev:");
        requireDevInput = new Text(container, SWT.NORMAL);
        requireDevInput.addVerifyListener(verifyListener);
        requireDevInput.addKeyListener(changeListener);
        */

        Label stabilityLabel = new Label(container, SWT.NONE);
        stabilityLabel.setText("Minimum stability:");
        stabilityInput = new Text(container, SWT.NORMAL);
        stabilityInput.addVerifyListener(verifyListener);
        stabilityInput.addKeyListener(changeListener);

        setControl(container);
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
