package com.dubture.composer.ui.job;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.pdtextensions.core.launch.ScriptLauncher;
import org.pdtextensions.core.launch.execution.ExecutionResponseAdapter;
import org.pdtextensions.core.launch.execution.ExecutionResponseListener;

import com.dubture.getcomposer.packages.PharDownloader;

@SuppressWarnings("restriction")
public class CreateProjectJob extends ComposerJob {

	private final String projectName;
	private final String packageName;
	private IWorkspace workspace;
	private IPath composerPath;
	private IPath path;
	
	public CreateProjectJob(String projectName, String packageName) {
		super("create-project");
		this.projectName = projectName;
		this.packageName = packageName;
		
		workspace = ResourcesPlugin.getWorkspace();
		path = workspace.getRoot().getLocation();
		DummyProject project = new DummyProject(path);
		setProject(project);
		
		try {
			composerPath = workspace.getRoot().getLocation().append("composer.phar");
			File file = composerPath.toFile();
			
			boolean existed = false;
			if (!file.exists()) {
				existed = true;
				PharDownloader downloader = new PharDownloader();
				InputStream phar = downloader.download();
				InputStream resource = downloader.download();
				FileUtils.copyInputStreamToFile(resource, file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void launch(ScriptLauncher launcher) throws ExecuteException, IOException, InterruptedException {

		launcher.addResponseListener(new ExecutionResponseListener() {
			
			@Override
			public void executionStarted() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void executionMessage(String message) {
				System.err.println(message);
			}
			
			@Override
			public void executionFinished(String response, int exitValue) {
				System.err.println(response);
				
			}
			
			@Override
			public void executionFailed(String response, Exception exception) {
				System.err.println(response);
				
			}
			
			@Override
			public void executionError(String message) {
				System.err.println(message);
			}
			
			@Override
			public void executionAboutToStart() {
				// TODO Auto-generated method stub
				
			}
		});
		
		launcher.launch("create-project", new String[]{packageName, projectName, "2.2.1"});
		
		// TODO: remove composer.phar
	}
	
	protected class DummyProject extends Project {

		public DummyProject(IPath path) {
			this(path, (Workspace) ResourcesPlugin.getWorkspace());
		}
		
		protected DummyProject(IPath path, Workspace container) {
			super(path, container);
		}
		
		@Override
		public IResource findMember(String path) {
			return new DummyResource();
		}
		
		@Override
		public IPath getLocation() {
			return path;
		}
	}
	
	public class DummyResource extends org.eclipse.core.internal.resources.File {

		protected DummyResource() {
			super(new Path("/"), null);
		}

		public IPath getFullPath() {
			return new Path("/dummy/composer.phar");
		}
	}
}
