package org.coursescheduling.domain;

import java.util.ArrayList;

public class CourseEntity {
	private int id;
	private String courseId;
	private int index;
	private int period;
	private String cycleDays;
	private String markingPeriods;
	private String courseName;
	private String staffName;
	private int staffId;
	private String room;
	private String academy;
	private int capacity;
	private int capacityUsed;
	private ArrayList<StudentEntity> students;

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

	public String getCycleDays() {
		return cycleDays;
	}

	public void setCycleDays(String cycleDays) {
		this.cycleDays = cycleDays;
	}

	public String getMarkingPeriods() {
		return markingPeriods;
	}

	public void setMarkingPeriods(String markingPeriods) {
		this.markingPeriods = markingPeriods;
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

	public void addStudent(StudentEntity student) {
		if (this.students == null) {
			this.students = new ArrayList<StudentEntity>();
		}
		this.students.add(student);
	}

	public ArrayList<StudentEntity> getStudents() {
		return students;
	}

	public boolean periodMatch(CourseEntity otherCourse) {
		boolean match = false;
		if (period == otherCourse.getPeriod()) {
			match = true;
		}
		return match;
	}

	@Override
	public String toString() {
		return "Course: [" + id + ", " + courseId + "--" + index + ", " + courseName + ", period: " + period + ", cycleDays: " + cycleDays + ", staffName: " + staffName + ", capacity: " + capacity + ", academy: " + academy + "]";
	}

}
