package com.dubture.composer.core.model;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.core.BuildpathContainerInitializer;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.core.util.Util;
import org.eclipse.php.internal.core.project.PHPNature;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.service.prefs.BackingStoreException;

import com.dubture.composer.core.ComposerBuildpathContainerInitializer;
import com.dubture.composer.core.ComposerNature;
import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.build.InstalledPackage;
import com.dubture.composer.core.log.Logger;

@SuppressWarnings("restriction")
public class PackageManager
{
    private Map<String, BuildpathPackage> packages;
    
    /**
     * Maps project to installed local packages
     */
    private Map<String, List<InstalledPackage>> installedPackages;
    
    private Map<String, List<InstalledPackage>> installedDevPackages;
    
    public final static String BP_COMPOSERPACKAGE_PREFERENCES_PREFIX = ComposerPlugin.ID
            + ".composerPackage."; //$NON-NLS-1$
    
    public final static String BP_PROJECT_BUILDPATH_PREFIX = ComposerPlugin.ID + ".projectPackages#";
    public final static String BP_PROJECT_BUILDPATH_DEV_PREFIX = ComposerPlugin.ID + ".projectDevPackages#";
    
    private BuildpathJob buildpathJob;
    
    public PackageManager() {
        initialize();
    }
    
    private void reloadPackages() {
        
        IEclipsePreferences instancePreferences = ConfigurationScope.INSTANCE.getNode(ComposerPlugin.ID);
        
        String[] propertyNames;
        try {
            propertyNames = instancePreferences.keys();
        } catch (BackingStoreException e) {
            Util.log(e, "Exception while initializing user libraries"); //$NON-NLS-1$
            return;
        }
        
        for (int i = 0, length = propertyNames.length; i < length; i++) {
            String propertyName = propertyNames[i];
            if (propertyName.startsWith(BP_PROJECT_BUILDPATH_PREFIX)) {
                String propertyValue = instancePreferences.get(propertyName,null);
                if (propertyValue != null) {
                    try {
                        List<InstalledPackage> packages = InstalledPackage.deserialize(propertyValue);
                        installedPackages.put(unpackProjectName(propertyName), packages);
                    } catch (IOException e) {
                        Logger.logException(e);
                    }
                }
            } else if (propertyName.startsWith(BP_PROJECT_BUILDPATH_DEV_PREFIX)) {
                String propertyValue = instancePreferences.get(propertyName,null);
                if (propertyValue != null) {
                    try {
                        List<InstalledPackage> packages = InstalledPackage.deserialize(propertyValue);
                        installedDevPackages.put(unpackProjectName(propertyName), packages);
                    } catch (IOException e) {
                        Logger.logException(e);
                    }
                }
            }
        }
    }
    
    private String unpackProjectName(String propertyName) {
        
        String[] strings = propertyName.split("#");
        return strings[1];
    }
    
    private void initialize() {
        
        packages = new HashMap<String, BuildpathPackage>();
        installedPackages = new HashMap<String, List<InstalledPackage>>();
        installedDevPackages = new HashMap<String, List<InstalledPackage>>();
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
                    packages.put(libName, library);
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
        
        buildpathJob = new BuildpathJob();
        
        reloadPackages();
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
        
        Set<String> set = packages.keySet();
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
                    .getBuildpathContainerInitializer(ComposerBuildpathContainerInitializer.CONTAINER);
            
            IBuildpathContainer suggestedContainer = new ComposerBuildpathContainer(new Path(ComposerBuildpathContainerInitializer.CONTAINER), project);
            
            // creates a global composer package if the version doesn't exist yet
            initializer.requestBuildpathContainerUpdate(suggestedContainer.getPath(), createPlaceholderProject(), suggestedContainer);
            
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
        
        try {
            IBuildpathContainer container = ModelManager.getModelManager().getBuildpathContainer(new Path(ComposerBuildpathContainerInitializer.CONTAINER), project);
            
            for (IBuildpathEntry entry : container.getBuildpathEntries()) {
                packagePaths.add(new PackagePath(entry, project));
            }
        } catch (ModelException e) {
            StatusManager.getManager().handle(e.getStatus());
        }
        
        return packagePaths.toArray(new PackagePath[packagePaths
                .size()]);
    }
    
    public void updateBuildpath() {

        if (buildpathJob == null) {
            return;
        }
        
        synchronized (buildpathJob) {
            buildpathJob.cancel();
            buildpathJob.setPriority(Job.LONG);
            buildpathJob.schedule(1000);
        }
    }
    
    private class BuildpathJob extends Job {

        private IPath installedPath;
        private IPath installedDevPath;
        
        private boolean running;
        
        public BuildpathJob()
        {
            super("Updating composer buildpath");
            installedPath = new Path("vendor/composer/installed.json");
            installedDevPath = new Path("vendor/composer/installed_dev.json");
        }
        
