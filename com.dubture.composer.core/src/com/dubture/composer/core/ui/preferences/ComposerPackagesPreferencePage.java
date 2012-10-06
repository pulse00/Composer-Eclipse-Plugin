package com.dubture.composer.core.ui.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.model.ModelAccess;

public class ComposerPackagesPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage
{
    public ComposerPackagesPreferencePage()
    {
        setPreferenceStore(ComposerPlugin.getDefault().getPreferenceStore());
        String[] packageNames = ModelAccess.getInstance().getPackageManager().getPackageNames();

    }

    @Override
    public void init(IWorkbench workbench)
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected Control createContents(Composite parent)
    {
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
//        LayoutUtil.doDefaultLayout(composite,
//                new DialogField[] { fLibraryList }, true);
//        LayoutUtil.setHorizontalGrabbing(fLibraryList.getTreeControl(null));
        Dialog.applyDialogFont(composite);
        return composite;        
    }

}
