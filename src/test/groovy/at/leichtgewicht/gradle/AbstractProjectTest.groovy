package at.leichtgewicht.gradle

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;

class AbstractProjectTest {
	
	protected Project project;
	
	@Before
	void setup() {
		def builder = ProjectBuilder.builder();
		builder.withProjectDir(new File('.'))
		project = builder.build()
		project.apply plugin: Media;
	}
}
