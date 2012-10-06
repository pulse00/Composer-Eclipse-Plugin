package com.dubture.composer.core.model;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.core.BuildpathContainerInitializer;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.internal.core.UserLibraryBuildpathContainerInitializer;
import org.eclipse.dltk.internal.core.util.Util;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.php.internal.core.buildpath.BuildPathUtils;
import org.eclipse.php.internal.core.includepath.IncludePath;
import org.eclipse.php.internal.core.includepath.IncludePathManager;
import org.eclipse.php.internal.core.preferences.CorePreferencesSupport;
import org.eclipse.php.internal.core.project.PHPNature;
import org.osgi.service.prefs.BackingStoreException;

import com.dubture.composer.core.ComposerBuildpathContainerInitializer;
import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.log.Logger;

@SuppressWarnings("restriction")
public class PackageManager
{
    private Map<String, BuildpathPackage> packages;
    
    public final static String BP_COMPOSERPACKAGE_PREFERENCES_PREFIX = ComposerPlugin.ID
            + ".composerPackage."; //$NON-NLS-1$
    
    public PackageManager() {
        initialize();
    }
    
    private void initialize() {
        this.packages = new HashMap<String, BuildpathPackage>();
        IEclipsePreferences instancePreferences = ConfigurationScope.INSTANCE.getNode(ComposerPlugin.ID);
        
        String[] propertyNames;
        try {
            propertyNames = instancePreferences.keys();
        } catch (BackingStoreException e) {
            Util.log(e, "Exception while initializing user libraries"); //$NON-NLS-1$
            return;
        }

        boolean preferencesNeedFlush = false;
        for (int i = 0, length = propertyNames.length; i < length; i++) {
            String propertyName = propertyNames[i];
            if (propertyName.startsWith(BP_COMPOSERPACKAGE_PREFERENCES_PREFIX)) {
                String propertyValue = instancePreferences.get(propertyName,
                        null);
                if (propertyValue != null) {
                    String libName = propertyName
                            .substring(BP_COMPOSERPACKAGE_PREFERENCES_PREFIX
                                    .length());
                    StringReader reader = new StringReader(propertyValue);
                    BuildpathPackage library;
                    try {
                        library = BuildpathPackage.createFromString(reader);
                    } catch (Exception e) {
                        Util
                                .log(
                                        e,
                                        "Exception while initializing user library " + libName); //$NON-NLS-1$
                        instancePreferences.remove(propertyName);
                        preferencesNeedFlush = true;
                        continue;
                    }
                    this.packages.put(libName, library);
                }
            }
        }
        if (preferencesNeedFlush) {
            try {
                instancePreferences.flush();
            } catch (BackingStoreException e) {
                Util.log(e, "Exception while flusing instance preferences"); //$NON-NLS-1$
            }
        }
    }    
    public synchronized void setPackage(String name, IBuildpathEntry[] buildpathEntries,
            boolean isSystemLibrary)
    {
        
        IEclipsePreferences prefs = ConfigurationScope.INSTANCE.getNode(ComposerPlugin.ID);
        String propertyName = BP_COMPOSERPACKAGE_PREFERENCES_PREFIX
                + makePackageName(name);
        try {
            String propertyValue = BuildpathPackage.serialize(buildpathEntries,
                    isSystemLibrary);
            
            prefs.put(propertyName, propertyValue);
            prefs.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void removePackage(String name)
    {
        try {
            IEclipsePreferences preferences = ConfigurationScope.INSTANCE.getNode(ComposerPlugin.ID);
            
            String propertyName = BP_COMPOSERPACKAGE_PREFERENCES_PREFIX
                    + makePackageName(name);
            
            preferences.remove(propertyName);
            preferences.flush();
        } catch (BackingStoreException e) {
            Util.log(e, "Exception while removing user library " + name); //$NON-NLS-1$
        }
    }

    public BuildpathPackage getPackage(String packageName)
    {
        if (!packages.containsKey(packageName)) {
            return null;
        }
        return (BuildpathPackage) packages.get(makePackageName(packageName));        
    }

    private Object makePackageName(String packageName)
    {
        return PHPNature.ID + "#" + packageName; //$NON-NLS-1$        
    }
    
    private String getPackageName(String key) {
        int pos = key.indexOf("#"); //$NON-NLS-1$
        if (pos != -1) {
            return key.substring(pos + 1);
        }
        return key;
    }
    
    
    public synchronized String[] getPackageNames() {
        
        Set<String> set = this.packages.keySet();
        Set<String> result = new HashSet<String>();
        for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            result.add(getPackageName(key));
        }

        return (String[]) result.toArray(new String[result.size()]);
    }

    /**
     * @param project
     * @param composer
     * @param composerPackage
     * @param monitor
     */
    public void createBuildpathEntry(IScriptProject project, IResource composer,
            EclipsePHPPackage composerPackage, IProgressMonitor monitor)
    {
        try {
            
            BuildpathContainerInitializer initializer = DLTKCore
                    .getBuildpathContainerInitializer(ComposerBuildpathContainerInitializer.PACKAGE_PATH);
            
            IBuildpathContainer suggestedContainer = new ComposerBuildpathContainer(project);
            
            // creates a global composer package if the version doesn't exist yet
            initializer.requestBuildpathContainerUpdate(suggestedContainer.getPath(), createPlaceholderProject(), suggestedContainer);
            
            // create the main composer buildpathentry
            List<IBuildpathEntry> entries = new ArrayList<IBuildpathEntry>();
            entries.add(DLTKCore.newContainerEntry(suggestedContainer.getPath()));
            
            // add the buildpaths to the project
            BuildPathUtils.addEntriesToBuildPath(project, entries);
            
//          save the ComposerBuildPathEntry in the include path of the project
            IncludePathManager.getInstance().addEntriesToIncludePath(project.getProject(),entries);
        } catch (Exception e) {
            Logger.logException(e);
        }
    }
    
    private static IScriptProject createPlaceholderProject() {
        String name = "####internal"; //$NON-NLS-1$
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        while (true) {
            IProject project = root.getProject(name);
            if (!project.exists()) {
                return DLTKCore.create(project);
            }
            name += '1';
        }
    }

    public PackagePath[] getPackagePaths(IScriptProject project)
    {
        List<PackagePath> packagePaths = new ArrayList<PackagePath>();
        IncludePath[] includePaths = IncludePathManager.getInstance().getIncludePaths(project.getProject());
        IPath composerPath = new Path(ComposerBuildpathContainerInitializer.PACKAGE_PATH);
        
        for (IncludePath includePath : includePaths) {
            if (includePath.getEntry() instanceof IBuildpathEntry) {
                IBuildpathEntry entry = (IBuildpathEntry) includePath.getEntry();
                if (composerPath.equals(entry.getPath())) {
                    try {
                        IBuildpathContainer container = DLTKCore.getBuildpathContainer(entry.getPath(), project);
                        if (container != null) {
                            for (IBuildpathEntry bpEntry : container.getBuildpathEntries()) {
                                
                                PackagePath ppath = new PackagePath(bpEntry, project);
                                packagePaths.add(ppath);
                            }
                            break;
                        }
                    } catch (ModelException e) {
                        Logger.logException(e);
                    }
                }
            }
        }
        
        return packagePaths.toArray(new PackagePath[packagePaths
                .size()]);        
    }
}
