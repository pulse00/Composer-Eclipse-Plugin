package org.getcomposer.core.packagist;

import org.getcomposer.core.ComposerConstants;

public class PharDownloader extends Downloader {

	public PharDownloader() {
		super(ComposerConstants.pharURL);
	}
}
