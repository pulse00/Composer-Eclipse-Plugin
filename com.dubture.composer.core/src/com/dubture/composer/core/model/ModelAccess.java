/*
 * This file is part of the Composer Eclipse Plugin.
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

import com.dubture.composer.core.CorePlugin;
import com.dubture.composer.core.visitor.ComposerVisitor;
import com.dubture.indexing.core.index.ReferenceInfo;
import com.dubture.indexing.core.search.SearchEngine;
import com.google.gson.Gson;

/**
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
            CorePlugin.logException(e);
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
        
        for (Composer composer : getPackages(resource.getProject().getFullPath())) {
            
            IPath ns = composer.resolve(resource);
            if (ns != null) {
                return ns;
            }
        }
        
        return null;
    }

    public List<Composer> getPackages(IPath path)
    {
        List<ReferenceInfo> references;
        List<Composer> packages = new ArrayList<Composer>();

        try {
            references = search.findReferences(path, ComposerVisitor.REFERENCE_ID);
        } catch (Exception e) {
            CorePlugin.logException(e);
            return null;
        }
        
        for (ReferenceInfo info : references) {
            String meta = info.getMetadata();
            Composer composer = gson.fromJson(meta, Composer.class);
            packages.add(composer);
        }
        
        return packages;
    }
}
