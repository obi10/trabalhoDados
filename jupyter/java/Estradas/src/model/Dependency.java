package model;

public class Dependency {
	public String before;
	public String after;
	public int time;
	
	public Dependency(String before, String after, int time) {
		this.before = before;
		this.after = after;
		this.time = time;
	}
	
	public Dependency(String before, String after) {
		this.before = before;
		this.after = after;
	}
}
