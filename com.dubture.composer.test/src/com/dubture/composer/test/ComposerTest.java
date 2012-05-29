/*
 * This file is part of the Composer Eclipse Plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.dubture.composer.test;
import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import com.dubture.composer.core.model.Composer;
import com.dubture.composer.core.model.ModelAccess;

/**
 *
 */
public class ComposerTest extends TestCase
{
    
    private IWorkspace workspace;
    private IFile file;
    private NullProgressMonitor monitor;
    private IProject project;

    @Override
    protected void setUp() throws Exception
    {
        workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        project = root.getProject("TestProject");
        monitor = new NullProgressMonitor();
        
        if (project.exists()) {
            project.delete(false, monitor);
        }
        
        project.create(monitor);
        project.open(monitor);
        
        file = project.getFile("composer.json");
        
        String json = "{\n" + 
        		"    \"autoload\": {\n" + 
        		"        \"psr-0\": {\n" + 
        		"         \"\": \"lib/\"\n" + 
        		"        }\n" + 
        		"    }\n" + 
        		"}";
        
        ByteArrayInputStream input = new ByteArrayInputStream(json.getBytes());
        file.create(input, false, monitor);
        
        IFolder folder = project.getFolder("lib");
        folder.create(true, false, monitor);
        
        folder = project.getFolder("lib/Acme");
        folder.create(true, false, monitor);
        
    }
    
    @Test
    public void testNamespaceResolver()
    {
        try {
            
            IFolder folder = project.getFolder("lib/Acme/Demo");
            folder.create(true, false, monitor);
            
            Composer composer = Composer.fromJson(file);
            ModelAccess model = ModelAccess.getInstance();
            
            
        } catch (CoreException e) {
            e.printStackTrace();
            fail();
        }
    }
}
