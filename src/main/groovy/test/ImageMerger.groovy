package test

import java.io.File;


class ImageMerger {
	
	int width
	int maxImages
	
	def ImageMergerFactory(int width, int maxImages) {
		this.width = width
		this.maxImages = maxImages
	}
	
	def createImageMerger(File folder, String fileName) {
		return new MergedImage(this.width, this.maxImages, folder, fileName)
	}
}
