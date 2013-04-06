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

	public static IFacetedProject installFacets(IProject project, PHPVersion version, IProgressMonitor monitor) {
		try {
			
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			
			final IFacetedProject facetedProject = ProjectFacetsManager.create(project, true, monitor);
			
			if (facetedProject == null) {
				Logger.log(Logger.ERROR, "Unable to create faceted composer project.");
				return null;
			}
			
			IProjectFacet coreFacet = ProjectFacetsManager.getProjectFacet(PHPFacetsConstants.PHP_CORE_COMPONENT);
			IProjectFacet composerFacet = ProjectFacetsManager.getProjectFacet(ComposerFacetConstants.COMPOSER_COMPONENT);

			// install the fixed facets
			facetedProject.installProjectFacet(coreFacet.getDefaultVersion(), null, monitor);
			facetedProject.installProjectFacet(PHPFacets.convertToFacetVersion(version), null, monitor);
			facetedProject.installProjectFacet(composerFacet.getVersion(ComposerFacetConstants.COMPOSER_COMPONENT_VERSION_1), composerFacet, monitor);
			
			return facetedProject;
			
		} catch (CoreException ex) {
			Logger.logException(ex.getMessage(), ex);
		}
		
		return null;
	}
	
	public static void uninstallFacets(IProject project, IProgressMonitor monitor) {
		try {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			
			final IFacetedProject facetedProject = ProjectFacetsManager.create(project, true, monitor);
			
			IProjectFacet composerFacet = ProjectFacetsManager.getProjectFacet(ComposerFacetConstants.COMPOSER_COMPONENT);
			
			facetedProject.uninstallProjectFacet(composerFacet.getVersion(ComposerFacetConstants.COMPOSER_COMPONENT_VERSION_1), composerFacet, monitor);
		} catch (CoreException ex) {
			Logger.logException(ex.getMessage(), ex);
		}
	}
	
}
