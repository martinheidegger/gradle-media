package at.leichtgewicht.gradle

import groovy.util.logging.Log;

import java.awt.Image;
import java.io.File;

import javax.activation.MimetypesFileTypeMap
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream

import static at.leichtgewicht.gradle.util.FileUtil.isFile;
import static at.leichtgewicht.gradle.util.ImageUtil.getImage;

class ImageMeta {
	
	private File _original
	private Image _originalData
	
	void setOriginal(File original) {
		if( _original != original ) {
			_original = original;
			_originalData = null;
		}
	}
	
	Image getOriginalData() {
		if( _originalData == null && isFile(_original) ) {
			_originalData = getImage(_original)
		}
		return _originalData
	}
}
