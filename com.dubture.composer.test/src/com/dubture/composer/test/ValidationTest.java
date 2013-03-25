package com.dubture.composer.test;

import org.junit.Test;

import com.dubture.composer.core.validation.ValidationUtils;

import junit.framework.TestCase;

public class ValidationTest extends TestCase {
	
	@Test
	public void testNamespaceValidation() {
		
		assertTrue(ValidationUtils.validateNamespace("Foo"));
		assertTrue(ValidationUtils.validateNamespace("Foo\\Bar"));
		assertTrue(ValidationUtils.validateNamespace("Foo\\"));
		assertTrue(ValidationUtils.validateNamespace("\\Foo\\Bar"));
		assertTrue(ValidationUtils.validateNamespace("foo\\baR_aha"));
		assertTrue(ValidationUtils.validateNamespace("Foo_Bar\\Something"));
		
		assertFalse(ValidationUtils.validateNamespace("Fo baro"));
		assertFalse(ValidationUtils.validateNamespace("Fo2Bar"));
		assertFalse(ValidationUtils.validateNamespace("Fo.Bar"));
		assertFalse(ValidationUtils.validateNamespace("Fo  Bar"));
		assertFalse(ValidationUtils.validateNamespace("Fo--Bar\\Aha"));
		
	}
}
