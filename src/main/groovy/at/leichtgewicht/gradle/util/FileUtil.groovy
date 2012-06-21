package at.leichtgewicht.gradle.util

import groovy.lang.Closure;

import java.io.File;

import org.gradle.api.Task;
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.collections.SimpleFileCollection;

class FileUtil {
	
	static String getFileSuffix(File file) {
		int pos = file.name.lastIndexOf('.')
		if (pos > 0 && pos < file.name.length() - 1) return file.name.substring(pos + 1)
	}
	
	static String getFilePrefix(File file) {
		int pos = file.name.lastIndexOf('.')
		if (pos > 0 && pos < file.name.length() - 1) return file.name.substring(0, pos)
		else return file.name
	}
	
	static final boolean isFile(File file) {
		return file != null && file.exists() && !file.isDirectory()
	}
	
	static final boolean createParentFolders(File file) {
		return file.parentFile.mkdirs();
	}
	
	static FileCollection resolveFiles(Task task, input, parameter) {
		while( input != null ) {
			if( input instanceof Closure ) {
				input = executeClosure(input, parameter)
			} else if( input instanceof FileCollection ) {
				return input
			} else if( input instanceof File) {
				return new SimpleFileCollection(input)
			} else {
				return task.project.files(input)
			}
		}
	}
	
	static File resolveFile(Task task, input, parameter) {
		while( input != null ) {
			if( input instanceof Closure ) {
				input = executeClosure(input, parameter)
			} else if( input instanceof FileCollection ) {
				FileCollection collection = input
				input = collection.iterator().next()
			} else if( input instanceof File) {
				return input
			} else {
				return task.project.file(input)
			}
		}
	}
	
	static def executeClosure(Closure closure, parameters) {
		Map<Class, Integer> posMap = new HashMap<Class, Integer>();
		List<Object> arguments = new ArrayList<Object>();
		closure.parameterTypes.each { Class type ->
			int startIndex;
			if( posMap.containsKey(type) ) {
				startIndex = posMap.get(type)
			} else {
				startIndex = 0
			}
			
			for(int i = startIndex; i< parameters.size(); ++i) {
				def parameter = parameters[i]
				if( typeMatches(type, parameter) ) {
					posMap.put(type, i+1)
					arguments.push(parameter)
					return
				}
			}
		}
		if( arguments.size() != closure.parameterTypes.size() ) {
			throw new RuntimeException("Name resolving uses unacceptable parameters ...")
		} else {
			closure.call(arguments)
		}
	}
	
	public static final boolean typeMatches(Class type, Object object) {
		return normalize(type).isAssignableFrom(normalize(object.class))
	}
	
	private static Class normalize(Class type) {
		if( type.isPrimitive() ) {
			switch( type.name ) {
				case 'bool':
					return Boolean.class
				case 'int':
					return Integer.class
				case 'float':
					return Float.class
			}
		}
		return type
	}
	
	static final String getRelativePath(File file) {
		return new File(System.getProperty("user.dir")).toURI().relativize(file.toURI()).getPath()
	}
}
