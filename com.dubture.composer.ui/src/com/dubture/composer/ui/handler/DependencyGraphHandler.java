package com.dubture.composer.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.core.launch.PharNotFoundException;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.view.dependencies.DependencyGraph;

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
                
                if (composer == null) {
                    Logger.debug("Unable to retrieve composer/project during dependency graph handling");
                    return null;
                }
                
                DependencyGraph graph = (DependencyGraph) view;
                graph.setProject(composer.getProject());
            }
            
        } catch (PartInitException e) {
            Logger.logException(e);
        }
        return null;
    }

}
