package com.dubture.composer.core.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IAccessRule;
import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;

import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.log.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Represents a package inside installed(_dev).json
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class InstalledPackage
{
    private IPath path;
    private File localFile;
    
    public String name;
    public String version;
    public String version_normalized;
    public String project;
    public boolean isDev;
    public Map<String, String> require;
    public Map<String, String> requireDev;
    public Map<String, String> suggest;
    public String targetDir;
    
    /**
     * Do not remove the no-args constructor! It's needed by gson for deserializing
     * from json.
     */
    public InstalledPackage()
    {
        
    }
    
    public IPath getPath()
    {
        if (path == null) {
            path = new Path(name);
        }
        return path;
    }

    public File getLocalFile()
    {
        if (localFile == null) {
            IPath location = ComposerPlugin.getDefault().getStateLocation();
            IPath localPath = location.append("packages").append(getPath())
                    .append(version);
            localFile = localPath.toFile();
            
            Logger.debug("Retrieving local filepath for " + name + ":");
            Logger.debug(localFile.getAbsolutePath());
        }

        return localFile;

    }

    public boolean isLocalVersionAvailable()
    {

        if (getLocalFile() != null && !getLocalFile().exists()) {
            return false;
        }

        return getLocalFile().list().length > 0;
    }

    public static List<InstalledPackage> deserialize(InputStream input)
            throws IOException
    {
//        Gson gson = new GsonBuilder().setFieldNamingStrategy(new ComposerFieldNamingStrategy()).create();
        Gson gson = new GsonBuilder().create();
        InputStreamReader reader = new InputStreamReader(input);
        Type listOfObjects = new TypeToken<List<InstalledPackage>>()
        {
        }.getType();
        List<InstalledPackage> result = gson.fromJson(reader, listOfObjects);
        reader.close();
        return result;
    }

    public static List<InstalledPackage> deserialize(String propertyValue)
            throws IOException
    {
        return deserialize(new ByteArrayInputStream(propertyValue.getBytes()));
    }

    public IBuildpathEntry getBuildpathEntry()
    {
        IPath libPath = Path.fromOSString(getLocalFile().getAbsolutePath())
                .makeAbsolute();

        IPath fullPath = EnvironmentPathUtils.getFullPath(
                EnvironmentManager.getLocalEnvironment(), libPath);

        IPath[] excludes = new IPath[]{new Path(".git/")/*
                                                         * , new Path("docs/"),
                                                         * new Path("tests/")
                                                         */};
        return DLTKCore
                .newLibraryEntry(fullPath, new IAccessRule[0],
                        new IBuildpathAttribute[0], new IPath[0], excludes,
                        false, true);
    }

    public boolean isRequiredBy(InstalledPackage dependency)
    {
        return dependency.requires(this);

    }

    public boolean requires(InstalledPackage dependency)
    {
        return require != null && require.containsKey(dependency.name);
    }
    
    public String getFullName()
    {
        return String.format("%s (%s)", name, version);
    }
}