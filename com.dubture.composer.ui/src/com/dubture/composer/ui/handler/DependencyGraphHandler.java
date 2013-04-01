package com.dubture.composer.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.core.launch.PharNotFoundException;
import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.views.DependencyGraphView;

public class DependencyGraphHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IViewPart view = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.showView(DependencyGraphView.ID);

			if (view instanceof DependencyGraphView) {

				// Somehow get the project 
//				if (composer == null) {
//					Logger.debug("Unable to retrieve composer/project during dependency graph handling");
//					return null;
//				}
//
//				DependencyGraph graph = (DependencyGraph) view;
//				graph.setProject(composer.getProject());
			}

		} catch (PartInitException e) {
			Logger.logException(e);
		}
		return null;
	}

}
