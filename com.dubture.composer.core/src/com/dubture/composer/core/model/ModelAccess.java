/*
 * This file is part of the PHPPackage Eclipse Plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.dubture.composer.core.model;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.core.IScriptProject;
import org.getcomposer.core.PackageInterface;
import org.osgi.service.prefs.BackingStoreException;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.log.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * Main ModelAccess to the workspaces composer model.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class ModelAccess implements NamespaceResolverInterface
{
    private PackageManager packageManager = null;

    private static ModelAccess instance = null;
    
    private Map<String, List<NamespaceMapping> > namespaceMap = new HashMap<String, List<NamespaceMapping> >();
    
    private Gson gson;
    
    private ModelAccess()
    {
        try {
            gson = new GsonBuilder()
                .registerTypeAdapter(PackageInterface.class, new PackageDeserializer())
                .registerTypeAdapter(IPath.class, new PathDeserializer())
                .create();
            
            initNamespaceMap();
        } catch (Exception e) {
            ComposerPlugin.logException(e);
        }
    }
    
    protected void initNamespaceMap() 
    {
        IEclipsePreferences instancePreferences = ConfigurationScope.INSTANCE.getNode(ComposerPlugin.ID);
        
        for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            String prefKey = "namespacemap#" + project.getName();
            String json = instancePreferences.get(prefKey, "{}");
            Type typeOfHashMap = new TypeToken<List<NamespaceMapping>>() { }.getType();
            List<NamespaceMapping> newMap = gson.fromJson(json, typeOfHashMap);
            namespaceMap.put(project.getName(), newMap);
            Logger.debug("loading namespacemap from preferences for project " + project.getName() + " " + json);
        }
    }

    public static ModelAccess getInstance()
    {
        if (instance == null) {
            instance = new ModelAccess();
        }
        
        return instance;
    }

    @Override
    public IPath resolve(IResource resource)
    {
        IPath root = resource.getFullPath().removeFirstSegments(1);
        
        if (!namespaceMap.containsKey(resource.getProject().getName())) {
            return null;
        }
        
        List<NamespaceMapping> namespaces = namespaceMap.get(resource.getProject().getName());
        
        for(NamespaceMapping mapping : namespaces) {
            if (root.toString().startsWith(mapping.getPath())) {
                return new Path(mapping.getNamespace());
            }
        }
        
        return null;
    }

    public PackageManager getPackageManager()
    {
        if (getInstance().packageManager == null) {
            PackageManager manager = new PackageManager();
            synchronized (instance) {
                if (instance.packageManager == null) { // ensure another
                    // package manager
                    // was not set while
                    // creating the
                    // instance above
                    instance.packageManager = manager;
                }
            }
        }

        return instance.packageManager;
    }

    public void updateNamespaces(List<NamespaceMapping> namespaces,
            IScriptProject scriptProject)
    {
        String json = gson.toJson(namespaces);
        IEclipsePreferences instancePreferences = ConfigurationScope.INSTANCE.getNode(ComposerPlugin.ID);
        
        namespaceMap.put(scriptProject.getProject().getName(), namespaces);
        instancePreferences.put("namespacemap#"+scriptProject.getProject().getName(), json);
        Logger.debug("updating namespacemap for project " + scriptProject.getProject().getName());
        try {
            instancePreferences.flush();
        } catch (BackingStoreException e) {
            Logger.logException(e);
        }
    }

    public List<NamespaceMapping> getNamespaceMappings(IProject project)
    {
        if (namespaceMap.containsKey(project.getName())) {
            return namespaceMap.get(project.getName());
        }
        
        return null;
    }

    public IResource getComposer(InstalledPackage installed, IScriptProject project)
    {
        if (!namespaceMap.containsKey(project.getProject().getName())) {
            return null;
        }
        
        for (NamespaceMapping mapping : namespaceMap.get(project.getProject().getName())) {
            
            if (mapping.getPath().contains(installed.name)) {
                IPath path = new Path(mapping.getPath().substring(0, mapping.getPath().lastIndexOf(installed.name)+installed.name.length()));
                if (installed.targetDir != null && installed.targetDir.length() > 0) {
                    path = path.append(installed.targetDir);
                }
                return project.getProject().findMember(path.append("composer.json"));
            }
        }
        return null;
    }
}
