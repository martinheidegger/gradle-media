package at.leichtgewicht.gradle.process

import java.io.File;

import at.leichtgewicht.gradle.ImagingProcess;

class AbstractImagingProcess implements ImagingProcess {

	private File[] _input;
	
	@Override
	public void setInput(File[] input ) {
		_input = input;
	}
	
	@Override
	public File[] getInput() {
		return _input;
	}
	
	@Override
	public void execute() {
	}

}
