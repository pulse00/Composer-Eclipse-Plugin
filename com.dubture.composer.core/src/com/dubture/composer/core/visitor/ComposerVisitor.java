/*******************************************************************************
 * This file is part of the Composer eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.composer.core.visitor;

import com.dubture.composer.core.model.Composer;
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
public class ComposerVisitor extends AbstractIndexingVisitor implements JsonIndexingVisitor
{
    public static final String REFERENCE_ID = "com.dubture.composer.package";
    
    protected Gson gson;
    
    public ComposerVisitor()
    {
        gson = new Gson();
    }
    
    @Override
    public void visit(Object object)
    {
        Composer composer = (Composer) object;
        
        if (composer != null) {
            composer.setFullPath(getResource().getFullPath().removeLastSegments(1).toString());
            String data = gson.toJson(composer);
            ReferenceInfo info = new ReferenceInfo(REFERENCE_ID, composer.getName(), data);
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
        return Composer.class;
    }
}
