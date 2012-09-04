package com.dubture.composer.core.packagist;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.IProgressMonitor;

public class Downloader
{
    protected String url;

    public Downloader(String url)
    {
        this.setUrl(url);
    }

    public InputStream downloadResource(IProgressMonitor monitor) throws IOException
    {
        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod(getUrl());
        monitor.worked(40);
        client.executeMethod(get);

        return get.getResponseBodyAsStream();
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}