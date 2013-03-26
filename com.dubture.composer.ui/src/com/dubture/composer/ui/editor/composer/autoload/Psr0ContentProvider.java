package com.dubture.composer.ui.editor.composer.autoload;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;
import org.getcomposer.core.collection.Psr0;
import org.getcomposer.core.objects.Namespace;

import com.dubture.composer.ui.ComposerUIPluginImages;

class Psr0ContentProvider extends StyledCellLabelProvider implements ITreeContentProvider {

	private final Psr0Section psr0Section;

	/**
	 * @param psr0Section
	 */
	Psr0ContentProvider(Psr0Section psr0Section) {
		this.psr0Section = psr0Section;
	}

	private Psr0 psr0;
	private Image namespaceImage = ComposerUIPluginImages.NAMESPACE.createImage();
	private Image pathsImage = ComposerUIPluginImages.PACKAGE_FOLDER.createImage();

	public String getText(Object element) {
		
		if (element instanceof NamespaceModel && element.toString().length() == 0) {
			return "[Fallback Namespace]";
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

		if (obj instanceof NamespaceModel) {
			NamespaceModel model = (NamespaceModel) obj;
			int count = model.namespace.size();
			styledString.append(" (" + count + ")", StyledString.COUNTER_STYLER);
			cell.setImage(namespaceImage);
		} else {
			cell.setImage(pathsImage);
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

			List<NamespaceModel> elements = new ArrayList<NamespaceModel>();

			Iterator<Namespace> iterator = psr0.iterator();

			while (iterator.hasNext()) {
				Namespace namespace = iterator.next();
				elements.add(new NamespaceModel(namespace.getNamespace(), namespace));
			}

			return elements.toArray();
		} else if (parentElement instanceof NamespaceModel) {
			NamespaceModel model = (NamespaceModel) parentElement;
			return model.getPaths().toArray();
		}

		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		TreeItem item = null;
		for (TreeItem ri : this.psr0Section.psr0Viewer.getTree().getItems()) {
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