package at.leichtgewicht.gradle.util

import java.io.File;

class FileUtil {
	
	static String getFileSuffix(File file) {
		int pos = file.name.lastIndexOf('.')
		if (pos > 0 && pos < file.name.length() - 1) return file.name.substring(pos + 1)
	}
	
	static final boolean isFile(File file) {
		return file != null && file.exists() && !file.isDirectory()
	}
}
