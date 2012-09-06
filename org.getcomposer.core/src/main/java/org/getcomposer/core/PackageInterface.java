package org.getcomposer.core;

import java.util.Map;

public interface PackageInterface {

	/**
	 * Returns the first available version by default. TODO: check the specs
	 * about how to resolve this ...
	 * 
	 * @return String the default version
	 */
	public abstract String getDefaultVersion();

	/**
	 * 
	 * Returns the package name suitable for passing it to
	 * "composer.phar require"
	 * 
	 * @param version
	 * @return String the package/version combination
	 * @throws Exception
	 */
	public abstract String getPackageName(String version) throws Exception;

	public abstract String getName();

	public abstract String getType();

	public abstract String getDescription();

	public abstract String getHomepage();

	public abstract String getUrl();

	public abstract String getFullPath();

	public abstract Map<String, String> getRequire();

	public abstract Map<String, String> getRequireDev();

	public abstract Autoload getAutoload();

	public abstract String getTargetDir();

	public abstract String getVersion();

	public abstract License getLicense();

	public abstract String[] getKeywords();

	public abstract Map<String, PHPPackage> getVersions();

	public abstract Author[] getAuthors();

}