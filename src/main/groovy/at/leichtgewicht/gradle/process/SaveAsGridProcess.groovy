package at.leichtgewicht.gradle.process

import java.awt.Graphics
import java.awt.Image
import java.awt.RenderingHints;
import java.awt.image.BufferedImage
import java.io.File;

import org.gradle.api.Task;

import at.leichtgewicht.gradle.MediaMeta;
import at.leichtgewicht.gradle.process.format.JpegFormat
import at.leichtgewicht.gradle.util.FileUtil

class SaveAsGridProcess extends SaveProcess {
	int maxWidth
	int maxHeight
	String info
	
	private Image currentImage
	private Graphics graphics
	private Integer currentX
	private Integer currentY
	private Integer currentHeight
	private Integer usedWidth
	private Integer usedHeight
	private File currentFile
	private JpegFormat currentFormat
	
	@Override
	public void setUp(Task parentTask) {
		currentFormat = getOutputFormat(null, null)
		super.setUp(parentTask);
	}
	
	@Override
	public void execute(Task task, MediaMeta meta) {
		if( !currentImage ) {
			createImage(task)
		}
		
		Image image = meta.lastImage
		
		int width = image.width
		int height = image.height
		
		if( width > maxWidth || height > maxHeight ) {
			throw new Error("SaveAsGrid: The Image size of ${meta.originalImage} exceeds with size ${width}x${height} (after processing) the max size of ${maxWidth}x${maxHeight}")
		}
		
		if( currentX + width > maxWidth ) {
			currentX = 0
			currentY += currentHeight
			currentHeight = height
		}
		if( currentY + height > maxHeight ) {
			nextImage(task)
		}
		meta.setData(meta.lastName, new GridData(currentX, currentY, width, height, currentFile.name))
		placeImage(image)
		currentX += width
		if( usedWidth < currentX) {
			usedWidth = currentX
		}
		if( usedHeight < currentY+currentHeight ) {
			usedHeight = currentY+currentHeight
		}
	}
	
	private void placeImage(Image image) {
		graphics.drawImage(image, currentX, currentY, image.width, image.height, null)
	}
	
	private void nextImage(Task task) {
		finishImage(task)
		createImage(task)
	}
	
	private void createImage(Task task) {
		createImage(maxWidth, maxHeight, task)
	}
	
	private void createImage(int width, int height, Task task) {
		currentX = 0
		currentY = 0
		usedWidth = 0
		usedHeight = 0
		currentHeight = 0
		currentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
		graphics = currentImage.createGraphics()
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
		currentFile = resolveFileByFormat(task, currentFormat)
	}
	
	File resolveFileByFormat(Task task, JpegFormat format) {
		if( namePattern == null ) {
			throw new RuntimeException("You need to define a output pattern for ${task}")
		}
		return FileUtil.resolveFile(task, namePattern, [count, currentImage, currentImage.width, currentImage.height, format])
	}
	
	private void finishImage(Task task) {
		if( usedHeight != maxHeight || usedWidth != maxWidth ) {
			Image temp = currentImage
			createImage(usedWidth, usedHeight, task)
			placeImage(temp)
			temp.flush()
		}
		currentFile.parentFile.mkdirs()
		
		saveFile(currentFormat, currentImage, currentFile)
		
		currentImage.flush()
		currentImage = null
	}
	
	@Override
	public void tearDown(Task parentTask) {
		super.tearDown(parentTask);
		if( currentImage != null ) {
			finishImage(parentTask)
		}
	}
}
