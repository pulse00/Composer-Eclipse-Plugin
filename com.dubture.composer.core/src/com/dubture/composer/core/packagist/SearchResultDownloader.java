package com.dubture.composer.core.packagist;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.internal.core.util.LRUCache;
import org.pex.core.model.InstallableItem;

import com.dubture.composer.core.ComposerConstants;
import com.dubture.composer.core.model.PHPPackage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

@SuppressWarnings("restriction")
public class PackageDownloader extends Downloader
{
    protected LRUCache searchCache;

    public PackageDownloader()
    {
        super(ComposerConstants.searchURL);
        searchCache = new LRUCache();
    }

    @SuppressWarnings("unchecked")
    public List<? extends InstallableItem> searchPackages(String query,
            IProgressMonitor monitor) throws IOException
    {
        List<PHPPackage> packages = (List<PHPPackage>) searchCache.get(query);

        if (packages != null) {
            return packages;
        }

        packages = new ArrayList<PHPPackage>();
        setUrl(String.format(ComposerConstants.searchURL, query));
        
        SearchResult result = loadPackages(getUrl(), monitor);
        packages =  result.results;
        
        int limit = 5;
        int current = 0;
        
        //TODO: implement paging results
        while(result.next != null && result.next.length() > 0) {
            
            result = loadPackages(result.next, monitor);
            
            if ( (result.results != null && result.results.size() == 0) || result.next == null || current++ > limit) {
                break;
            }
            
            packages.addAll(result.results);
        }

        return (List<InstallableItem>) searchCache.put(query, packages);
    }
    
    protected SearchResult loadPackages(String url, IProgressMonitor monitor) throws IOException {

        setUrl(url);
        InputStream resource = downloadResource(monitor);
        InputStreamReader reader = new InputStreamReader(resource);
        JsonReader jsonReader = new JsonReader(reader);
        Gson gson = new GsonBuilder().create();
        
        return gson.fromJson(jsonReader, SearchResult.class);
    }

    public static class SearchResult
    {
        public List<PHPPackage> results;
        public String next;
        public String total;
    }
}
