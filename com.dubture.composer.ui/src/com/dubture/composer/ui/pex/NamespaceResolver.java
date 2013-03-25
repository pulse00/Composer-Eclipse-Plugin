package com.dubture.composer.ui.pex;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ModelException;
import org.pdtextensions.core.ui.extension.INamespaceResolver;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.model.ModelAccess;

public class NamespaceResolver implements INamespaceResolver {

	@Override
	public String resolve(IScriptFolder container) {
		
		IPath path;
		try {
			path = ModelAccess.getInstance().resolve(container.getUnderlyingResource());
			if (path != null) {
				return path.toString().replace("/", "\\");
			}
		} catch (ModelException e) {
			Logger.logException(e);
		}
		
		return null;
	}
}
