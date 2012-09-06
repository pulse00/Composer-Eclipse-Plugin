/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.getcomposer.core;

import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 */
public class Autoload {
	private Map<String, String> psr_0;

	public String getPSR0Path() {
		if (psr_0 == null) {
			return null;
		}
		Iterator<String> it = getPsr_0().keySet().iterator();
		while (it.hasNext()) {
			return psr_0.get(it.next());
		}
		return null;
	}

	public Map<String, String> getPsr_0() {
		return psr_0;
	}

	public void setPsr_0(Map<String, String> psr_0) {
		this.psr_0 = psr_0;
	}
}
