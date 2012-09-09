/*
 * This file is part of the PHPPackage Eclipse Plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.dubture.composer.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.getcomposer.core.PHPPackage;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.visitor.ComposerVisitor;
import com.dubture.indexing.core.index.ReferenceInfo;
import com.dubture.indexing.core.search.SearchEngine;
import com.google.gson.Gson;

/**
 * 
 * Main ModelAccess to the workspaces composer model.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class ModelAccess implements NamespaceResolverInterface
{
    private static ModelAccess instance = null;
    
    private SearchEngine search;
    
    private Gson gson;
    
    private ModelAccess()
    {
        try {
            search = SearchEngine.getInstance();
            gson = new Gson();
        } catch (Exception e) {
            ComposerPlugin.logException(e);
        }
    }

    public static ModelAccess getInstance()
    {
        if (instance == null) {
            instance = new ModelAccess();
        }
        
        return instance;
    }

    @Override
    public IPath resolve(IResource resource)
    {
        if (search == null) {
            return null;
        }
        
        for (EclipsePHPPackage pHPPackage : getPackages(resource.getProject().getFullPath())) {
            
            IPath ns = pHPPackage.resolve(resource);
            if (ns != null) {
                return ns;
            }
        }
        
        return null;
    }

    public List<EclipsePHPPackage> getPackages(IPath path)
    {
        List<ReferenceInfo> references;
        List<EclipsePHPPackage> packages = new ArrayList<EclipsePHPPackage>();

        try {
            references = search.findReferences(path, ComposerVisitor.REFERENCE_ID);
        } catch (Exception e) {
            ComposerPlugin.logException(e);
            return null;
        }
        
        for (ReferenceInfo info : references) {
            String meta = info.getMetadata();
            PHPPackage json = gson.fromJson(meta, PHPPackage.class);
            EclipsePHPPackage pHPPackage = new EclipsePHPPackage(json);
            packages.add(pHPPackage);
        }
        
        return packages;
    }
}
