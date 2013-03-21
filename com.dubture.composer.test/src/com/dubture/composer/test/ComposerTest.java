/*
 * This file is part of the Composer Eclipse Plugin.

 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.dubture.composer.test;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
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
import com.dubture.composer.core.model.InstalledPackage;
import com.dubture.composer.core.model.NamespaceMapping;
import com.dubture.composer.core.visitor.AutoloadVisitor;

@SuppressWarnings("restriction")
public class ComposerTest extends ModifyingResourceTests
{
	private static final String[] TEST_NATURE = new String[] { PHPNature.ID, ComposerNature.NATURE_ID };
	
    public ComposerTest() {
		super("TestProject", "testproject");
	}
    
    public void testSerialization() {
    	
    	try {
    		String dir = System.getProperty("user.dir") + "/Resources/installed.json";
    		
    		System.err.println(dir);
    		FileInputStream input = new FileInputStream(new File(dir));
    		
            List<InstalledPackage> json = InstalledPackage.deserialize(input);
            assertEquals(21, json.size());
            
            InstalledPackage installed = json.get(20);
            
            assertEquals("jms/security-extra-bundle", installed.name);
            assertEquals("jms/security-extra-bundle (1.2.0)", installed.getFullName());
            assertEquals("JMS/SecurityExtraBundle", installed.targetDir);
            assertEquals(5, installed.require.size());
            assertEquals("1.1.*", installed.require.get("jms/metadata"));
            
            installed = json.get(5);
            
            assertEquals("monolog/monolog", installed.name);
            assertEquals(3, installed.suggest.size());
            assertEquals("Allow sending log messages to a MongoDB server", installed.suggest.get("ext-mongo"));
            assertEquals("1.0.*", installed.requireDev.get("mlehner/gelf-php"));
            
		} catch (Exception e) {
			fail();
		}
    }
    
    public void testNamespaceMap() {

		try {
			String dir = System.getProperty("user.dir") + "/Resources/autoload_namespaces.php";
			FileInputStream input = new FileInputStream(new File(dir));
			
			createScriptProject("P", TEST_NATURE,new String[] { "vendor" } );
			createFolder("P/vendor/composer");
			createFile("P/vendor/composer/autoload_namespaces.php", input);
			
			ISourceModule sourceModule = getSourceModule("P/vendor/composer/autoload_namespaces.php");
			assertNotNull(sourceModule);
			
			ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(sourceModule);
			AutoloadVisitor visitor = new AutoloadVisitor(sourceModule);
			moduleDeclaration.traverse(visitor);
			
			assertNotNull(visitor.getMappings());
			assertEquals(22, visitor.getMappings().size());
			NamespaceMapping mapping = visitor.getMappings().get(0);
			assertEquals("Twig_Extensions_", mapping.getNamespace());
			assertEquals("vendor/twig/extensions/lib/", mapping.getPath());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
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
