/*
 * This file is part of the Composer Eclipse Plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.dubture.composer.core.model;

import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.dubture.composer.core.visitor.ComposerFieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 */
public class Composer implements NamespaceResolverInterface
{
    private String name;
    private String type;
    private String description;
    private String homepage;
    private String fullPath;
    private Map<String, String> require;
    private Map<String, String> requireDev;
    private Autoload autoload;
    private String targetDir;
    
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
    

    public IPath getPath()
    {
        return new Path(fullPath);
    }
    
    public String getName()
    {
        return name;
    }
    
    public static Composer fromJson(IFile input) throws CoreException
    {
        Gson gson = getBuilder();
        InputStreamReader reader = new InputStreamReader(input.getContents());
        Composer composer = gson.fromJson(reader, Composer.class);
        composer.setFullPath(input.getFullPath().toString());
        
        return composer;

    }
    
    protected static Gson getBuilder() 
    {
        return new GsonBuilder()
            .setFieldNamingStrategy(new ComposerFieldNamingStrategy())
            .create();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Composer)) {
            return false;
        }
        
        Composer other = (Composer) obj;
        return getPath().equals(other.getPath()) 
                && getName().equals(other.getName()); 
    }
    
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public IPath resolve(IResource resource)
    {
        if (autoload == null) {
            return null;
        }
        
        IPath ns = null;
        IPath path = resource.getFullPath();
        IPath composerPath = getPath();
        IPath psr0Path = composerPath.append(autoload.getPSR0Path());
        int segments = psr0Path.segmentCount();
        
        if (path.matchingFirstSegments(psr0Path) == segments) {
            
            if (targetDir != null && targetDir.length() > 0) {
                Path target = new Path(targetDir);
                ns = target.append(path.removeFirstSegments(psr0Path.segmentCount()));    
            } else {
                ns = path.removeFirstSegments(psr0Path.segmentCount());
            }
            
        }
        
        return ns;
    }

    public String getTargetDir()
    {
        return targetDir;
    }

    public void setTargetDir(String targetDir)
    {
        this.targetDir = targetDir;
    }
    
    public String getFullPath()
    {
        return fullPath;
    }

    public void setFullPath(String fullPath)
    {
        this.fullPath = fullPath;
    }

    public Map<String, String> getRequireDev()
    {
        return requireDev;
    }

    public void setRequireDev(Map<String, String> requireDev)
    {
        this.requireDev = requireDev;
    }
}
