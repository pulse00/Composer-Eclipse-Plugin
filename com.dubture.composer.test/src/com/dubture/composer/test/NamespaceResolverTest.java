package com.dubture.composer.test;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.tests.model.AbstractModelTests;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.facet.PHPFacets;
import org.eclipse.php.internal.core.project.PHPNature;
import org.eclipse.php.internal.core.project.ProjectOptions;
import org.junit.Test;

import com.dubture.composer.core.ComposerNature;
import com.dubture.composer.core.facet.FacetManager;
import com.dubture.composer.core.model.ModelAccess;

@SuppressWarnings("restriction")
public class NamespaceResolverTest extends AbstractModelTests {

	public NamespaceResolverTest() {
		super(ComposerCoreTestPlugin.PLUGIN_ID, "Namespace Resolver tests");
	}

	@Test
	public void testNamespaceResolver() throws CoreException, IOException {

		IScriptProject scriptProject = setUpScriptProject("testproject1");

		assertNotNull(scriptProject);

		IProjectDescription desc = scriptProject.getProject().getDescription();
		desc.setNatureIds(new String[] { PHPNature.ID });
		scriptProject.getProject().setDescription(desc, null);

		ProjectOptions.setPhpVersion(PHPVersion.PHP5_3, scriptProject.getProject());

		PHPFacets.setFacetedVersion(scriptProject.getProject(), PHPVersion.PHP5_3);
		FacetManager.installFacets(scriptProject.getProject(), PHPVersion.PHP5_3, new NullProgressMonitor());

		scriptProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		scriptProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);

		ComposerCoreTestPlugin.waitForIndexer();
		ComposerCoreTestPlugin.waitForAutoBuild();

		IFile file = scriptProject.getProject().getFile("composer.json");
		assertNotNull(file);

		assertTrue(scriptProject.getProject().hasNature(PHPNature.ID));
		assertTrue(scriptProject.getProject().hasNature(ComposerNature.NATURE_ID));

		IResource resource = scriptProject.getProject().getFolder(new Path("src/Foobar/Sub"));
		IPath path = ModelAccess.getInstance().resolve(resource);
		assertNotNull(path);
		assertEquals("Foobar/Sub", path.toString());
	}
}
