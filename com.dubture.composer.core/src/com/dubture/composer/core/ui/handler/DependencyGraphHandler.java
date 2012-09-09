package com.dubture.composer.core.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.core.ui.PharNotFoundException;
import com.dubture.composer.core.ui.view.DependencyGraph;

public class DependencyGraphHandler extends ComposerHandler implements IHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        try {
            try {
                init(event);
            } catch (PharNotFoundException e) {
                installPharDialog(event);
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
