/*
 * This file is part of the PHPPackage Eclipse Plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.dubture.composer.core.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.core.IScriptProject;
import org.getcomposer.core.collection.Psr0;
import org.getcomposer.core.objects.Namespace;
import org.json.simple.parser.ParseException;
import org.osgi.service.prefs.BackingStoreException;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.log.Logger;

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
    private Map<String, Psr0> psr0Map = new HashMap<String, Psr0>();
    
    private ModelAccess()
    {
        try {
            initNamespaceMap();
        } catch (Exception e) {
            ComposerPlugin.logException(e);
        }
    }
    
    protected void initNamespaceMap() throws ParseException 
    {
        IEclipsePreferences instancePreferences = ConfigurationScope.INSTANCE.getNode(ComposerPlugin.ID);
        for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            String prefKey = "namespacemap#" + project.getName();
            String json = instancePreferences.get(prefKey, "{}");
            psr0Map.put(project.getName(), new Psr0(json));
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
        
        if (!psr0Map.containsKey(resource.getProject().getName())) {
            return null;
        }
        
        Psr0 namespaces = psr0Map.get(resource.getProject().getName());
        
        for(Namespace namespace : namespaces) {
        	for(Object object : namespace.getPaths()) {
        		if (!(object instanceof String)) {
        			continue;
        		}
        		String path = (String) object;
        		if (root.toString().startsWith((String) path)) {
        			String replacement = path;
        			if (!replacement.endsWith("/")) {
        				replacement += "/";
        			}
        			return new Path(root.toString().replace(replacement, ""));
        		}
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

    public void updatePsr0(Psr0 psr0, IScriptProject scriptProject)
    {
        String json = psr0.toJson();
        IEclipsePreferences instancePreferences = ConfigurationScope.INSTANCE.getNode(ComposerPlugin.ID);
        psr0Map.put(scriptProject.getProject().getName(), psr0);
        instancePreferences.put("namespacemap#"+scriptProject.getProject().getName(), json);
        Logger.debug("updating namespacemap for project " + scriptProject.getProject().getName());
        try {
            instancePreferences.flush();
        } catch (BackingStoreException e) {
            Logger.logException(e);
        }
    }

    public Psr0 getNamespaceMappings(IProject project)
    {
        if (psr0Map.containsKey(project.getName())) {
            return psr0Map.get(project.getName());
        }
        
        return null;
    }

    public IResource getComposer(InstalledPackage installed, IScriptProject project)
    {
    	/*
        if (!namespaceMap.containsKey(project.getProject().getName())) {
            return null;
        }
        
        for (Namespace mapping : namespaceMap.get(project.getProject().getName())) {
            
            if (mapping.getPath().contains(installed.name)) {
                IPath path = new Path(mapping.getPath().substring(0, mapping.getPath().lastIndexOf(installed.name)+installed.name.length()));
                if (installed.targetDir != null && installed.targetDir.length() > 0) {
                    path = path.append(installed.targetDir);
                }
                return project.getProject().findMember(path.append(ComposerConstants.COMPOSER_JSON));
            }
        }
        */
        return null;
        
    }
}
