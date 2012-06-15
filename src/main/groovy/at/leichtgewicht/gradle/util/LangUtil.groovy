package at.leichtgewicht.gradle.util

class LangUtil {
	public static def configure(object, Closure config) {
		def formerDelegate = config.delegate
		def formerStrategy = config.resolveStrategy
		config.delegate = object
		config.resolveStrategy = Closure.DELEGATE_FIRST
		config.run()
		config.delegate = formerDelegate
		config.resolveStrategy = formerStrategy
		return object
	}
	
}
