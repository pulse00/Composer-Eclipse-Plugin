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
    
    @Override
    public String toString()
    {
        if (psr_0 != null) {
            Iterator<String> it = psr_0.keySet().iterator();
            while(it.hasNext()) {
                String key = it.next();
                String val = psr_0.get(key);
                return key + " => " + val;
            }
        }
        
        return null;
    }
}
