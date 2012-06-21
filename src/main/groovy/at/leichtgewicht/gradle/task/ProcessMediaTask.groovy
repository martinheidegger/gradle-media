package at.leichtgewicht.gradle.task


import static at.leichtgewicht.gradle.util.LangUtil.configure
import groovy.lang.Closure;

import java.util.ArrayList

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.collections.SimpleFileCollection
import org.gradle.api.tasks.TaskAction
import org.jgrapht.DirectedGraph
import org.jgrapht.alg.CycleDetector
import org.jgrapht.alg.BlockCutpointGraph.BCGEdge;
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

import at.leichtgewicht.gradle.MediaMeta
import at.leichtgewicht.gradle.MediaProcess
import at.leichtgewicht.gradle.process.ProcessInfo
import at.leichtgewicht.gradle.process.ResizeProcess
import at.leichtgewicht.gradle.process.SaveAsGridProcess
import at.leichtgewicht.gradle.process.SaveProcess
import at.leichtgewicht.gradle.util.FileUtil;

class ProcessMediaTask extends DefaultTask {
	
	protected static CycleDetector<Object, DefaultEdge> detector = new CycleDetector<Object, DefaultEdge>()
	
	protected ArrayList<MediaProcess> processes = new ArrayList<MediaProcess>()
	protected DirectedGraph<Object, DefaultEdge> dependencyGraph = null
	private FileCollection _input = null
	protected MediaMeta meta = new MediaMeta()
	protected ProcessInfo processInfo = new ProcessInfo()
	
	@TaskAction
	def process() {
		prepareGraph()
		if( graphIsCycleFree() ) {
			setUp()
			processFileTasks()
			tearDown()
		} else {
			logger.error "Cycle in Image rendering chain detected!";
		}
	}
	
	protected void setUp() {
		logger.info "Setting up all processes"
		processes.each { process ->
			process.setUp(this)
		}
	}
	
	protected void processFileTasks() {
		logger.info "Processing ${input}"
		input.each { File file->
			if(file.exists() && !file.isDirectory()) {
				logger.info "Processing file ${file.name}"
				meta.original = file
				meta.setData('name', file.name)
				executeRelatedProcesses(input, file.name, meta)
				processInfo.add(meta.clear())
			}
		}
		processInfo.process(this)
	}
	
	protected void tearDown() {
		processes.each { process ->
			process.tearDown(this)
		}
	}
	
	protected boolean graphIsCycleFree() {
		detector.graph = dependencyGraph
		return !detector.detectCycles()
	}
	
	protected Set<Object> allGraphEntries() {
		return dependencyGraph.vertexSet()
	}
	
	protected void prepareGraph() {
		clearGraph()
		fillGraph()
	}
	
	protected void clearGraph() {
		dependencyGraph = new DefaultDirectedGraph<Object, DefaultEdge>(DefaultEdge.class);
	}
	
	protected void fillGraph() {
		logger.debug "Found ${processes.size()} processes to work with"
		processes.each { MediaProcess process ->
			def processInput = getProcessInput(process, input)
			if( !dependencyGraph.containsVertex(processInput)) {
				dependencyGraph.addVertex(processInput)
			}
			if( !dependencyGraph.containsVertex(process)) {
				dependencyGraph.addVertex(process)
			}
			dependencyGraph.addEdge(processInput, process)
		}
	}
	
	protected void setInput(input) {
		_input = FileUtil.resolveFiles(this, input, [this])
	}
	
	protected FileCollection getInput() {
		if( _input == null) {
			if( project.media.input ) {
				logger.info "Using Images from project.imaging setting"
				_input = project.media.input
			} else {
				def folder = project.file('images')
				def defaultCollection = project.fileTree(dir: 'images', include: '**/*')
				logger.info "Resetting input to convention folder ${folder.absolutePath}"
				_input = defaultCollection
			}
		}
		return _input
	}
	
	protected def getProcessInput(MediaProcess process, FileCollection defaultInput) {
		if( process.input != null ) {
			logger.info "Taking custom input ${process.input} for ${process}"
			return process.input
		} else {
			return defaultInput
		}
	}
	
	protected Set<Object> processesDependingOn(other) {
		Set<DefaultEdge> edges = dependencyGraph.edgesOf(other)
		Set<Object> depending = new HashSet<Object>();
		edges.each {
			def target = dependencyGraph.getEdgeTarget(it)
			if( target != other ) {
				depending.add( target)
			}
		}
		logger.debug "Found ${depending.size()} depending targets"
		return depending
	}
	
	protected def executeProcess(something, String parentName) {
		if( assertThatInputIsProper(something) ) {
			MediaProcess process = something
			meta.setLastName(parentName)
			def img = meta.processedImage
			process.execute(this, meta)
			executeRelatedProcesses(process, process.name != null ? process.name : parentName, meta)
			meta.processedImage = img
		} else {
			logger.error "Process ${something} is not a valid process"
		}
	}
	
	protected boolean assertThatInputIsProper(something) {
		return something instanceof MediaProcess
	}
	
	protected void executeRelatedProcesses(something, String name, MediaMeta meta) {
		processesDependingOn(something).each { process ->
			executeProcess(process, name)
		}
	}
	
	FileCollection sortByName(FileCollection files, Comparator<String> comparator) {
		logger.info "Starting to sort files by name ..."
		ArrayList<String> names = []
		files.each { File file ->
			names.add(file.absolutePath)
		}
		Collections.sort(names, comparator)
		ArrayList<File> sorted = [];
		names.each { String absolutePath ->
			sorted.add(new File(absolutePath))
		}
		logger.info "... done."
		return new SimpleFileCollection(sorted);
	}
	
	FileCollection sort(FileCollection files, Comparator<File> comparator) {
		logger.info "Starting to sort files ..."
		ArrayList<File> sorted = [];
		files.each { File file ->
			sorted.add(file)
		}
		logger.info "... done."
		return new SimpleFileCollection(sorted);
	}
	
	ResizeProcess resize(Closure config) {
		return addAndConfigure(new ResizeProcess(), config)
	}
	
	SaveProcess save(Closure config) {
		return addAndConfigure(new SaveProcess(), config)
	}
	
	SaveAsGridProcess saveAsGrid(Closure config) {
		return addAndConfigure(new SaveAsGridProcess(), config)
	}
	
	ProcessInfo info(Closure config) {
		configure(processInfo, config)
		return processInfo
	}
	
	protected def addAndConfigure(MediaProcess process, Closure config) {
		configure(process, config)
		return add(process)
	}
	
	protected def add(MediaProcess process) {
		processes.add(process)
		logger.info "Adding process ${process}"
		return process
	}
}
