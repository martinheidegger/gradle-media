package at.leichtgewicht.gradle

import static org.junit.Assert.*;

import org.gradle.api.Project;
import org.junit.Test;

import at.leichtgewicht.gradle.task.ClearMediaTask;
import at.leichtgewicht.gradle.task.ProcessMediaTask;

class MediaTest extends AbstractProjectTest {
	
	@Test
	void availableTasks() {
		assertTaskExists("processMedia", ProcessMediaTask)
		assertTaskExists("clearMedia", ClearMediaTask)
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
			task processMedia {
				
				def largeThumbs = resize {
					name = 'l'
					width = 300
				}
				
				def smallThumbs = resize {
					name = 's'
					input = largeThumbs // Faster than default input (because the largeThumbs will all have been loaded & resized already)
					width = 150
				}
				
				save {
					input = largeThumbs
					namePattern = {String name -> "largeThumbs/${name}.jpg"}
				}
				
				saveAsGrid {
					input = smallThumbs
					maxWidth = 700
					maxHeight = 300
					namePattern = {int count -> "smallThumbs/img-${count}.jpg" }
				}
				
				info {
					toJson = "data.json"
				}
			}
			tasks.processMedia.execute();
		}
		setup.delegate = project
		setup.call()
	}
} 