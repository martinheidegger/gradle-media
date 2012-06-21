package at.leichtgewicht.gradle

import java.awt.Image;
import java.io.File;

import javax.activation.MimetypesFileTypeMap
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream

import org.apache.log4j.Logger;

import static at.leichtgewicht.gradle.util.FileUtil.isFile;
import static at.leichtgewicht.gradle.util.ImageUtil.getImage;

class MediaMeta {
	
	private static def logger = Logger.getLogger('ImageMeta')
	
	private File _original
	private Image _originalData
	
	private Image _lastImage
	private String _lastName
	
	private Map<String, Object> _data
	
	Image getLastImage() {
		return _lastImage != null ? _lastImage : getOriginalData() 
	}
	
	Image getProcessedImage() {
		return _lastImage
	}
	
	void setProcessedImage(Image image) {
		_lastImage = image;
	}
	
	void setData(String field, data) {
		logger.info "add ${field} => ${data}"
		_data.put(field, data)
	}
	
	void setData(Map<String, Object> data) {
		_data = data
	}
	
	Map<String, Object> getData() {
		return _data
	}
	
	Map<String, Object> clear() {
		def tempData = _data
		_original = null
		if( _originalData ) {
			_originalData.flush()
			_originalData = null
		}
		if( _lastImage ) {
			_lastImage.flush()
			_lastImage = null
		}
		_data = new HashMap<String, Object>()
		return tempData
	}
	
	File getOriginal() {
		return _original
	}
	
	String getLastName() {
		return _lastName == null ? _original.name : _lastName
	}
	
	void setLastName(String name) {
		_lastName = name
	}
	
	void setOriginal(File original) {
		if( _original != original ) {
			clear()
			_original = original;
		}
	}
	
	Image getOriginalData() {
		if( _originalData == null && isFile(_original) ) {
			_originalData = getImage(_original)
		}
		return _originalData
	}
}
