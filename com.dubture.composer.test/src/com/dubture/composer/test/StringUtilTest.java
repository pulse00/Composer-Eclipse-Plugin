package com.dubture.composer.test;

import junit.framework.TestCase;

import org.junit.Test;

import com.dubture.composer.core.util.StringUtil;

public class StringUtilTest extends TestCase {
	
	@Test
	public void testLinkReplacement() {
		
		String input = "dfoo bar ofoijsdf aha <http://foobar.com> soidjfoija aoosjd f aha <http://google.com> asdf oaisjdofjais df";
		String expected = "dfoo bar ofoijsdf aha <a>http://foobar.com</a> soidjfoija aoosjd f aha <a>http://google.com</a> asdf oaisjdofjais df";
		assertEquals(expected, StringUtil.replaceLinksInComposerMessage(input));
		

		input = "dfoo bar ofoijsdf aha soidjfoija aoosjd f aha  asdf oaisjdofjais df";
		assertEquals(input, StringUtil.replaceLinksInComposerMessage(input));

		input = "dfoo bar ofoijsdf aha <=4 soidjfoija aoosjd f aha <http://google.com> asdf oaisjdofjais df";
		expected = "dfoo bar ofoijsdf aha <=4 soidjfoija aoosjd f aha <a>http://google.com</a> asdf oaisjdofjais df";
		assertEquals(expected, StringUtil.replaceLinksInComposerMessage(input));
		
		
	}
}
