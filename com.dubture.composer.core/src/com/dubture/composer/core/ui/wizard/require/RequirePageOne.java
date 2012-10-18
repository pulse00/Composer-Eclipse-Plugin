package com.dubture.composer.core.ui.wizard.require;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.WorkbenchJob;
import org.getcomposer.core.PackageInterface;
import org.getcomposer.core.packagist.SearchResultDownloader;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.model.EclipsePHPPackage;
import com.dubture.composer.core.model.InstallableItem;
import com.dubture.composer.core.ui.wizard.iteminstaller.MultiItemInstallerPage;

public class RequirePageOne extends MultiItemInstallerPage
{
    protected RequirePageOne()
    {
        super("Search for items on packagist.org");
        setTitle("Add composer dependencies");
        setDescription("Search for composer packages on packagist.org");
        
    }

    @Override
    protected void createRefreshJob()
    {
        refreshJob = new WorkbenchJob("filter") { //$NON-NLS-1$
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
                            List<PackageInterface> searchPackages = downloader.searchPackages(text);
                            
                            for (PackageInterface p : searchPackages) {
                                EclipsePHPPackage eclipsePackage = new EclipsePHPPackage(p);
                                items.add(eclipsePackage);
                            }
                        }
                    } catch (IOException e) {
                        Logger.logException(e);
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
    }

    public List<InstallableItem> getSelectedItems()
    {
        return selectedItems;
    }

    @Override
    public boolean doFinish()
    {
        return true;
    }
}
