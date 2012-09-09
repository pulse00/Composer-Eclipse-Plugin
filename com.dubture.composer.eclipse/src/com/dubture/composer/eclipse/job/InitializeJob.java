package com.dubture.composer.eclipse.job;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.getcomposer.core.Author;
import org.getcomposer.core.PHPPackage;

import com.dubture.composer.eclipse.log.Logger;

public class InitializeJob extends ComposerJob
{
    private final PHPPackage phpPackage;
    
    public InitializeJob(IProject project, PHPPackage phpPackage)
    {
        super("Installing composer dependencies...");
        
        Assert.isNotNull(phpPackage);
        Assert.isNotNull(project);
        
        this.phpPackage = phpPackage;
        composer = project.findMember("composer.phar").getLocation().toOSString();
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        try {
            monitor.beginTask("Running composer.phar init", 2);
            monitor.worked(1);
            
            List<String> args = new ArrayList<String>();
            
            args.add("--name='" + phpPackage.name + "'");
            args.add(String.format("--description='%s'", phpPackage.description));
            
            if (phpPackage.authors != null && phpPackage.authors.length > 0) {
                Author author = phpPackage.authors[0];
                args.add(String.format("--author=%s", author.getInitString()));
            }
            
            args.add("--minimum-stability='" + phpPackage.minimumStability + "'");
            args.add("--no-interaction");
            
            
            execute("init", args.toArray(new String[args.size()]));
            
            monitor.worked(2);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.logException(e);
            return ERROR_STATUS;
        } finally {
            monitor.done();
        }
        
        return Status.OK_STATUS;
    }
}
