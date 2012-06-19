package at.leichtgewicht.gradle.process

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.Task;

import at.leichtgewicht.gradle.MediaMeta;
import at.leichtgewicht.gradle.MediaProcess;

class AbstractMediaProcess implements MediaProcess {

	private File[] _input;
	
	public void setInput(File[] input ) {
		_input = input;
	}
	
	public File[] getInput() {
		return _input;
	}
	
	
	void setUp(Task parentTask) {}
	void execute(Task parentTask, MediaMeta meta) {}
	void tearDown(Task parentTask) {}
	String getName() { return null }
}
