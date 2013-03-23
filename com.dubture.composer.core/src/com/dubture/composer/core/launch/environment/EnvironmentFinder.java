package com.dubture.composer.core.launch.environment;

import java.io.File;

import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.core.preferences.PHPexes;
import org.eclipse.php.internal.debug.core.xdebug.communication.XDebugCommunicationDaemon;
import org.eclipse.php.internal.debug.core.zend.communication.DebuggerCommunicationDaemon;

import com.dubture.composer.core.launch.execution.ComposerExecutor;
import com.dubture.composer.core.launch.execution.ExecutionResponseAdapter;
import com.dubture.composer.core.log.Logger;

@SuppressWarnings("restriction")
public class EnvironmentFinder {

	private static String composer = null;
	private static boolean composerSearched = false;
	
	private static String composerPhar = null;
	private static boolean composerPharSearched = false;
	
	private static String php = null;
	private static boolean phpSearched = false;
	
	private static String pdtPhp = null;
	private static boolean pdtPhpSearched = false;
	
	private static String[] paths = new String[]{"/usr/local/bin/", "/usr/bin/"};
	
	private static String result;
	
	/**
	 * Searches for a executable composer and returns the path or <code>null</code> if not
	 * found.
	 * 
	 * @return composer path or <code>null</code> if not found.
	 */
	public static String findComposer() {
		if (!composerSearched) {
			composer = find("composer", true);
			composerSearched = true;
		}

		return composer;
	}
	
	/**
	 * Searches for a composer.phar and returns the path or <code>null</code> if not
	 * found.
	 * 
	 * @return composer.phar path or <code>null</code> if not found.
	 */
	public static String findComposerPhar() {
		if (!composerPharSearched) {
			composerPhar = find("composer.phar");
			composerPharSearched = true;
		}

		return composerPhar;
	}
	
	
	/**
	 * Searches for a executable php and returns the path or <code>null</code> if not
	 * found.
	 * 
	 * @return php path or <code>null</code> if not found.
	 */
	public static String findPhp() {
		if (!phpSearched) {
			php = find("php", true);
			phpSearched = true;
		}

		return php;
	}
	
	public static String findPdtPhp() {
		if (!pdtPhpSearched) {
			
			String[] debuggers = new String[]{"",
					XDebugCommunicationDaemon.XDEBUG_DEBUGGER_ID,
					DebuggerCommunicationDaemon.ZEND_DEBUGGER_ID};
			
			PHPexeItem phpExe = null;
			
			for (String debugger : debuggers) {
				phpExe = PHPexes.getInstance().getDefaultItem(debugger);
				
				if (phpExe != null) {
					pdtPhp = phpExe.getExecutable().toString();
					break;
				}
			}
			
			pdtPhpSearched = true;
		}
		
		return pdtPhp;
	}
	
	private static String find(String file) {
		return find(file, false);
	}
	
	private static String find(String file, boolean executable) {
		String result = findInPath(file, executable);
		
		if (result == null) {
			result = exec("which \"" + file + "\"");
		}
		
		return result;
	}
	
	private static String findInPath(String file, boolean executable) {
		for (String location : paths) {
			File f = new File(location + file);
			Logger.debug("Searching for file " + f.getAbsolutePath());
			if (f.exists() && (executable ? f.canExecute() : true)) {
				Logger.debug("File exists and is executable: " + f.getAbsolutePath());
				return f.getAbsolutePath();
			}
		}
		return null;
	}

	private static String exec(String cmd) {
		result = null;
		ComposerExecutor executor = new ComposerExecutor();
		executor.addResponseListener(new ExecutionResponseAdapter() {
			public void executionFinished(String response, int exitValue) {
				result = response;
			}

			public void executionFailed(String response, Exception e) {
				result = null;
			}
		});

		try {
			executor.execute(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
