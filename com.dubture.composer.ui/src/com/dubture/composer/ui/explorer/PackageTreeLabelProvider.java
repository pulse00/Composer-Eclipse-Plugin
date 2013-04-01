package com.dubture.composer.ui.explorer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.php.internal.ui.util.PHPPluginImages;
import org.eclipse.swt.graphics.Image;

import com.dubture.composer.core.model.PackagePath;

@SuppressWarnings("restriction")
public class PackageTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof PackagePath) {
			PackagePath path = (PackagePath) element;
			return path.getPackageName();
		}

		return null;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof PackagePath) {
			return PHPPluginImages.get(PHPPluginImages.IMG_OBJS_LIBRARY);
		}

		return null;
	}
}
