package com.dubture.composer.ui.wizard.require;

import org.getcomposer.core.core.PackageInterface;

public interface VersionChangeListener
{
    void versionChanged(PackageInterface phpPackage, String versionName);
}
