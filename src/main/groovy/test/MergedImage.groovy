package test

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.plugins.jpeg.JPEGImageWriteParam
import javax.imageio.stream.FileImageOutputStream

class MergedImage {
	
	private int height = -1
	private int addedImages = 0
	private int currentImageNo = 0
	
	int width
	int maxImages
	Image outputImage
	List<BufferedImage> tempImages
	File folder
	String fileName
	Graphics2D graphics
	
	def MergedImageWriter(int width, int maxImages, File folder, String fileName) {
		this.width = width
		this.maxImages = maxImages
		this.fileName = fileName
		this.folder = folder
	}
	
	void validateHeight(Image image) {
		def newHeight = (int) width/image.width*image.height;
		if( height != -1 && newHeight != height ) {
			throw new RuntimeException("Scaled Image(${image.width}x${image.height} -> ${width}x${newHeight}) size doesn't match others image size (${width}, ${height})!")
		}
	}
	
	void calculateHeight(Image image) {
		height = width/image.width*image.height
		int w = image.width;
		int h = image.height;
		tempImages = new ArrayList<BufferedImage>();
		while( w == width && h == height ) {
			if (w > width) {
				w = w >> 1;
				if (w < width) w = width;
			}
			
			if (h > height) {
				h = h >> 1;
				if (h < height) h = height;
			}
			
			tempImages += new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		}
	}
	
	BufferedImage multiPassResize(BufferedImage image) {
		BufferedImage formerImage = image;
		for( BufferedImage nextImage in tempImages ) {
			Graphics2D graphics = nextImage.createGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics.drawImage(formerImage, 0, 0, nextImage.width, nextImage.height, null);
			formerImage = nextImage;
		}
		return formerImage;
	}
	
	void addImage(Image image) {
		if( !outputImage ) {
			if( image.width < width ) {
				throw new RuntimeException("The thumbnails must be smaller or equal to the source ${image.width} <= ${width}");
			}
			if( height == -1 ) {
				calculateHeight(image)
			}
			if( height * maxImages > 65500 ) {
				throw new RuntimeException("Max height exceeded in writer for width=${width} height=${height} maxImages=${maxImages}");
			}
			outputImage = new BufferedImage(width, height*maxImages, BufferedImage.TYPE_INT_RGB);
			graphics = outputImage.createGraphics()
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
		} else {
			validateHeight(image);
		}
		
		graphics.drawImage(multiPassResize(image), 0, addedImages*height, width, height, null)
		
		++addedImages
		
		if(addedImages>maxImages) {
			finishImage();
		}
	}
	
	private void finishImage() {
		addedImages = 0;
		
		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next()
		JPEGImageWriteParam param = writer.getDefaultWriteParam()
		param.compressionMode = ImageWriteParam.MODE_EXPLICIT;
		param.compressionQuality = 1
		param.optimizeHuffmanTables = true
		param.progressiveMode = ImageWriteParam.MODE_DEFAULT;
		def stream = new FileImageOutputStream(new File(fileName+"."+currentImageNo+".jpeg", folder));
		writer.output = stream;
		
		IIOImage imageWrapper = new IIOImage(outputImage, null, null);
		writer.write(null, imageWrapper, param);
		stream.close()
		currentImageNo ++;
	}
	
	public void cropAndSafeLastImage() {
		if( addedImages > 0 ) {
			def croppedImage = new BufferedImage(width, height*addedImages, BufferedImage.TYPE_3BYTE_BGR);
			croppedImage.graphics.drawImage(outputImage, 0, 0, null, null);
			outputImage = croppedImage;
			finishImage();
		}
	}
}
