package com.dubture.composer.core.build;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.builder.IBuildParticipant;

import com.dubture.composer.core.ComposerConstants;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.visitor.AutoloadVisitor;

public class ComposerBuildParticipant implements IBuildParticipant
{
    @Override
    public void build(IBuildContext context) throws CoreException
    {
        if (ComposerConstants.AUTOLOAD_NAMESPACES.equals(context.getSourceModule().getElementName()) == false) {
            return;
        }
        
        try {
            ISourceModule sourceModule = context.getSourceModule();     
            ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(sourceModule);
            moduleDeclaration.traverse(new AutoloadVisitor(context.getSourceModule()));
        } catch (Exception e) {
            Logger.logException(e);
        }        
    }
}
