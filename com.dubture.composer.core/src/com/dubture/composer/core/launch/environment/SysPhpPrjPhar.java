package com.dubture.composer.core.launch.environment;

import java.io.File;

import org.apache.commons.exec.CommandLine;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.php.internal.debug.core.phpIni.PHPINIUtil;
import org.pdtextensions.core.launch.environment.PrjPharEnvironment;

public class SysPhpPrjPhar extends PrjPharEnvironment {

	private String php;
	
	public SysPhpPrjPhar(String executable) {
		php = executable;
	}

	public boolean isAvailable() {
		return php != null;
	}

	public CommandLine getCommand() {
		CommandLine cmd = new CommandLine(php.trim());
		
		// specify php.ini location
		File iniFile = PHPINIUtil.findPHPIni(php);
		if (iniFile != null) {
			cmd.addArgument("-c");
			cmd.addArgument(iniFile.getAbsolutePath());
		}
		
		// specify composer.phar location
		cmd.addArgument(phar.trim());
		return cmd;
	}
	
	@Override
	protected IResource getScript(IProject project) {
		return project.findMember("composer.phar");
	}
}
