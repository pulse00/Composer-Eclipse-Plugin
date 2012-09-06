/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.getcomposer.test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import org.getcomposer.core.PHPPackage;
import org.junit.Test;


public class JsonParserTest extends TestCase {

	@Test
	@SuppressWarnings("rawtypes")
	public void testComposerJson() {

		try {
			
			PHPPackage phpPackage = PHPPackage.fromJson(loadFile("composer.json"));
			
			assertNotNull(phpPackage);
			assertEquals(3, phpPackage.authors.length);
			assertEquals(1, phpPackage.license.names.length);
			assertEquals(1,  phpPackage.keywords.length);
			assertEquals(3, phpPackage.require.size());
			
			Map<String, String> require = phpPackage.require;
			Iterator it = require.keySet().iterator();
			
			while(it.hasNext()) {
				String key = (String) it.next();
				String value = require.get(key);
				assertNotNull(key);
				assertNotNull(value);
			}
			
			assertNotNull(phpPackage.autoload);
			assertEquals("FOS\\UserBundle", phpPackage.autoload.getPsr_0().keySet().iterator().next());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testPackagistJson() {

		try {
			PHPPackage phpPackage = PHPPackage.fromPackagist(loadFile("packagist.json"));
			assertNotNull(phpPackage);
			
			assertEquals("friendsofsymfony/user-bundle", phpPackage.name);
			assertEquals("Symfony FOSUserBundle", phpPackage.description);
			assertNotNull(phpPackage.versions);
			assertTrue(phpPackage.versions.size() > 0);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} 
	}
	
	protected File loadFile(String name) throws URISyntaxException {

		ClassLoader loader = getClass().getClassLoader();
		URL resource = loader.getResource(name);
		return new File(resource.toURI());
	}
}
