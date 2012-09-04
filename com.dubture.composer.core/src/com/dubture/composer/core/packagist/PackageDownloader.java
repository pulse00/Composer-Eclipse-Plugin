package com.dubture.composer.core.packagist;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IProgressMonitor;

import com.dubture.composer.core.model.PHPPackage;
import com.dubture.composer.core.visitor.ComposerFieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class PackageDownloader extends Downloader
{
    public PackageDownloader(String url)
    {
        super(url);
    }

    public PHPPackage getPackage(IProgressMonitor monitor) throws IOException
    {
        if (!url.endsWith(".json")) {
            url += ".json";
        }
        
        InputStream resource = downloadResource(monitor);

        InputStreamReader reader = new InputStreamReader(resource);
        JsonReader jsonReader = new JsonReader(reader);
        Gson gson = new GsonBuilder()
            .setFieldNamingStrategy(new ComposerFieldNamingStrategy())
            .create();

        Package pack = gson.fromJson(jsonReader, Package.class);
        return pack.phpPackage;

    }

    public static class Package
    {

        public PHPPackage phpPackage;
    }
}
