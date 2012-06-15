package at.leichtgewicht.gradle

import static org.junit.Assert.*;

import org.gradle.api.Project;
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
	
	@Test
	void fullTest() {
		boolean executed = false
		Closure setup = {
			tasks.processImages << {
				
				def largeThumbs = resize {
					width = 300
				}
				
				def smallThumbs = resize {
					input = largeThumbs // Faster than default input (because the largeThumbs will all have been loaded & resized already)
					width = 150
				}
				
				save {
					input = largeThumbs
					namePattern = {File file -> "largeThumbs/{file.name}.jpg"}
				}
				
				saveAsGrid {
					input = smallThumbs
					maxWidth = 700
					maxHeight = 300
					namePattern = {File file -> "smallThumbs/{file.name}.jpg" }
					outputFormat = JPEG({
						quality = 75
					})
					info = 'smallThumbs.json'
				}
				
//				clone {
//					namePattern = {File file -> "original/{file.name}.jpg" }
//				}
			}
			tasks.processImages.execute();
		}
		setup.delegate = project
		setup.call()
	}
} 