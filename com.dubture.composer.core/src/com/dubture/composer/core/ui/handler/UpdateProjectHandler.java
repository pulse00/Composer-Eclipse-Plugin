package com.dubture.composer.core.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.ModelException;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.model.ModelAccess;
import com.dubture.composer.core.util.BuildpathUtil;

public class UpdateProjectHandler extends ComposerHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        try {
            init(event);
        } catch (Exception e) { }

        if (this.scriptProject != null) {
            try {
                ModelAccess.getInstance().getPackageManager()
                        .updateBuildpath(scriptProject.getProject());
                BuildpathUtil.setupVendorBuildpath(this.scriptProject,
                        new NullProgressMonitor());
            } catch (ModelException e) {
                Logger.logException(e);
            }
        }

        return null;
    }
}
