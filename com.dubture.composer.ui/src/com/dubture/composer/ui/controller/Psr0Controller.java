package com.dubture.composer.ui.controller;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

import com.dubture.composer.ui.ComposerUIPluginImages;
import com.dubture.getcomposer.core.collection.Psr0;
import com.dubture.getcomposer.core.objects.Namespace;

public class Psr0Controller extends StyledCellLabelProvider implements ITreeContentProvider {

	

	private Psr0 psr0;
	private Image namespaceImage = ComposerUIPluginImages.NAMESPACE.createImage();
	private Image pathImage = ComposerUIPluginImages.PACKAGE_FOLDER.createImage();
	
	private TreeViewer viewer;

	public Psr0Controller(TreeViewer viewer) {
		this.viewer = viewer;
	}
	
	public String getText(Object element) {
		
		if (element instanceof Namespace) {
			return ((Namespace)element).getNamespace();
		}
		
		return element.toString();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		psr0 = (Psr0) newInput;
	}

	public void update(ViewerCell cell) {
		Object obj = cell.getElement();
		String text = getText(obj);

		StyledString styledString = new StyledString(text);

		if (obj instanceof Namespace) {
			Namespace namespace = (Namespace) obj;
			styledString.append(" (" + namespace.size() + ")", StyledString.COUNTER_STYLER);
			cell.setImage(namespaceImage);
		} else {
			cell.setImage(pathImage);
		}

		cell.setText(styledString.toString());
		cell.setStyleRanges(styledString.getStyleRanges());

		super.update(cell);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Psr0) {
			Psr0 psr0 = (Psr0) parentElement;
			return psr0.getNamespaces().toArray();
		} else if (parentElement instanceof Namespace) {
			Namespace model = (Namespace) parentElement;
			return model.getPaths().toArray();
		}

		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		TreeItem item = null;
		for (TreeItem ri : viewer.getTree().getItems()) {
			for (TreeItem i : ri.getItems()) {
				if (i.getData() == element) {
					item = i;
					break;
				}
			}
		}

		if (item != null) {
			TreeItem parent = item.getParentItem();
			if (parent == null) {
				return psr0;
			}

			if (parent.getData() != null) {
				return parent.getData();
			}
		}

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}
}