package com.dubture.composer.eclipse.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.eclipse.ui.view.DependencyGraph;

public class DependencyGraphHandler extends ComposerHandler implements IHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        try {
            init(event);
            
            if (composer == null) {
                return null;
            }
            
            IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(DependencyGraph.VIEW_ID);
            
            if (view instanceof DependencyGraph) {
                DependencyGraph graph = (DependencyGraph) view;
                graph.setProject(composer.getProject());
            }
            
        } catch (PartInitException e) {
            e.printStackTrace();
        }
        return null;
    }

}
