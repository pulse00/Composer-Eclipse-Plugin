package com.dubture.composer.ui.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.dubture.composer.core.ComposerConstants;
import com.dubture.composer.core.ComposerPlugin;

public class ComposerPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage
{
    private BooleanFieldEditor buildpathEnabled;

    public ComposerPreferencePage()
    {
        setTitle("Composer");
        setDescription("Composer settings");
        
        setPreferenceStore(ComposerPlugin.getDefault().getPreferenceStore());
//        String[] packageNames = ModelAccess.getInstance().getPackageManager().getPackageNames();

    }

    @Override
    public void init(IWorkbench workbench)
    {
        // TODO Auto-generated method stub
    }
    
    @Override
    public IPreferenceStore getPreferenceStore()
    {
        return ComposerPlugin.getDefault().getPreferenceStore();
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        
        buildpathEnabled = new BooleanFieldEditor(ComposerConstants.PREF_BUILDPATH_ENABLE, "Enable buildpath management for composer packages (experimental)", composite);
        buildpathEnabled.setPreferenceStore(getPreferenceStore());
        buildpathEnabled.load();
        
        Dialog.applyDialogFont(composite);
        return composite;        
    }
    
    @Override
    protected void performDefaults()
    {
        buildpathEnabled.loadDefault();
    }
    
    @Override
    public boolean performOk()
    {
        buildpathEnabled.store();
        return true;
    }
}
