package com.dubture.composer.core.ui.handler;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class RequirePageOne extends WizardPage
{
    private Text text;
    protected RequirePageOne(String pageName)
    {
        super(pageName);
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        setControl(container);
        container.setLayout(new FormLayout());
        
        Composite composite = new Composite(container, SWT.NONE);
        FormData fd_composite = new FormData();
        fd_composite.right = new FormAttachment(0, 580);
        fd_composite.top = new FormAttachment(0, 10);
        fd_composite.left = new FormAttachment(0, 10);
        composite.setLayoutData(fd_composite);
        
        text = new Text(composite, SWT.BORDER);
        text.setBounds(10, 10, 450, 19);
        
        Button btnNewButton = new Button(composite, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                System.err.println("foobar");
            }
        });
        btnNewButton.setBounds(466, 6, 94, 28);
        btnNewButton.setText("New Button");
        
        Composite composite_1 = new Composite(container, SWT.NONE);
        FormData fd_composite_1 = new FormData();
        fd_composite_1.bottom = new FormAttachment(0, 290);
        fd_composite_1.right = new FormAttachment(0, 580);
        fd_composite_1.top = new FormAttachment(0, 80);
        fd_composite_1.left = new FormAttachment(0, 10);
        composite_1.setLayoutData(fd_composite_1);
        composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
    }
}
