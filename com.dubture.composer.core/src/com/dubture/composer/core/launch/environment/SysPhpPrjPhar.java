package com.dubture.composer.core.launch.environment;

import org.apache.commons.exec.CommandLine;

public class SysPhpPrjPhar extends PrjPharEnvironment implements Environment {

	private String php;
	
	public SysPhpPrjPhar() {
		php = EnvironmentFinder.findPhp();
	}
	public SysPhpPrjPhar(String executable) {
		php = executable;
	}

	public boolean isAvailable() {
		return php != null;
	}

	public CommandLine getCommand() {
		CommandLine cmd = new CommandLine(php.trim());
		cmd.addArgument(phar.trim());
		
		return cmd;
	}
}
