/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.getcomposer.core;

import java.lang.reflect.Field;

import com.google.gson.FieldNamingStrategy;

/**
 * Process non-standard java fields from json attributes.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 */
public class ComposerFieldNamingStrategy implements FieldNamingStrategy {

	public String translateName(Field field) {
		if (field.getName() == "psr_0") {
			return "psr-0";
		} else if (field.getName() == "targetDir") {
			return "target-dir";
		} else if (field.getName() == "requireDev") {
			return "require-dev";
		} else if (field.getName() == "phpPackage") {
			return "package";
		}

		return field.getName();
	}
}
