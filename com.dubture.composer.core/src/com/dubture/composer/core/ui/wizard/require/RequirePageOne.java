package com.dubture.composer.core.ui.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.WorkbenchJob;
import org.pex.core.log.Logger;
import org.pex.core.model.InstallableItem;
import org.pex.ui.wizards.iteminstaller.MultiItemInstallerPage;

import com.dubture.composer.core.model.PHPPackage;
import com.dubture.composer.core.packagist.SearchResultDownloader;

public class RequirePageOne extends MultiItemInstallerPage
{
    protected RequirePageOne()
    {
        super("Search for items on packagist.org");
        setDescription("Search for composer packages on packagist.org");
    }

    @Override
    protected void createRefreshJob()
    {
        refreshJob = new WorkbenchJob("filter") { //$NON-NLS-1$
            @SuppressWarnings("unchecked")
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                if (RequirePageOne.this.filterText.isDisposed()) {
                    return Status.CANCEL_STATUS;
                }
                
                String text = RequirePageOne.this.filterText.getText();
                text = text.trim();
                
                if (!RequirePageOne.this.previousFilterText .equals(text)) {
                    
                    SearchResultDownloader downloader = new SearchResultDownloader();
                    items = new ArrayList<InstallableItem>();
                    
                    try {
                        if (text != null && text.length() > 0) {
                            items = (List<InstallableItem>) downloader.searchPackages(text, monitor);
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                    if (items == null) {
                        Logger.debug("Error downloading pakages");
                        items = new ArrayList<InstallableItem>();
                    } 
                    
                    RequirePageOne.this.previousFilterText = text;
                    RequirePageOne.this.filterPattern = createPattern(RequirePageOne.this.previousFilterText);
                    createBodyContents();
                }
                
                return Status.OK_STATUS;
            }
        };
        refreshJob.setSystem(true);
    }

    public boolean doFinish()
    {
        for (InstallableItem item : selectedItems) {
            PHPPackage packageItem = (PHPPackage) item;
            System.err.println(packageItem.getName());
        }
        
        return true;
    }
    
    public List<? extends InstallableItem> getSelectedItems()
    {
        return selectedItems;
    }
}
