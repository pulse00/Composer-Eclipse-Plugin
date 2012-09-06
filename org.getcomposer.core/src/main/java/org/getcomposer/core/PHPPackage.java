/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.getcomposer.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Represents a composer package. The source can either be a composer.json file
 * or a json response from packagist.org.
 * 
 * See fromJson / fromPackagist for details.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 * 
 */
public class PHPPackage implements PackageInterface {

	public String name;
	public String type;
	public String description;
	public String homepage;
	public String url;
	public String fullPath;
	public Map<String, String> require;
	public Map<String, String> requireDev;
	public Autoload autoload;
	public String targetDir;
	public String version;
	public String versionNormalized;
	public License license;
	public String[] keywords;
	public Map<String, PHPPackage> versions;
	public Author[] authors;

	public String toString() {
		return name;
	}

	/**
	 * Deserializes a package from a composer.json file
	 * 
	 * @param input
	 * @return {@link PHPPackage} the deserialized package
	 * @throws FileNotFoundException
	 */
	public static PHPPackage fromJson(File input) throws FileNotFoundException {
		Gson gson = getBuilder();
		InputStream stream = new FileInputStream(input);
		InputStreamReader reader = new InputStreamReader(stream);
		PHPPackage pHPPackage = gson.fromJson(reader, PHPPackage.class);
		pHPPackage.fullPath = input.getAbsolutePath();

		return pHPPackage;
	}

	/**
	 * Deserializes a package from packagist.org, e.g.
	 * http://packagist.org/packages/react/react.json
	 * 
	 * @param input
	 * @return {@link PHPPackage} the deserialized package
	 * @throws FileNotFoundException
	 */
	public static PHPPackage fromPackagist(File input)
			throws FileNotFoundException {
		Gson gson = getBuilder();
		InputStream stream = new FileInputStream(input);
		InputStreamReader reader = new InputStreamReader(stream);
		PackagistPackage packagistPackage = gson.fromJson(reader,
				PackagistPackage.class);

		return packagistPackage.phpPackage;
	}

	/**
	 * Retrieve a Gson with the proper TypeAdapters and FieldNamingStrategy
	 * 
	 * @return {@link Gson}
	 */
	public static Gson getBuilder() {
		return new GsonBuilder()
				.registerTypeAdapter(License.class, new LicenseDeserializer())
				.setFieldNamingStrategy(new ComposerFieldNamingStrategy())
				.create();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getDefaultVersion()
	 */
	public String getDefaultVersion() {
		return versions.keySet().iterator().next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.getcomposer.core.PackageInterface#getPackageName(java.lang.String)
	 */
	public String getPackageName(String version) throws Exception {
		if (!versions.containsKey(version)) {
			throw new Exception("Invalid version " + version + " for package "
					+ name);
		}

		return String.format("%s:%s", name, version);
	}

	/**
	 * 
	 * Helper class for deserializing a packagist.org json object.
	 * 
	 * @author Robert Gruendler <r.gruendler@gmail.com>
	 * 
	 */
	public class PackagistPackage {

		public PHPPackage phpPackage;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getType()
	 */
	public String getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getHomepage()
	 */
	public String getHomepage() {
		return homepage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getUrl()
	 */
	public String getUrl() {
		return url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getFullPath()
	 */
	public String getFullPath() {
		return fullPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getRequire()
	 */
	public Map<String, String> getRequire() {
		return require;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getRequireDev()
	 */
	public Map<String, String> getRequireDev() {
		return requireDev;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getAutoload()
	 */
	public Autoload getAutoload() {
		return autoload;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getTargetDir()
	 */
	public String getTargetDir() {
		return targetDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getVersion()
	 */
	public String getVersion() {
		return version;
	}

	public String getVersionNormalized() {
		return versionNormalized;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getLicense()
	 */
	public License getLicense() {
		return license;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getKeywords()
	 */
	public String[] getKeywords() {
		return keywords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getVersions()
	 */
	public Map<String, PHPPackage> getVersions() {
		return versions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.getcomposer.core.PackageInterface#getAuthors()
	 */
	public Author[] getAuthors() {
		return authors;
	}
}
