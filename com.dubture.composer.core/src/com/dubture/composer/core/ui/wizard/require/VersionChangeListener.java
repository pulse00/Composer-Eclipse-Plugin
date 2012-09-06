package com.dubture.composer.core.ui.wizard.require;

import com.dubture.composer.PackageInterface;

public interface VersionChangeListener
{
    void versionChanged(PackageInterface phpPackage, String versionName);
}
