package at.leichtgewicht.gradle

import static org.junit.Assert.*;
import groovy.mock.interceptor.MockFor;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import at.leichtgewicht.gradle.task.ClearImagingTask;
import at.leichtgewicht.gradle.task.ProcessImagingTask;

class ImagingTest extends AbstractProjectTest {
	
	@Test
	void availableTasks() {
		assertTaskExists("processImages", ProcessImagingTask)
		assertTaskExists("clearImages", ClearImagingTask)
	}
	
	private assertTaskExists(String taskName, Class<Object> clazz) {
		def task = project.tasks.getByName(taskName)
		assertNotNull('The task "'+taskName+'" should have been created', task)
		if(!(clazz.isInstance(task))) {
			fail('The task is not of the appropriate class')
		}
	}
	
	private fullTest() {
		project.imaging {
			output = 'build/images'
			input = 'images'
			
			def largeThumbs = resize {
				width = 300
			}
			def smallThumbs = resize {
				images = largeThumbs
				width = 150
			}
			save {
				images = largeThumbs
				name = {File file -> "largeThumbs/{file.name}.jpg" }
			}
			saveAsGrid {
				images = smallThumbs
				maxWidth = 700
				maxHeight = 300
				name = {File file -> "smallThumbs/{file.name}.jpg" }
				format = JPEG({
					quality = 75
				})
				info = 'smallThumbs.json'
			}
		}
	}
} 