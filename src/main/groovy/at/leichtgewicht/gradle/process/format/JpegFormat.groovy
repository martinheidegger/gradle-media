package at.leichtgewicht.gradle.process.format

import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.plugins.jpeg.JPEGImageWriteParam

class JpegFormat {
	int quality
	
	ImageWriteParam getParam() {
		JPEGImageWriteParam param = writer.getDefaultWriteParam()
		param.compressionMode = ImageWriteParam.MODE_EXPLICIT;
		param.compressionQuality = 1
		param.optimizeHuffmanTables = true
		param.progressiveMode = ImageWriteParam.MODE_DEFAULT;
		return param
	}
	
	ImageWriter getWriter() {
		return ImageIO.getImageWritersByFormatName("jpeg").next()
	}
}
