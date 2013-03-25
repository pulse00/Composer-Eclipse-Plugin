/*
 * This file is part of the Composer Eclipse Plugin.

 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.dubture.composer.test;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.tests.model.ModifyingResourceTests;
import org.eclipse.php.internal.core.project.PHPNature;
import org.getcomposer.core.ComposerPackage;
import org.getcomposer.core.VersionedPackage;
import org.getcomposer.core.collection.Dependencies;
import org.getcomposer.packages.PharDownloader;

import com.dubture.composer.core.ComposerNature;
import com.dubture.composer.core.buildpath.BuildpathParser;
import com.dubture.composer.core.launch.ComposerLauncher;
import com.dubture.composer.core.launch.execution.ExecutionResponseAdapter;

@SuppressWarnings("restriction")
public class ComposerTest extends ModifyingResourceTests
{
	private static final String[] TEST_NATURE = new String[] { PHPNature.ID, ComposerNature.NATURE_ID };
	
    public ComposerTest() {
		super("TestProject", "testproject");
	}
    
    public void testBuildpathParser() {
    	try {
			IScriptProject p = createScriptProject("P", TEST_NATURE, null);
			IProject project = p.getProject();
			
			// create Composer json
			ComposerPackage composer = new ComposerPackage();
			Dependencies require = composer.getRequire();
			
			// add symfony dep
			VersionedPackage symfony = new VersionedPackage();
			symfony.setName("symfony/symfony");
			symfony.setVersion("2.2.0");
			require.add(symfony);
			
			// add symfony routing dep
			VersionedPackage routing = new VersionedPackage();
			routing.setName("symfony/routing");
			routing.setVersion("2.2.0");
			require.add(routing);
			
			String contents = composer.toJson();
			createFile("P/composer.json", contents);
			
			// download composer.phar
			PharDownloader downloader = new PharDownloader();
			InputStream stream = downloader.download();
			
			createFile("P/composer.phar", stream);
			
			// install dependencies
			final CountDownLatch counter = new CountDownLatch(1);
			ComposerLauncher launcher = ComposerLauncher.getLauncher(project);
			launcher.addResponseListener(new ExecutionResponseAdapter() {
				public void executionFailed(final String response, Exception e) {
//					Logger.logException(e);
					e.printStackTrace();
					fail();
					counter.countDown();
				}
				
				public void executionFinished(String response, int exitValue) {
					counter.countDown();
				}
			});
			launcher.launch("install");
			
			counter.await(30, TimeUnit.SECONDS);
			
			// test buildpath findings
			BuildpathParser parser = new BuildpathParser(project);
			
			List<ComposerPackage> pkgs = parser.getInstalledPackages();
			
			System.out.println("Installed packages: " + pkgs.size());
			
//			for (String path : parser.getPaths()) {
//				System.out.println("Found path: " + path);
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
    	
    }
}
