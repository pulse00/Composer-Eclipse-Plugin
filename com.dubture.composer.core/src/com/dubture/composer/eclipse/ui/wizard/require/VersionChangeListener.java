package com.dubture.composer.eclipse.ui.wizard.require;

import org.getcomposer.core.PackageInterface;

public interface VersionChangeListener
{
    void versionChanged(PackageInterface phpPackage, String versionName);
}
