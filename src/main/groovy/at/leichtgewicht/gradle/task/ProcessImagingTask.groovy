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
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

import at.leichtgewicht.gradle.ImageMeta
import at.leichtgewicht.gradle.ImagingProcess
import at.leichtgewicht.gradle.process.CloneProcess
import at.leichtgewicht.gradle.process.MoveProcess
import at.leichtgewicht.gradle.process.ResizeProcess
import at.leichtgewicht.gradle.process.SaveAsGridProcess
import at.leichtgewicht.gradle.process.SaveProcess

class ProcessImagingTask extends DefaultTask {
	
	private ArrayList<ImagingProcess> processes = new ArrayList<ImagingProcess>()
	private DirectedGraph<Object, DefaultEdge> dependencyGraph = null
	private static CycleDetector<Object, DefaultEdge> detector = new CycleDetector<Object, DefaultEdge>()
	private Set<FileCollection> dataInput = null
	private ImageMeta meta = new ImageMeta()
	
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
	
	private void setUp() {
		logger.info "Setting up all processes"
		processes.each { process ->
			process.setUp()
		}
	}
	
	private void processFileTasks() {
		logger.info "Starting ${dataInput.size()} root processes"
		dataInput.each {
			executeDataInput(it)
		}
	}
	
	private void tearDown() {
		processes.each { process ->
			process.tearDown()
		}
	}
	
	private boolean graphIsCycleFree() {
		detector.graph = dependencyGraph
		return !detector.detectCycles()
	}
	
	private Set<Object> allGraphEntries() {
		return dependencyGraph.vertexSet()
	}
	
	private void prepareGraph() {
		clearGraph()
		fillGraph()
	}
	
	private void clearGraph() {
		dependencyGraph = new DefaultDirectedGraph<Object, DefaultEdge>(DefaultEdge.class);
		dataInput = new HashSet<FileCollection>();
	}
	
	private void fillGraph() {
		logger.info "Found ${processes.size()} processes to work with"
		processes.each { ImagingProcess process ->
			def input = getInput(process)
			if( input instanceof FileCollection ) {
				dataInput.add(process.input)
			}
			dependencyGraph.addVertex(input)
			dependencyGraph.addVertex(process)
			dependencyGraph.addEdge(process, input)
		}
	}
	
	private def getInput(ImagingProcess process) {
		if( process.input ) {
			logger.info "Taking custom input for ${process}"
			return process.input
		}
		if( project.imaging.input ) {
			logger.info "Using Images from project.imaging setting"
			return project.imaging.input
		}
		def folder = project.file('images')
		def images = project.files('images/*')
		logger.info "Resetting input to convention folder ${folder.absolutePath}"
		return images
	}
	
	private def executeDataInput(FileCollection files) {
		logger.info "Processing ${files.size()}"
		files.each { File file->
			if(file.exists() && !file.isDirectory()) {
				logger.info "Processing file ${file.name}"
				meta.original = file
				executeRelatedProcesses(files)
			}
		}
		meta.original = null
	}
	
	private Set<Object> processesDependingOn(other) {
		return dependencyGraph.edgesOf(other)
	}
	
	private def executeProcess(something) {
		if( something instanceof ImagingProcess ) {
			ImagingProcess process = something
			process.execute(meta)
		}
	}
	
	private void executeRelatedProcesses(something) {
		processesDependingOn(something).each { process ->
			executeProcess(process)
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
	
	CloneProcess clone(Closure config) {
		return addAndConfigure(new CloneProcess(), config)
	}
	
	MoveProcess move(Closure config) {
		return addAndConfigure(new MoveProcess(), config)
	}
	
	SaveProcess save(Closure config) {
		return addAndConfigure(new SaveProcess(), config)
	}
	
	SaveAsGridProcess saveAsGrid(Closure config) {
		return addAndConfigure(new SaveAsGridProcess(), config)
	}
	
	private def addAndConfigure(ImagingProcess process, Closure config) {
		configure(process, config)
		return add(process)
	}
	
	private def add(ImagingProcess process) {
		processes.add(process)
		logger.info "Adding process ${process}"
		return process
	}
}
