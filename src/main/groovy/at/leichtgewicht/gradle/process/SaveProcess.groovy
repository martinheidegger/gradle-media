package at.leichtgewicht.gradle.process

import groovy.lang.Closure;
import at.leichtgewicht.gradle.ImagingProcess
import at.leichtgewicht.gradle.process.format.JpegFormat;
import static at.leichtgewicht.gradle.util.LangUtil.configure

class SaveProcess extends AbstractImagingProcess implements ImagingProcess {
	def input
	def outputFormat
	Closure namePattern
	
	JpegFormat JPEG(Closure config) {
		return configure(new JpegFormat(), config)
	}
}
