package com.dubture.composer.ui.parts.composer;

import org.getcomposer.core.collection.Dependencies;

public interface DependencySelectionFinishedListener {
	public void dependenciesSelected(Dependencies dependencies);
}
