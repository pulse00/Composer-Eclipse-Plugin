/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.getcomposer.core;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * 
 * @see http://getcomposer.org/doc/04-schema.md#license
 * @author Robert Gruendler <r.gruendler@gmail.com>
 * 
 */
public class LicenseDeserializer implements JsonDeserializer<License> {

	public License deserialize(JsonElement element, Type type,
			JsonDeserializationContext context) throws JsonParseException {

		License license = new License();

		if (type instanceof GenericArrayType) {

			JsonArray jsonArray = element.getAsJsonArray();
			String[] licenses = new String[jsonArray.size()];
			int i = 0;

			for (JsonElement child : jsonArray) {
				licenses[i++] = child.getAsString();
			}
			license.names = licenses;
		} else {
			license.names = new String[] { element.getAsString() };
		}

		return license;
	}
}
