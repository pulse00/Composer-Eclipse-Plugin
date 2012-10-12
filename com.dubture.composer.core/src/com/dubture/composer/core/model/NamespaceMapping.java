package com.dubture.composer.core.model;


public class NamespaceMapping
{
    private String namespace;
    private String path;

    public NamespaceMapping()  {
        
    }
    
    public NamespaceMapping(String namespace, String path) {
        this.setNamespace(namespace);
        this.setPath(path);
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getNamespace()
    {
        return namespace;
    }

    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }
}
