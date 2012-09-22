package com.dubture.composer.core.model;

import java.lang.reflect.Type;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class PathDeserializer implements JsonDeserializer<IPath>
{
    @Override
    public IPath deserialize(JsonElement element, Type type,
            JsonDeserializationContext context) throws JsonParseException
    {
        return new GsonBuilder().create().fromJson(element, Path.class);
    }
}
