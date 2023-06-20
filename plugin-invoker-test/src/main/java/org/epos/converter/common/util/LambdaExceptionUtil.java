package org.epos.converter.common.util;

import org.epos.converter.common.util.LambdaExceptionUtil;

/**
 * @author patk
 *
 * Another implementation of a class also used by the plug-in client calling code. This will help 
 *  'plug-in proxy calling code <-> plug-in code' integration tests to test for potential
 *   classloading order issues.
 */
public class LambdaExceptionUtil {

	@Override
	public String toString() {
		return String.format(
				"PLUGIN IMPLEMENTATION OF %s",
				LambdaExceptionUtil.class.getName());
	}
	
}
