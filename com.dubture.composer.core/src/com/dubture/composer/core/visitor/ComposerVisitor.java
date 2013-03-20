/*******************************************************************************
 * This file is part of the PHPPackage eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.composer.core.visitor;

import org.eclipse.core.resources.IFile;
import org.getcomposer.core.ComposerPackage;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.model.ModelAccess;
import com.dubture.indexing.core.index.AbstractIndexingVisitor;
import com.dubture.indexing.core.index.JsonIndexingVisitor;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;

/**
 * 
 * Lucene indexing visitor for composer.json files.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 * 
 */
public class ComposerVisitor extends AbstractIndexingVisitor implements
        JsonIndexingVisitor
{
    public static final String REFERENCE_ID = "com.dubture.composer.lib.package";

    @Override
    public void visit(Object object)
    {
        IFile file = (IFile) getResource();

        if (file == null) {
            Logger.debug("ComposerVisitor being called, but file was null");
            return;
        }
        
        Logger.debug("ComposerVisitor being called on " + file.getName());
        if ("installed.json".equals(file.getName()) || "installed_dev.json".equals(file.getName())) {
            Logger.debug("updating buildpath");
            ModelAccess.getInstance().getPackageManager().updateBuildpath(file.getProject());
            return;
        }
    }

    @Override
    public FieldNamingStrategy getFieldNamingStrategy()
    {
//        return new ComposerFieldNamingStrategy();
    	return null;
    }

    @Override
    public Class<?> getTransformerClass()
    {
        return ComposerPackage.class;
    }

    @Override
    public Gson getBuilder()
    {
        return ComposerPackage.getBuilder();
    }

    @Override
    public void resourceDeleted(IFile file)
    {
        
    }
}
