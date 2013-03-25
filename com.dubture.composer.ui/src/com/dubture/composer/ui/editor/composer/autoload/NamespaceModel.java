package com.dubture.composer.ui.editor.composer.autoload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.getcomposer.core.objects.Namespace;

/**
 * Used for the TreeViewer in the {@link Psr0Section} of the {@link AutoloadPage}
 */
class NamespaceModel {

	public String key;
	public Namespace namespace;
	private List<NamespacePath> paths;
	
	public NamespaceModel(String key, Namespace value) {
		this.key = key;
		this.namespace = value;
		
		List<NamespacePath> paths = new ArrayList<NamespacePath>();
		for (Object p: value.getPaths()) {
			paths.add(new NamespacePath(this, (String) p));
		}
		
		this.paths = paths;
	}
	
	public String getPathsAsString() {

		StringBuilder builder = new StringBuilder();
		
		Object[] array = namespace.getPaths().toArray();
		List<Object> list = new ArrayList<Object>(Arrays.asList(array));
		builder.append( list.remove(0));

		for(Object s : list) {
		    builder.append( ", ");
		    builder.append( ((String)s).trim());
		}

		return builder.toString();			
	}
	
	public List<NamespacePath> getPaths() {
		
		return paths;
	}

	@Override
	public String toString() {
		return key;
	}
}