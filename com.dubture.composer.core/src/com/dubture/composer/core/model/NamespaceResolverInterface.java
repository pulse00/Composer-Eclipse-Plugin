/*
 * This file is part of the PHPPackage Eclipse Plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.dubture.composer.core.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 *
 */
public interface NamespaceResolverInterface
{
    /**
     * Resolve the namespace of a given resource.
     * 
     * Example input: IResource is a folder "/TestProject/lib/Acme/Demo"
     * 
     * The psr-0 autoload path in TestProject is "lib"
     * 
     * The return value will be an IPath with the segments "Acme/Demo".
     * 
     * @param the resource to resolve
     * @return the resolved namespace as an IPath
     */
    IPath resolve(IResource resource);
    
    /**
     * Resolve the source folder for given Namespace in a project
     * @param namespace The Namespace to be resolved
     * @return {@link IPath} | null
     */
    IPath reverseResolve(IProject project, String namespace);
}
