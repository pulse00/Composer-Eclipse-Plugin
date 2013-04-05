package com.dubture.composer.core.facet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.facet.PHPFacets;
import org.eclipse.php.internal.core.facet.PHPFacetsConstants;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

import com.dubture.composer.core.log.Logger;

@SuppressWarnings("restriction")
public class FacetManager {

	public static void installFacets(IProject project, PHPVersion version, IProgressMonitor monitor) {
		try {
			
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			
			final IFacetedProject facetedProject = ProjectFacetsManager.create(project, true, monitor);
			
//			final Set<IProjectFacet> fixedFacets = new HashSet<IProjectFacet>();
			IProjectFacet coreFacet = ProjectFacetsManager.getProjectFacet(PHPFacetsConstants.PHP_CORE_COMPONENT);
//			fixedFacets.add(coreFacet);
//			
			IProjectFacet composerFacet = ProjectFacetsManager.getProjectFacet(ComposerFacetConstants.COMPOSER_COMPONENT);
//			fixedFacets.add(composerFacet);
//			
//			IProjectFacet phpFacet = ProjectFacetsManager.getProjectFacet(PHPFacetsConstants.PHP_COMPONENT);
//			fixedFacets.add(phpFacet);
//			facetedProject.setFixedProjectFacets(fixedFacets);

			// install the fixed facets
			facetedProject.installProjectFacet(coreFacet.getDefaultVersion(), null, monitor);
			facetedProject.installProjectFacet(PHPFacets.convertToFacetVersion(version), null, monitor);
			facetedProject.installProjectFacet(composerFacet.getVersion(ComposerFacetConstants.COMPOSER_COMPONENT_VERSION_1), composerFacet, monitor);
			
		} catch (CoreException ex) {
			Logger.logException(ex.getMessage(), ex);
		}
	}
	
}
