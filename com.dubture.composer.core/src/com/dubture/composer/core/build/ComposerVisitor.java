package com.dubture.composer.core.build;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.model.ModelAccess;
import com.dubture.composer.core.model.PackageManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ComposerVisitor implements IResourceVisitor
{

    @Override
    public boolean visit(IResource resource) throws CoreException
    {
        if(resource.getName().equals("installed.json")) {
            PackageManager manager = ModelAccess.getInstance().getPackageManager();
            manager.updateBuildpath();
            return false;
        }
        return true;
    }
    
    public static class InstalledPackage {
        
        private IPath path;
        
        public InstalledPackage() {
            
        }
        
        public String name;
        public String version;
        public String version_normalized;
        public String project;
        
        private File localFile;
        
        public IPath getPath() {
            
            if (path == null) {
                path = new Path(name);
            }
            return path;
        }
        
        public File getLocalFile() {
            
            if (localFile == null) {
                IPath location = ComposerPlugin.getDefault().getStateLocation();
                IPath localPath = location.append("packages").append(getPath()).append(version);
                localFile = localPath.toFile();
                
            }
            
            return localFile;
            
        }
        
        public boolean isLocalVersionAvailable() {
            
            if (getLocalFile() != null && !getLocalFile().exists()) {
                return false;
            }
            
            return getLocalFile().list().length > 0;
        }
        
        public static List<InstalledPackage> deserialize(InputStream input) throws IOException {
            
            Gson gson = new GsonBuilder().create();
            InputStreamReader reader = new InputStreamReader(input);
            Type listOfObjects = new TypeToken<List<InstalledPackage>>(){}.getType();
            List<InstalledPackage> result = gson.fromJson(reader, listOfObjects);
            reader.close();
            return result;
        }

        public static List<InstalledPackage> deserialize(String propertyValue) throws IOException
        {
            return deserialize(new ByteArrayInputStream(propertyValue.getBytes()));
        }

        public IBuildpathEntry getBuildpathEntry()
        {
            IPath libPath =  Path.fromOSString(getLocalFile().getAbsolutePath()).makeAbsolute();
            
            IPath fullPath = EnvironmentPathUtils.getFullPath(
                    EnvironmentManager.getLocalEnvironment(), libPath);
            
            return DLTKCore.newLibraryEntry(fullPath, false, true);
        }
    }
}
