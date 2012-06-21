package at.leichtgewicht.gradle.process.format

import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.plugins.jpeg.JPEGImageWriteParam

class JpegFormat {
	float quality = 1
	
	ImageWriteParam getParam() {
		JPEGImageWriteParam param = writer.getDefaultWriteParam()
		param.compressionMode = ImageWriteParam.MODE_EXPLICIT;
		param.compressionQuality = quality
		param.optimizeHuffmanTables = true
		param.progressiveMode = ImageWriteParam.MODE_DEFAULT;
		return param
	}
	
	ImageWriter getWriter() {
		return ImageIO.getImageWritersByFormatName("jpeg").next()
	}
}
