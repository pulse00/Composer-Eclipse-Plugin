package com.dubture.composer.core.builder;

import com.dubture.composer.core.model.Composer;
import com.dubture.indexing.core.index.AbstractIndexingVisitor;
import com.dubture.indexing.core.index.JsonIndexingVisitor;
import com.dubture.indexing.core.index.ReferenceInfo;
import com.google.gson.FieldNamingStrategy;

public class ComposerVisitor extends AbstractIndexingVisitor implements JsonIndexingVisitor
{
    @Override
    public void visit(Object object)
    {
        Composer composer = (Composer) object;
        
        if (composer != null) {
            ReferenceInfo info = new ReferenceInfo();
            info.setName(composer.getName());
            requestor.addReference(info);
            System.err.println("indexing package: " + composer.getName());
        } else {
            System.err.println("not indexing");
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
