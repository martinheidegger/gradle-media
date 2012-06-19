package at.leichtgewicht.gradle


import org.gradle.api.Plugin;
import org.gradle.api.Project;

import at.leichtgewicht.gradle.task.ClearMediaTask
import at.leichtgewicht.gradle.task.ProcessMediaTask;

class Media implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.tasks.add("processMedia", ProcessMediaTask);
		project.tasks.add("clearMedia", ClearMediaTask);
		project.extensions.create('media', MediaExtension);
	}
}