package com.dubture.composer.core.ui.wizard.require;

import com.dubture.composer.core.model.PHPPackage;

public interface VersionChangeListener
{
    void versionChanged(PHPPackage phpPackage, String versionName);
}
