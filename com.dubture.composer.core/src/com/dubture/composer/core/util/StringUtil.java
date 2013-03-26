package com.dubture.composer.core.util;

public class StringUtil {

	public static final String LINK_PATTERN = "(?i)(<http)(.+?)(>)"; 
	
	public static String replaceLinksInComposerMessage(String message) {
		return message.replaceAll(LINK_PATTERN,  "<a>http$2</a>");
	}
}
