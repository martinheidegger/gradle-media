package at.leichtgewicht.gradle

import java.awt.image.BufferedImage;

import org.gradle.api.Project;
import org.gradle.api.Task;

interface ImagingProcess {
	def input;
	
	void setUp(Task parentTask)
	void execute(Task parentTask, ImageMeta meta)
	void tearDown(Task parentTask)
	String getName()
}
