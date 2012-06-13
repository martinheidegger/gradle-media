package at.leichtgewicht.gradle

import java.awt.image.BufferedImage;

interface ImagingProcess {
	void setInput(File[] input);
	File[] getInput();
	void execute(ImageMeta image);
}
