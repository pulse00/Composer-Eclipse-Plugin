/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.getcomposer.core.packagist;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.getcomposer.core.ComposerConstants;
import org.getcomposer.core.PHPPackage;
import org.getcomposer.core.PackageInterface;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class SearchResultDownloader extends Downloader {
	public SearchResultDownloader() {
		super(ComposerConstants.searchURL);
	}

	public List<PackageInterface> searchPackages(String query)
			throws IOException {
		List<PackageInterface> packages = new ArrayList<PackageInterface>();
		setUrl(String.format(ComposerConstants.searchURL, query));

		SearchResult result = loadPackages(getUrl());
		int limit = 5;
		int current = 0;

		// TODO: implement paging results
		while (result.next != null && result.next.length() > 0) {
			result = loadPackages(result.next);

			System.err.println("search has " + result.total + "  hits");
			if ((result.results != null && result.results.size() == 0)
					|| result.next == null || current++ > limit) {
				break;
			}
			packages.addAll(result.results);
		}

		return packages;
	}

	protected SearchResult loadPackages(String url) throws IOException {

		setUrl(url);
		System.err.println("downloading packages from " + url);
		InputStream resource = downloadResource();
		InputStreamReader reader = new InputStreamReader(resource);
		JsonReader jsonReader = new JsonReader(reader);
		Gson gson = new GsonBuilder().create();

		return gson.fromJson(jsonReader, SearchResult.class);
	}

	public static class SearchResult {
		public List<PHPPackage> results;
		public String next;
		public String total;
	}
}
