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

/**
 *
 */
public class ModelAccess implements NamespaceResolverInterface
{
    private static ModelAccess instance = null;
    
    private static List<Composer> packages;
    
    private ModelAccess()
    {
        
    }

    public static ModelAccess getInstance()
    {
        if (instance == null) {
            instance = new ModelAccess();
        }
        
        return instance;
    }

    /**
     * @param composer
     */
    public ModelAccess add(Composer composer)
    {
        getPackages();
        if (packages.contains(composer)) {
            packages.remove(composer);
        }
        
        System.err.println("Adding Composer :" + composer.getName());
        packages.add(composer);
        
        return this;
    }
    
    public List<Composer> getPackages()
    {
        if (packages == null) {
            packages = new ArrayList<Composer>();
        }
        
        return packages;
    }

    @Override
    public IPath resolve(IResource resource)
    {
        for (Composer composer : getPackages()) {
            IPath ns = composer.resolve(resource);
            if (ns != null) {
                return ns;
            }
        }
        return null;
    }
}
