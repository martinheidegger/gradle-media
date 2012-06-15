package at.leichtgewicht.gradle.process

import at.leichtgewicht.gradle.ImagingProcess

class ResizeProcess extends AbstractImagingProcess {
	int width = -1
	int height = -1
	int maxWidth = -1
	int maxHeight = -1
	float widthPercent = -1
	float heightPercent = -1
	def input
}
