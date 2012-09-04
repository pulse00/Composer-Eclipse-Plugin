/*
 * This file is part of the PHPPackage Eclipse Plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.dubture.composer.core.visitor;

import java.lang.reflect.Field;

import com.google.gson.FieldNamingStrategy;

/**
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
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
        } else if (field.getName() == "requireDev") {
            return "require-dev";
        }
        
        return field.getName();
    }
}
