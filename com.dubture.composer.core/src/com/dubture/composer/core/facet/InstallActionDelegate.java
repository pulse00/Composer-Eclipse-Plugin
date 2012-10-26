package com.dubture.composer.core.facet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.utils.ResourceUtil;
import org.eclipse.php.internal.core.buildpath.BuildPathUtils;
import org.eclipse.php.internal.core.project.PHPNature;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

import com.dubture.composer.core.ComposerBuildpathContainerInitializer;
import com.dubture.composer.core.ComposerNature;
import com.dubture.composer.core.ComposerPlugin;
import com.dubture.composer.core.model.ComposerBuildpathContainer;
import com.dubture.composer.core.util.BuildpathUtil;
import com.dubture.indexing.core.IndexingCorePlugin;

/**
 * Facet installation action delegate to add the composer nature to a PHP
 * project.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 * 
 */
@SuppressWarnings("restriction")
public class InstallActionDelegate implements IDelegate
{
    @Override
    public void execute(IProject project, IProjectFacetVersion version,
            Object object, IProgressMonitor progress) throws CoreException
    {
        if (!project.hasNature(PHPNature.ID)) {
            return;
        }

        progress.subTask("Installing composer nature");
        IScriptProject scriptProject = DLTKCore.create(project);

        // add the composer nature
        ResourceUtil.addNature(project, progress, ComposerNature.NATURE_ID);

        progress.subTask("Installing composer buildpath");

        // create composer buildpath entry

        if (ComposerPlugin.getDefault().isBuildpathContainerEnabled()) {
            IBuildpathContainer composerContainer = new ComposerBuildpathContainer(
                    new Path(ComposerBuildpathContainerInitializer.CONTAINER),
                    scriptProject);
            List<IBuildpathEntry> entries = new ArrayList<IBuildpathEntry>();
            entries.add(DLTKCore.newContainerEntry(composerContainer.getPath()));

            // add the composer buildpathentry to the project
            BuildPathUtils.addEntriesToBuildPath(scriptProject, entries);

            IndexingCorePlugin.getDefault().setupBuilder(project);
            BuildpathUtil.setupVendorBuildpath(scriptProject, progress);
            
        }
    }
}
