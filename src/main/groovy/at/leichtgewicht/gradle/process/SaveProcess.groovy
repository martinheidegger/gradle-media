package at.leichtgewicht.gradle.process




import java.awt.Image
import java.awt.Window.Type;

import javax.imageio.IIOImage
import javax.imageio.ImageWriter
import javax.imageio.stream.FileImageOutputStream

import at.leichtgewicht.gradle.MediaMeta
import at.leichtgewicht.gradle.MediaProcess
import at.leichtgewicht.gradle.process.format.JpegFormat
import at.leichtgewicht.gradle.util.FileUtil;

import org.apache.log4j.Logger
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import static at.leichtgewicht.gradle.util.LangUtil.configure

class SaveProcess extends AbstractMediaProcess implements MediaProcess {
	
	private static def logger = Logger.getLogger('SaveProcess')
	
	def input = null
	def format = JPEG()
	Closure namePattern
	
	protected def count = 0
	
	JpegFormat JPEG(Closure config) {
		return configure(new JpegFormat(), config)
	}
	
	@Override
	public void setUp(Task parentTask) {
		count = 0
	}
	
	@Override
	public void execute(Task task, MediaMeta meta) {
		
		File file = prepareFile(task, meta)
		
		JpegFormat format = getOutputFormat(meta, file.name)
		
		meta.setData(meta.lastName, FileUtil.getRelativePath(file))
		
		saveFile(format, meta.lastImage, file)
	}
	
	protected File prepareFile(Task task, MediaMeta meta) {
		return prepareFile( resolveFile(task, meta) )
	}
	
	protected File prepareFile(File file) {
		file.parentFile.mkdirs()
		if( file.exists() ) {
			file.delete()
		}
		return file
	}
	
	void saveFile(JpegFormat format, Image image, File file) {
		
		logger.info "Saving to ${file.absolutePath} as ${format}"
		ImageWriter writer = format.getWriter()
		
		def stream = new FileImageOutputStream(file)
		writer.output = stream
		
		IIOImage imageWrapper = new IIOImage(image, null, null);
		writer.write(null, imageWrapper, format.param);
		stream.close()
		
		count++
	}
	
	File resolveFile(Task task, MediaMeta meta) {
		if( namePattern == null ) {
			throw new RuntimeException("You need to define a output pattern for ${task}")
		}
		String extension = FileUtil.getFileSuffix(meta.original )
		String name = FileUtil.getFilePrefix(meta.original )
		return FileUtil.resolveFile(task, namePattern, [meta, meta.original, count, name, extension, meta.lastImage, meta.lastImage.width, meta.lastImage.height])
	}
	
	protected JpegFormat getOutputFormat(meta, fileName) {
		return format ? format : JPEG()
	}
}
