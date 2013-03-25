package com.dubture.composer.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ComposerTest.class, ExecutableTest.class, NamespaceBuilderTest.class, ValidationTest.class })
public class AllTests {

}
