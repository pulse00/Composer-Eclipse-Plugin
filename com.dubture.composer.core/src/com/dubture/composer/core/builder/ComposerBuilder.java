/*
 * This file is part of the Composer Eclipse Plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.dubture.composer.core.builder;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.dubture.composer.core.model.Composer;
import com.dubture.composer.core.model.ModelAccess;
import com.dubture.indexing.core.index.ReferenceInfo;
import com.dubture.indexing.core.search.SearchEngine;

public class ComposerBuilder extends IncrementalProjectBuilder {

    class ComposerDeltaVisitor implements IResourceDeltaVisitor {
        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
         */
        public boolean visit(IResourceDelta delta) throws CoreException {
            
            IResource resource = delta.getResource();
            switch (delta.getKind()) {
                case IResourceDelta.ADDED:
                    // handle added resource
                    parseJSON(resource);
                    break;
                case IResourceDelta.REMOVED:
                    // handle removed resource
                    break;
                case IResourceDelta.CHANGED:
                    // handle changed resource
                    parseJSON(resource);
                    break;
            }
            //return true to continue visiting children.
            return true;
        }
    }

    class ComposerResourceVisitor implements IResourceVisitor {
        public boolean visit(IResource resource) {
            parseJSON(resource);
            //return true to continue visiting children.
            return true;
        }
    }

    public static final String BUILDER_ID = "com.dubture.composer.core.composerBuilder";

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
     *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
            throws CoreException {
        
        if (kind == FULL_BUILD) {
            fullBuild(monitor);
        } else {
            IResourceDelta delta = getDelta(getProject());
            if (delta == null) {
                fullBuild(monitor);
            } else {
                incrementalBuild(delta, monitor);
            }
        }
        return null;
    }

    void parseJSON(IResource resource) {

        if (resource instanceof IFile) {
            try {
                
                SearchEngine engine = SearchEngine.getInstance();
                List<ReferenceInfo> references = engine.findReferences((IFile) resource);
                
                System.err.println(resource.getFullPath().removeLastSegments(1).toString() + " " + " INDEX HAS " + references.size()+ " references");
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
        }
                
        if (resource instanceof IFile && resource.getName().equals("composer.json")) {

            try {
                Composer composer = Composer.fromJson((IFile) resource);
                ModelAccess.getInstance().add(composer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void fullBuild(final IProgressMonitor monitor)
            throws CoreException {
        try {
            getProject().accept(new ComposerResourceVisitor());
        } catch (CoreException e) {
        }
    }

    protected void incrementalBuild(IResourceDelta delta,
            IProgressMonitor monitor) throws CoreException {
        // the visitor does the work.
        delta.accept(new ComposerDeltaVisitor());
    }
}
