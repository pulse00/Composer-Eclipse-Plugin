package com.dubture.composer.core.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
	
	protected static final Pattern NS_PATTERN = Pattern.compile("^[a-zA-Z_\\\\]+$");
	
	public static boolean validateNamespace(String namespace) {
		Matcher matcher = NS_PATTERN.matcher(namespace);
		return matcher.matches();
	}
}
