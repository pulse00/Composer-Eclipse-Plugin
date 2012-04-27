/*
 * This file is part of the Composer Eclipse Plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.dubture.composer.core.builder;

import java.lang.reflect.Field;

import com.google.gson.FieldNamingStrategy;

/**
 *
 */
public class ComposerFieldNamingStrategy implements FieldNamingStrategy
{

    @Override
    public String translateName(Field field)
    {
        if (field.getName() == "psr_0") {
            return "psr-0";
        } else if (field.getName() == "targetDir") {
            
            return "target-dir";
        }
        return field.getName();
    }
}
