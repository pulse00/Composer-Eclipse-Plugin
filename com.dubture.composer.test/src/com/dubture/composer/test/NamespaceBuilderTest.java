package com.dubture.composer.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.project.PHPNature;
import org.eclipse.php.internal.core.project.ProjectOptions;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.dubture.composer.core.ComposerNature;
import com.dubture.composer.core.facet.FacetManager;
import com.dubture.composer.core.model.ModelAccess;

@SuppressWarnings("restriction")
public class NamespaceBuilderTest extends TestCase {

	private IProject project;
	private IPath namespacePath;
	private Path srcPath;
	private IPath subPath;

	@Override
	protected void setUp() throws Exception {

		project = ResourcesPlugin.getWorkspace().getRoot().getProject("ComposerNamespaceTest");
		if (project.exists()) {
			return;
		}

		project.create(null);
		project.open(null);

		// configure nature
		IProjectDescription desc = project.getDescription();
		desc.setNatureIds(new String[] { PHPNature.ID });
		project.setDescription(desc, null);

		ProjectOptions.setPhpVersion(PHPVersion.PHP5_3, project);
		FacetManager.installFacets(project, PHPVersion.PHP5_3, new NullProgressMonitor());

		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);

		mockAutoload();
		createFolderStructure();

		ComposerCoreTests.waitForIndexer();
		ComposerCoreTests.waitForAutoBuild();
	}

	protected InputStream openResource(String path) throws IOException {
		File localFile = new File(path);
		if (localFile.exists()) {
			return new FileInputStream(localFile);
		}
		Bundle testBundle = ComposerCoreTests.getDefault().getBundle();
		URL url = testBundle.getEntry(path);
		return new BufferedInputStream(url.openStream());
	}

	protected void createFolderStructure() throws CoreException {

		srcPath = new Path("src");
		project.getFolder(srcPath).create(true, true, new NullProgressMonitor());

		namespacePath = srcPath.append("Foobar");
		project.getFolder(namespacePath).create(true, true, new NullProgressMonitor());

		subPath = namespacePath.append("Sub");
		project.getFolder(subPath).create(true, true, new NullProgressMonitor());
	}

	protected void mockAutoload() throws CoreException, IOException {
		NullProgressMonitor monitor = new NullProgressMonitor();
		IPath vendorDir = new Path("vendor");
		IPath composerDir = vendorDir.append("composer");
		project.getFolder(vendorDir).create(true, true, monitor);
		project.getFolder(composerDir).create(true, true, monitor);
		String autoload = "workspace/builder/vendor/composer/autoload_namespaces.php";
		InputStream inputStream = openResource(autoload);
		IPath autoloadNamespaces = composerDir.append("autoload_namespaces.php");
		IFile file = project.getFile(autoloadNamespaces);
		file.create(inputStream, true, monitor);
		assertTrue(file.exists());
	}

	@Test
	public void testTest() throws CoreException {

		assertNotNull(project);
		assertTrue(project.hasNature(ComposerNature.NATURE_ID));
		assertTrue(project.getFolder(new Path("vendor")).exists());
		IResource resource = project.getFolder(subPath);
		IPath path = ModelAccess.getInstance().resolve(resource);
		assertNotNull(path);
		assertEquals("Foobar/Sub", path.toString());
	}
}
