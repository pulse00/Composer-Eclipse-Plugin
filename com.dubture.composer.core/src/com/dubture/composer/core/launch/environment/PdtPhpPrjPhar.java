package com.dubture.composer.core.launch.environment;

import org.apache.commons.exec.CommandLine;

public class PdtPhpPrjPhar extends PrjPharEnvironment implements Environment {
	
	private String php;
	
	public PdtPhpPrjPhar() {
		php = EnvironmentFinder.findPdtPhp();
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
