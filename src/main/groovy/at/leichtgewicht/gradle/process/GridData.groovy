package at.leichtgewicht.gradle.process

class GridData {
	int x
	int y
	int w
	int h
	String img
	def GridData(x, y, width, height, name) {
		this.x = x
		this.y = y
		this.w = width
		this.h = height
		this.img = name
	}
}
