package com.dubture.composer.ui.editor.composer.autoload;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

public class PathDropAdapter extends ViewerDropAdapter {

	private NamespaceModel target;
	private Psr0Section section;
	
	public PathDropAdapter(Viewer viewer, Psr0Section psr0Section) {
		super(viewer);
		this.section = psr0Section;
	}
	

	@Override
	public boolean performDrop(Object data) {
		
		if (data instanceof IResource[]) {
			IResource[] resources = (IResource[]) data;
			
			List<IFolder> folders = new ArrayList<IFolder>();

			for (IResource resource : resources) {
				if (resource instanceof IFolder) {
					folders.add((IFolder) resource);
				}
			}
			
			section.dropTargetReceived(target, folders);
			return false;
		}
		
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		
		if (target instanceof NamespaceModel) {
			this.target = (NamespaceModel) target;
			return true;
		}
		
		return false;
	}
}
