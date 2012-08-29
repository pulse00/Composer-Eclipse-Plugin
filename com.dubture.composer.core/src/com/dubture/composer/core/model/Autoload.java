/*
 *  * This file is part of the Composer Eclipse Plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.dubture.composer.core.model;

import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class Autoload
{
    private Map<String, String> psr_0;
    
    public String getPSR0Path()
    {
        if (psr_0 == null) {
            return null;
        }
        Iterator<String> it = getPsr_0().keySet().iterator();
        while(it.hasNext()) {
            return psr_0.get(it.next());
        }
        return null;
    }

    public Map<String, String> getPsr_0()
    {
        return psr_0;
    }

    public void setPsr_0(Map<String, String> psr_0)
    {
        this.psr_0 = psr_0;
    }
}