        private void installLocalPackage(InstalledPackage installedPackage,
                IProject project)
        {
            IResource resource = project.findMember(new Path("vendor").append(installedPackage.name));
            
            if (resource instanceof IFolder) {
                IFolder folder = (IFolder) resource;
                File file = folder.getRawLocation().makeAbsolute().toFile();
                
                if (file != null && file.exists() && installedPackage.getLocalFile() != null) {
                    try {
                        Logger.debug("Installing local package " + installedPackage.name + " to " + installedPackage.getLocalFile().getAbsolutePath());
                        installedPackage.getLocalFile().mkdirs();
                        FileUtils.copyDirectory(file, installedPackage.getLocalFile());
                    } catch (IOException e) {
                        Logger.logException(e);
                    }
                }
            }
        }
        
        @Override
        protected void canceling()
        {
            super.canceling();
            running = false;
        }
        
        private void handleDevPackages(IProject project) throws Exception {
            
            handlePackages(project, BP_PROJECT_BUILDPATH_DEV_PREFIX+ project.getName(), installedDevPath);            
        }
        
        private void handleProdPackages(IProject project) throws Exception {
            
            handlePackages(project, BP_PROJECT_BUILDPATH_PREFIX + project.getName(), installedPath);
            
        }
        
        private void handlePackages(IProject project, String propertyName, IPath path) throws Exception {

            IFile installed = (IFile) project.findMember(path);
            
            if (installed == null) {
                Logger.debug("Unable to find 'installed.json' in " + project.getName());
                return;
            }
            List<InstalledPackage> json = InstalledPackage.deserialize(installed.getContents());
            installPackages(json, project);
            persist(propertyName, installed);
        }
        
        private void persist(String key, IFile file) throws IOException, CoreException, BackingStoreException {
            
            IEclipsePreferences prefs = ConfigurationScope.INSTANCE.getNode(ComposerPlugin.ID);
            StringWriter writer = new StringWriter();
            IOUtils.copy(file.getContents(), writer);
            String propertyValue = writer.toString();
            prefs.put(key, propertyValue);
            prefs.flush();
            writer.close();
        }
        
        private void installPackages(List<InstalledPackage> packages, IProject project) {
            for (InstalledPackage installedPackage : packages) {

                if (!installedPackage.isLocalVersionAvailable()) {
                    installLocalPackage(installedPackage, project);
                }
            }
        }
        
        
        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
            running = true;
            monitor.setTaskName("Updating composer buildpath...");
            
            for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {

                try {
                    if (!running) {
                        return Status.CANCEL_STATUS;
                    }
                    
                    if (!project.hasNature(ComposerNature.NATURE_ID)) {
                        monitor.worked(1);
                        continue;
                    }

                    IFile installed = (IFile) project.findMember(installedPath);
                    
                    if (installed == null) {
                        Logger.debug("Unabled to find installed.json in project " + project.getName());
                        continue;
                    }
                    
                    handleProdPackages(project);
                    handleDevPackages(project);
                    DLTKCore.refreshBuildpathContainers(DLTKCore.create(project));
                    
                } catch (Exception e) {
                    Logger.logException(e);
                }
                
                monitor.worked(1);
            }
            
            reloadPackages();
            return Status.OK_STATUS;
        }
    }

    public List<InstalledPackage> getInstalledPackages(IScriptProject project)
    {
        if (installedPackages.containsKey(project.getProject().getName())) {
            return installedPackages.get(project.getProject().getName());
        }
        
        return null;
    }
    
    public List<InstalledPackage> getInstalledDevPackages(IScriptProject project) {
        
        if (installedDevPackages.containsKey(project.getProject().getName())) {
            return installedDevPackages.get(project.getProject().getName());
        }
        
        return null;
    }
    
    public List<InstalledPackage> getAllPackages(IScriptProject project) {
        
        List<InstalledPackage> allPackages = new ArrayList<InstalledPackage>();
        
        if (!installedPackages.containsKey(project.getProject().getName())) {
            return allPackages;
        }
        
        for (InstalledPackage pack : installedPackages.get(project.getProject().getName())) {
            pack.isDev = false;
            allPackages.add(pack);
        }
        
        if (installedDevPackages.containsKey(project.getProject().getName())) {
            for (InstalledPackage pack : installedDevPackages.get(project.getProject().getName())) {
                pack.isDev = true;
                allPackages.add(pack);
            }
        }
        
        return allPackages;
    }

    public void removeProject(IProject project)
    {
        try {
            
            String name = project.getName();
            String propertyName = BP_PROJECT_BUILDPATH_PREFIX + name;
            IEclipsePreferences instancePreferences = ConfigurationScope.INSTANCE.getNode(ComposerPlugin.ID);
            instancePreferences.remove(propertyName);
            instancePreferences.flush();
            
            if (installedPackages.containsKey(name)) {
                installedPackages.remove(name);
            }
            
        } catch (BackingStoreException e) {
            Logger.logException(e);
        }
    }
}
