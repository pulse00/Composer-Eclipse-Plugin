/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.dubture.composer.core.launch;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;


public interface IPHPLauncher {

	void launch(String scriptPath, String[] arguments, ILaunchResponseHandler handler) throws IOException, InterruptedException, CoreException;
	
	void launchAsync(String scriptPath, String[] arguments, ILaunchResponseHandler handler) throws IOException, InterruptedException, CoreException;
}
