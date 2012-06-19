package at.leichtgewicht.gradle.process

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.Task;

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
	
	
	void setUp(Task parentTask) {}
	void execute(Task parentTask, ImageMeta meta) {}
	void tearDown(Task parentTask) {}
	String getName() { return null }
}
