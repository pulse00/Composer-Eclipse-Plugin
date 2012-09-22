package com.dubture.composer.core.model;

import java.lang.reflect.Type;

import org.getcomposer.core.PHPPackage;
import org.getcomposer.core.PackageInterface;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class PackageDeserializer implements JsonDeserializer<PackageInterface>
{

    @Override
    public PackageInterface deserialize(JsonElement element, Type type,
            JsonDeserializationContext context) throws JsonParseException
    {
        return new GsonBuilder().create().fromJson(element, PHPPackage.class);
    }
}
