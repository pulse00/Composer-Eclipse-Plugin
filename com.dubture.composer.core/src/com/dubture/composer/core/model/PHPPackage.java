/*
 * This file is part of the PHPPackage Eclipse Plugin.
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
import org.pex.core.model.InstallableItem;

import com.dubture.composer.core.visitor.ComposerFieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 */
public class PHPPackage implements NamespaceResolverInterface, InstallableItem
{
    private String name;
    private String type;
    private String description;
    private String homepage;
    private String url;
    private String fullPath;
    private Map<String, String> require;
    private Map<String, String> requireDev;
    private Autoload autoload;
    private String targetDir;
    
    public String version;
    public String versionNormalized;
    
    public String[] license;
    
    public String[] keywords;
    
    public Map<String, PHPPackage> versions;
    
    public String toString()
    {
        return getName();
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
        if (fullPath != null) {
            return new Path(fullPath);
        }
        
        return null;
    }
    
    public String getName()
    {
        return name;
    }
    
    public static PHPPackage fromJson(IFile input) throws CoreException
    {
        Gson gson = getBuilder();
        InputStreamReader reader = new InputStreamReader(input.getContents());
        PHPPackage pHPPackage = gson.fromJson(reader, PHPPackage.class);
        pHPPackage.setFullPath(input.getFullPath().toString());
        
        return pHPPackage;

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
        if (!(obj instanceof PHPPackage)) {
            return false;
        }
        
        PHPPackage other = (PHPPackage) obj;
        
        IPath path = getPath();
        
        if (path == null) {
            return getName().equals(other.getName());
        }
        
        return getPath().equals(other.getPath()) 
                && getName().equals(other.getName()); 
    }
    
    @Override
    public int hashCode()
    {
        return getName().hashCode();
    }

    @Override
    public IPath resolve(IResource resource)
    {
        if (autoload == null || autoload.getPSR0Path() == null) {
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

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
    
    public String getDefaultVersion()
    {
        return versions.keySet().iterator().next();
    }
    
    public String getPackageName(String version) throws Exception
    {
        if (!versions.containsKey(version)) {
            throw new Exception("Invalid version " + version + " for package " + name);
        }
        
        return String.format("%s:%s", name, version);
    }
}
