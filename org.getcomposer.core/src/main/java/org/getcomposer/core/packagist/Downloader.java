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
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.getcomposer.core.ProgressListener;


/**
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 * 
 */
public class Downloader {
	protected String url;

	protected List<ProgressListener> listeners;

	public Downloader(String url) {
		this.setUrl(url);
		listeners = new ArrayList<ProgressListener>();
	}

	public void addProgressListener(ProgressListener listener) {

		listeners.add(listener);

	}

	public void removeProgressListener(ProgressListener listener) {

		listeners.remove(listener);
	}

	public InputStream downloadResource() throws IOException {

		for (ProgressListener listener : listeners) {
			listener.setTotalWork(4);
		}

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(getUrl());
		HttpResponse response = client.execute(get);

		for (ProgressListener listener : listeners) {
			listener.progressChanged(1);
		}

		try {
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();

			for (ProgressListener listener : listeners) {
				listener.progressChanged(1);
			}

			for (ProgressListener listener : listeners) {
				listener.worked();
			}

			return content;

		} catch (Exception e) {
			// TODO: log exception
//			if (get != null) {
//				get.releaseConnection();
//			}
		} finally {
			for (ProgressListener listener : listeners) {
				listener.worked();
			}
		}

		throw new IOException("Error downloading resource");
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
