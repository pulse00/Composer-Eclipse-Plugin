package com.dubture.composer.core.build;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.IBuildParticipantFactory;

import com.dubture.composer.core.ComposerNature;

public class BuildParticipantFactory implements IBuildParticipantFactory
{

    @Override
    public IBuildParticipant createBuildParticipant(IScriptProject project)
            throws CoreException
    {
        if (project.getProject().hasNature(ComposerNature.NATURE_ID) == false) {
            return null;
        }
        
        return new ComposerBuildParticipant();
    }

}
