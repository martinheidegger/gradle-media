**Note: This project is in an EXPERIMENTal phase! Not much more than a proof of concept.**

*gradle-media* is a plugin for the gradle platform to batch-process images/videos/audio
in the current state its a proof-of-concept that does not much more than resizing and
mixing images.

At the current state I am not sure if I will develop this project any further.

*Usage*

Extend the processMedia task

    task processMedia {
    }
	
Resize images from a folder

	task processMedia {
		resize {
			width = 300
		}
	}
	
Save the resized images 

	task processMedia {
		def thumbs = resize {
			width = 300
		}
		save {
			input = thumbs
			namePattern = {String name -> "thumbs/${name}.jpg" }
		}
	}
	
Resize the images based on the temporay images

	task processMedia {
		def thumbs = resize {
			width = 300
		}
		def small = resize {
			input = thumbs
			width = 100
		}
		save {
			input = thumbs
			namePattern = {String name -> "thumbs/${name}.jpg" }
		}
		save {
			input = small
			namePattern = {String name -> "smallThumbs/${name}.jpg" }
		}
	}

Save the resized images in a grid (multiple images merged in one => request reduction)

	task processMedia {
		def thumbs = resize {
			width = 300
		}
		saveAsGrid {
			input = thumbs
			maxWidth = 900
			maxHeight = 300
			namePattern = {String name -> "thumbs/${name}.jpg" }
		}
	}
*Important things missing*

 * Proper check for necessity of the process (right now its done without asking)
 * Saving of different image formats
 
*Ideas for future work*

 * Merging images to icons
 * "proper" sprite-sheets instead of a grid processing
 * Filters (branding of images)
 * Integration of video processing suite
 * Integration of audio processing suite