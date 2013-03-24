package com.dubture.composer.ui.editor.composer.autoload;

public class NamespacePath {
	
	private NamespaceModel parent;
	private String path;

	public NamespacePath(final NamespaceModel parent, final String path) {
		this.parent = parent;
		this.path = path;
	}
	
	@Override
	public String toString() {
		return path;
	}

	public NamespaceModel getParent() {
		return parent;
	}
}
