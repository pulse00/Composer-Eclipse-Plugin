/*
 * This file is part of the Composer Eclipse Plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.dubture.composer.core.model;

import java.util.Map;

import org.eclipse.core.resources.IFile;

/**
 *
 */
public class Composer
{
    private String name;
    private String type;
    private String description;
    private String homepage;
    private Map<String, String> require;
    private Autoload autoload;
    private IFile file;
    
    public String toString()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getHomepage()
    {
        return homepage;
    }

    public void setHomepage(String homepage)
    {
        this.homepage = homepage;
    }

    public Map<String, String> getRequire()
    {
        return require;
    }

    public void setRequire(Map<String, String> require)
    {
        this.require = require;
    }

    public Autoload getAutoload()
    {
        return autoload;
    }

    public void setAutoload(Autoload autoload)
    {
        this.autoload = autoload;
    }

    public IFile getFile()
    {
        return file;
    }

    public void setFile(IFile file)
    {
        this.file = file;
    }
}
