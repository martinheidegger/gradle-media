package at.leichtgewicht.gradle.process

import java.io.File;

import at.leichtgewicht.gradle.ImageMeta;
import at.leichtgewicht.gradle.ImagingProcess;

class AbstractImagingProcess implements ImagingProcess {

	private File[] _input;
	
	public void setInput(File[] input ) {
		_input = input;
	}
	
	public File[] getInput() {
		return _input;
	}
	
	void setUp() {
		
	}
	
	void execute(ImageMeta meta) {
		
	}
	
	void tearDown() {
		
	}

}
