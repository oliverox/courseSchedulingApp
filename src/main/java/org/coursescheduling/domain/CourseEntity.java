package org.coursescheduling.domain;

public class CourseEntity {
	private int id;
	private String courseId;
	private int index;
	private int period;
	private String courseName;
	private String staffName;
	private int staffId;
	private String room;
	private String academy;
	private int capacity;
	private int capacityUsed;

	public CourseEntity() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

	public int getStaffId() {
		return staffId;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getRoom() {
		return room;
	}

	public void setAcademy(String academy) {
		this.academy = academy;
	}

	public String getAcademy() {
		return academy;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public int getPeriod() {
		return period;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacityUsed(int capacityUsed) {
		this.capacityUsed = capacityUsed;
	}

	public int getCapacityUsed() {
		return capacityUsed;
	}

	@Override
	public String toString() {
		return "Course: [" + id + ", " + courseId + "--" + index + ", " + courseName + ", period: " + period + ", staffName: " + staffName + ", capacity: " + capacity + ", academy: " + academy + "]";
	}

}
