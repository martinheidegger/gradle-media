package at.leichtgewicht.gradle

import java.awt.image.BufferedImage;

interface ImagingProcess {
	def input;
	
	void setUp()
	void execute(ImageMeta meta)
	void tearDown()
}
