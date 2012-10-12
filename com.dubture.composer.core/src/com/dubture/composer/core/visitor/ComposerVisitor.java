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
import org.getcomposer.core.ComposerFieldNamingStrategy;
import org.getcomposer.core.PHPPackage;
import org.getcomposer.core.PackageInterface;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.model.EclipsePHPPackage;
import com.dubture.composer.core.model.ModelAccess;
import com.dubture.indexing.core.index.AbstractIndexingVisitor;
import com.dubture.indexing.core.index.JsonIndexingVisitor;
import com.dubture.indexing.core.index.ReferenceInfo;
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

    protected Gson gson;

    public ComposerVisitor()
    {
        gson = new Gson();
    }

    @Override
    public void visit(Object object)
    {
        IFile file = (IFile) getResource();

        if (file == null) {
            return;
        }
        
        if ("installed.json".equals(file.getName())) {
            ModelAccess.getInstance().getPackageManager().updateBuildpath();
            return;
        }
        
        if ("composer.json".equals(file.getName()) == false
                || !(object instanceof PackageInterface)) {
            return;
        }

        PackageInterface pHPPackage = (PackageInterface) object;
        
        if (pHPPackage != null) {
            
            EclipsePHPPackage eclipsePackage = new EclipsePHPPackage(pHPPackage);
            eclipsePackage.setFullPath(getResource().getFullPath().removeLastSegments(1).toString());
            ReferenceInfo info = new ReferenceInfo(REFERENCE_ID, pHPPackage.getName(), gson.toJson(eclipsePackage));
            Logger.debug("Adding composer reference " + pHPPackage.getName());
            requestor.addReference(info);
        }        
    }

    @Override
    public FieldNamingStrategy getFieldNamingStrategy()
    {
        return new ComposerFieldNamingStrategy();
    }

    @Override
    public Class<?> getTransformerClass()
    {
        return PHPPackage.class;
    }

    @Override
    public Gson getBuilder()
    {
        return PHPPackage.getBuilder();
    }

    @Override
    public void resourceDeleted(IFile file)
    {
        requestor.deleteReferences(file, REFERENCE_ID);
    }
}
