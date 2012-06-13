	package at.leichtgewicht.gradle
	
	
	import org.gradle.api.Plugin;
	import org.gradle.api.Project;
	
	import at.leichtgewicht.gradle.task.ClearImagingTask
	import at.leichtgewicht.gradle.task.ProcessImagingTask;
	
	class Imaging implements Plugin<Project> {
	
		@Override
		public void apply(Project project) {
			project.tasks.add("processImages", ProcessImagingTask);
			project.tasks.add("clearImages", ClearImagingTask);
			project.extensions.create('imaging', ImagingExtension);
		}
	}
	
	
	// test
	
	/*
	
			project.extensions.add("merger",{ int width, int maxImages ->
				return new ImageMerger(width, maxImages)
			});
			
			project.extensions.add("generatedDir", "generated")
			project.extensions.add("tempDir", "temp")
			project.extensions.add("mixedImagesFolder", "mixed")
			project.extensions.add("sequenceFolder", "images")
			project.extensions.add("sizes", new ArrayList<ImageMerger>())
			
			File projectGenerated = getOrCreateFolder( sub(new File(project.generatedDir), project.name) )
			File projectTemp = getOrCreateFolder( sub(new File(project.tempDir), project.name) )
			File mixedImagesFolder = sub(projectGenerated, project.mixedImagesFolder);
			
			project.tasks.add("clearTimeLapse", {
				if( mixedImagesFolder.exists() ) mixedImagesFolder.deleteDir();
			});
			
			project.tasks.add("mergeTimeLapse", {
				def mixedImages = getOrCreateFolder( mixedImagesFolder );
				def allFiles = sub(project.projectDir, project.sequenceFolder).listFiles()
				
				def projectMerger = [];
				
				for( ImageMerger factory in project.sizes ) {
					projectMerger += factory.createImageMerger(mixedImages, factory.width.toString());
				} 
				
				for( File imageFile in allFiles ) {
					Image image = getImage(imageFile)
					
					for( MergedImage writer in projectMerger ) {
						writer.addImage(image);
					}
					
					image.flush()
				}
				
				for( MergedImage writer in projectMerger ) {
					writer.cropAndSafeLastImage();
				}
			});
		
			project.task('processImages', {
			});
	*/