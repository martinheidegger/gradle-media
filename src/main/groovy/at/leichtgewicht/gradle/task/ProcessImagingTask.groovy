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

import at.leichtgewicht.gradle.ImageMeta
import at.leichtgewicht.gradle.ImagingProcess
import at.leichtgewicht.gradle.process.ProcessInfo
import at.leichtgewicht.gradle.process.ResizeProcess
import at.leichtgewicht.gradle.process.SaveAsGridProcess
import at.leichtgewicht.gradle.process.SaveProcess

class ProcessImagingTask extends DefaultTask {
	
	protected static CycleDetector<Object, DefaultEdge> detector = new CycleDetector<Object, DefaultEdge>()
	
	protected ArrayList<ImagingProcess> processes = new ArrayList<ImagingProcess>()
	protected DirectedGraph<Object, DefaultEdge> dependencyGraph = null
	protected Set<FileCollection> dataInput = null
	protected ImageMeta meta = new ImageMeta()
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
		logger.info "Starting ${dataInput.size()} root processes"
		dataInput.each {
			executeDataInput(it)
		}
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
		dataInput = new HashSet<FileCollection>();
	}
	
	protected void fillGraph() {
		logger.debug "Found ${processes.size()} processes to work with"
		processes.each { ImagingProcess process ->
			def input = getInput(process)
			if( input != null && input instanceof FileCollection ) {
				dataInput.add(input)
			}
			if( !dependencyGraph.containsVertex(input)) {
				dependencyGraph.addVertex(input)
			}
			if( !dependencyGraph.containsVertex(process)) {
				dependencyGraph.addVertex(process)
			}
			dependencyGraph.addEdge(input, process)
		}
	}
	
	protected def getInput(ImagingProcess process) {
		if( process.input != null ) {
			logger.info "Taking custom input ${process.input} for ${process}"
			return process.input
		}
		if( project.imaging.input ) {
			logger.info "Using Images from project.imaging setting"
			return project.imaging.input
		}
		def folder = project.file('images')
		def images = project.fileTree(dir: 'images', include: '**/*')
		logger.info "Resetting input to convention folder ${folder.absolutePath}"
		return images
	}
	
	protected def executeDataInput(FileCollection files) {
		logger.info "Processing ${files}"
		files.each { File file->
			if(file.exists() && !file.isDirectory()) {
				logger.info "Processing file ${file.name}"
				meta.original = file
				executeRelatedProcesses(files, file.name, meta)
				processInfo.add(meta.clear())
			}
		}
		processInfo.process(this)
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
			ImagingProcess process = something
			meta.setLastName(parentName)
			process.execute(this, meta)
			executeRelatedProcesses(process, process.name != null ? process.name : parentName, meta)
		} else {
			logger.error "Process ${something} is not a valid process"
		}
	}
	
	protected boolean assertThatInputIsProper(something) {
		return something instanceof ImagingProcess
	}
	
	protected void executeRelatedProcesses(something, String name, ImageMeta meta) {
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
	
	protected def addAndConfigure(ImagingProcess process, Closure config) {
		configure(process, config)
		return add(process)
	}
	
	protected def add(ImagingProcess process) {
		processes.add(process)
		logger.info "Adding process ${process}"
		return process
	}
}
