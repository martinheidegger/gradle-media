package at.leichtgewicht.gradle.util

import groovy.util.logging.Log

import java.awt.Image;
import java.io.File;

import javax.activation.MimetypesFileTypeMap
import javax.imageio.ImageIO
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream

import static at.leichtgewicht.gradle.util.FileUtil.getFileSuffix

class ImageUtil {
	
	static final Image getImage(File file) {
		def reader = getImageReader(file)
		if( reader ) {
			try {
				reader.setInput(new FileImageInputStream(file))
				return reader.read(0)
			} catch( e ) {
				Log.warn "Error while reading file ", file.name, ": ", e
			}
		} else {
			Log.warn "No image reader for ", file.name, " (", new MimetypesFileTypeMap().getContentType(file), ")"
		}
	}

	static final ImageReader getImageReader(File file) {
		def readers = ImageIO.getImageReaders(file)
		if( readers.hasNext() ) return readers.next()
		
		String suffix = getFileSuffix(file)
		if( suffix ) {
			readers = ImageIO.getImageReadersBySuffix(suffix)
			if( readers.hasNext() ) return readers.next()
		}
	}
}
