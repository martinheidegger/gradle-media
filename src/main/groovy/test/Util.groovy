package test

import java.awt.Image;
import java.io.File;

import javax.activation.MimetypesFileTypeMap
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream

class Util {

	String getFileSuffix(File file) {
		int pos = file.name.lastIndexOf('.')
		if (pos > 0 && pos < file.name.length() - 1) return file.name.substring(pos + 1)
	}

	Image getImage(File file) {
		def reader = getImageReader(file)
		if( reader ) {
			try {
				reader.setInput(new FileImageInputStream(file))
				return reader.read(0)
			} catch( e ) {
				println "Error while reading file: "+ e
			}
		} else {
			println "No image reader for: " + file.name + " - " + new MimetypesFileTypeMap().getContentType(file)
		}
	}

	ImageReader getImageReader(File file) {
		def readers = ImageIO.getImageReaders(file)
		if( readers.hasNext() ) return readers.next()
		
		String suffix = getFileSuffix(file)
		if( suffix ) {
			readers = ImageIO.getImageReadersBySuffix(suffix)
			if( readers.hasNext() ) return readers.next()
		}
	}

	File getOrCreateFolder(File file) {
		if( file.exists() && !file.isDirectory() ) {
			throw new Error("Project target path '${file.name}' is already occupied by a file");
		}
		if( file.parentFile != null && !file.parentFile.exists() ) {
			getOrCreateFolder(file.parentFile);
		}
		if( !file.exists() ) {
			file.mkdir();
		}
		return file
	}

	File sub(File file, String folderName) {
		return new File(file, folderName)
	}

}
