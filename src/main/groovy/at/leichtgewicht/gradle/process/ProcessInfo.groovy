package at.leichtgewicht.gradle.process

import org.gradle.api.Task

import com.fasterxml.jackson.databind.ObjectMapper;

import at.leichtgewicht.gradle.util.FileUtil

class ProcessInfo {
	def toJson
	
	private List<Map<String, Object>>_data = new ArrayList<Map<String, Object>>()
	
	void add(Map<String, Object> info) {
		_data.add(info)
	}
	
	void process(Task task) {
		processJson(task)
	}
	
	void processJson(Task task) {
		File jsonFile = FileUtil.resolveFile(task, toJson, [])
		if( jsonFile != null ) {
			jsonFile.parentFile.mkdirs()
			ObjectMapper mapper = new ObjectMapper()
			mapper.writeValue(jsonFile, _data)
		}
	}
}
