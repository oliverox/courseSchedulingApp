package org.coursescheduling.domain;

public class CourseEntity {
	private long id;
	private String subject;
	private String academy;
	private int period;
	private Integer capacity;

	public CourseEntity() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
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

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public Integer getCapacity() {
		return capacity;
	}

	@Override
	public String toString() {
		return "Course: " + subject + ", capacity: " + capacity.toString() + ", academy: " + academy;
	}

}
