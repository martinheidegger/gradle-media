package at.leichtgewicht.gradle.process


import java.awt.Image;

import org.apache.log4j.Logger;
import org.gradle.api.Task

import com.mortennobel.imagescaling.Lanczos3Filter
import com.mortennobel.imagescaling.ResampleOp;

import at.leichtgewicht.gradle.MediaMeta;

class ResizeProcess extends AbstractMediaProcess {
	
	private static def logger = Logger.getLogger('ResizeProcess')
	
	int width = -1
	int height = -1
	int maxWidth = -1
	int maxHeight = -1
	float widthPercent = -1
	float heightPercent = -1
	def input = null
	def _name = null
	
	@Override
	public String getName() {
		if( _name == null ) {
			_name = renderDefaultName()
		}
		return _name;
	}
	
	private String renderDefaultName() {
		String widthString
		if( width > 0 ) {
			widthString = width
		} else if( widthPercent > 0 ) {
			widthString = "${widthPercent}%"
		}
		if( maxWidth > 0 ) {
			if( widthString == null ) {
				widthString = "?"
			}
			widthString += "<"+maxWidth
		}
		String heightString
		if( height > 0 ) {
			heightString = height
		} else if( heightPercent > 0 ) {
			heightString = "${heightPercent}%"
		}
		if( maxHeight > 0 ) {
			if( heightString == null ) {
				heightString = "?"
			}
			heightString += "<"+maxHeight
		}
		
		if( widthString != null ) {
			if( heightString != null ) {
				return "size-${widthString}x${heightString}"
			} else {
				return "size-${widthString}"
			}
		} else if( heightString != null ) {
			return "size-${heightString}"
		}
		return null
	}
	
	public void setName(String name) {
		_name = name
	}
	
	@Override
	void execute(Task task, MediaMeta meta) {
		Image image = meta.lastImage
		Size size = calculateSize(image.width, image.height)
		if( size.width != image.width || size.height != image.height ) {
			ResampleOp op = new ResampleOp(size.width, size.height)
			op.setFilter( new Lanczos3Filter() )
			logger.info "Resizing ${image.width}x${image.height} -> ${size.width}x${size.height}"
			meta.processedImage = op.filter(image, null)
		} else {
			logger.info "Resizing has nothing to do, image already of proper size"
		}
	}
	
	Size calculateSize(int width, int height) {
		int targetWidth = -1
		if( this.width > 0 ) {
			targetWidth = this.width;
		} else if( this.widthPercent > 0 ) {
			targetWidth = width * this.widthPercent
		}
		int targetHeight = -1
		if( this.height > 0 ) {
			targetHeight = this.height
		} else if( this.heightPercent > 0 ) {
			targetHeight = height * this.heightPercent
		}
		
		if( targetHeight == -1 && targetWidth == -1 ) {
			targetHeight = height
			targetWidth = width
		} else if( targetHeight == -1 ){
			targetHeight = height/width * targetWidth
		} else if( targetWidth == -1 ){
			targetWidth = width/height * targetHeight
		}
		
		if( maxWidth > 0 && targetWidth > maxWidth ) {
			targetHeight = height/width * maxWidth;
			targetWidth = maxWidth
		}
		if( maxHeight > 0 && targetHeight > maxHeight ) {
			targetWidth = width/height * maxHeight
			targetHeight = maxHeight
		}
		return new Size(targetWidth, targetHeight)
	}
}

private class Size {
	int width
	int height
	
	def Size(int width, int height) {
		this.width = width
		this.height = height
	}
}
