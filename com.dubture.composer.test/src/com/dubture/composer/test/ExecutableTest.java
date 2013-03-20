package com.dubture.composer.test;

import org.junit.Test;

import com.dubture.composer.core.launch.environment.EnvironmentFinder;

import junit.framework.TestCase;

public class ExecutableTest extends TestCase {

	@Test
	public void testEnvironmentFinder() {
		System.out.println("composer found at: " + EnvironmentFinder.findComposer());
		System.out.println("composer.phar found at: " + EnvironmentFinder.findComposerPhar());
		System.out.println("php found at: " + EnvironmentFinder.findPhp());
//		System.out.println("pdt php found at: " + EnvironmentFinder.findPdtPhp());
		
//		CommandLine cmdLine = CommandLine.parse("which composer");
//		DefaultExecutor executor = new DefaultExecutor();
//		executor.setExitValue(1);
//		try {
//			int exitValue = executor.execute(cmdLine);
//			System.out.println("Exit Value: " + exitValue);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
