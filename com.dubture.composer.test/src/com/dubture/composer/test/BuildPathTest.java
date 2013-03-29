package com.dubture.composer.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.tests.model.AbstractModelTests;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.facet.PHPFacets;
import org.eclipse.php.internal.core.project.PHPNature;
import org.eclipse.php.internal.core.project.ProjectOptions;
import org.junit.Test;

import com.dubture.composer.core.ComposerNature;
import com.dubture.composer.core.buildpath.BuildPathParser;
import com.dubture.composer.core.facet.FacetManager;

@SuppressWarnings("restriction")
public class BuildPathTest extends AbstractModelTests {

	public BuildPathTest() {
		super(ComposerCoreTests.PLUGIN_ID, "BuildPath tests");
	}

	@Test
	public void testBuildpathParser() throws CoreException, IOException, InterruptedException {

		IScriptProject scriptProject = setUpScriptProject("testproject");

		assertNotNull(scriptProject);

		IProjectDescription desc = scriptProject.getProject().getDescription();
		desc.setNatureIds(new String[] { PHPNature.ID });
		scriptProject.getProject().setDescription(desc, null);

		ProjectOptions.setPhpVersion(PHPVersion.PHP5_3, scriptProject.getProject());

		PHPFacets.setFacetedVersion(scriptProject.getProject(), PHPVersion.PHP5_3);
		FacetManager.installFacets(scriptProject.getProject(), PHPVersion.PHP5_3, new NullProgressMonitor());

		scriptProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		scriptProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);

		ComposerCoreTests.waitForIndexer();
		ComposerCoreTests.waitForAutoBuild();

		IFile file = scriptProject.getProject().getFile("composer.json");
		assertNotNull(file);

		assertTrue(scriptProject.getProject().hasNature(PHPNature.ID));
		assertTrue(scriptProject.getProject().hasNature(ComposerNature.NATURE_ID));

		BuildPathParser parser = new BuildPathParser(scriptProject.getProject());
		List<String> paths = parser.getPaths();
		List<String> expected = new ArrayList<String>(Arrays.asList("/vendor/imagine/Imagine/lib/", "/vendor/symfony/yaml/Symfony/Component/Yaml/", "/testproject/src/", "/vendor/composer/"));
		assertThat(paths, is(expected));

		// let indexing threads shutdown to avoid SWT thread access errors
		Thread.sleep(2000);

	}
}
