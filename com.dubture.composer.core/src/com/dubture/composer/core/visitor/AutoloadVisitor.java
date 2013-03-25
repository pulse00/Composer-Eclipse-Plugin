package com.dubture.composer.core.visitor;

import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.php.internal.core.compiler.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.compiler.ast.nodes.ArrayElement;
import org.eclipse.php.internal.core.compiler.ast.nodes.InfixExpression;
import org.eclipse.php.internal.core.compiler.ast.nodes.Scalar;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.getcomposer.core.collection.Psr0;
import org.getcomposer.core.objects.Namespace;

import com.dubture.composer.core.model.ModelAccess;

@SuppressWarnings("restriction")
public class AutoloadVisitor extends PHPASTVisitor
{
    protected ISourceModule source;
    private NamespaceVisitor visitor;
    
    public AutoloadVisitor(ISourceModule source)
    {
        this.source = source;
    }


    @Override
    public boolean visit(ArrayCreation s) throws Exception
    {
        visitor = new NamespaceVisitor();
        s.traverse(visitor);
        
        ModelAccess.getInstance().updatePsr0(visitor.getPsr0(), source.getScriptProject());
        
        return true;
    }
    
    public Psr0 getPsr0() {
        
        if (visitor != null) {
            return visitor.getPsr0();
        }
        
        return null;
    }

    protected class NamespaceVisitor extends PHPASTVisitor {
        
        protected Psr0 psr0 = new Psr0();
        
        @Override
        public boolean visit(ArrayElement element) throws Exception
        {
            if (!(element.getKey() instanceof Scalar) || !(element.getValue() instanceof InfixExpression)) {
                return false;
            }
            
            Scalar namespace = (Scalar) element.getKey();
            Scalar path = (Scalar) ((InfixExpression) element.getValue()).getRight();
            VariableReference reference = (VariableReference) ((InfixExpression) element.getValue()).getLeft();
            
            String resourcePath = "";
            
            if ("$baseDir".equals(reference.getName())) {
                resourcePath = path.getValue().replace("'", "");
            } else if ("$vendorDir".equals(reference.getName())) {
                resourcePath = "vendor" + path.getValue().replace("'", "");
            }
            
            if (resourcePath.startsWith("/")) {
                resourcePath = resourcePath.replaceFirst("/", "");
            }
            
            String ns = namespace.getValue().replace("'", "").replace("\\\\", "\\");
            psr0.add(new Namespace(ns, resourcePath));
            
            return true;
        }
        
        public Psr0 getPsr0()
        {
            return psr0;
        }
    }
}
