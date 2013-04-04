package com.dubture.composer.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ NamespaceResolverTest.class, ValidationTest.class,
	StringUtilTest.class, BuildPathTest.class})
public class AllTests {

}
