package com.dubture.composer.core.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.php.internal.core.compiler.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.compiler.ast.nodes.ArrayElement;
import org.eclipse.php.internal.core.compiler.ast.nodes.InfixExpression;
import org.eclipse.php.internal.core.compiler.ast.nodes.Scalar;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;

import com.dubture.composer.core.model.ModelAccess;
import com.dubture.composer.core.model.NamespaceMapping;

@SuppressWarnings("restriction")
public class AutoloadVisitor extends PHPASTVisitor
{
    protected IBuildContext context;
    
    public AutoloadVisitor(IBuildContext context)
    {
        this.context = context;
    }


    @Override
    public boolean visit(ArrayCreation s) throws Exception
    {
        NamespaceVisitor visitor = new NamespaceVisitor();
        s.traverse(visitor);
        
        ModelAccess.getInstance().updateNamespaces(visitor.getNamespaces(), context.getSourceModule().getScriptProject());
        
        return true;
    }

    protected class NamespaceVisitor extends PHPASTVisitor {
        
        protected List<NamespaceMapping> namespaces = new ArrayList<NamespaceMapping>();
        
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
            
            namespaces.add(new NamespaceMapping(namespace.getValue().replace("'", "").replace("\\\\", "\\"), resourcePath));
            return true;
        }
        
        public List<NamespaceMapping> getNamespaces()
        {
            return namespaces;
        }
    }
}
