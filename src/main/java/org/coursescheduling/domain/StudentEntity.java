package org.coursescheduling.domain;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.coursescheduling.domain.solver.StudentDifficultyComparator;
import org.coursescheduling.domain.solver.CourseStrengthComparator;
import org.coursescheduling.domain.solver.MovableStudentEntitySelectionFilter;

@PlanningEntity(difficultyComparatorClass = StudentDifficultyComparator.class) //, movableEntitySelectionFilter = MovableStudentEntitySelectionFilter.class)
public class StudentEntity {
	private int id;
	private int studentId;
	private String lastName;
	private String firstName;
	private String nextYearGrade;
	private String nextYearAcademy;
	private String requestedCourseId;
	private Integer priority;
	private Boolean locked;
	// Planning variables: changes during planning, between score calculations.
	private CourseEntity assignedCourse;


	public StudentEntity() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
        this.id = id;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getNextYearGrade() {
		return nextYearGrade;
	}

	public void setNextYearGrade(String nextYearGrade) {
		this.nextYearGrade = nextYearGrade;
	}


	public void setNextYearAcademy(String nextYearAcademy) {
		this.nextYearAcademy = nextYearAcademy;
	}

	public String getNextYearAcademy() {
		return nextYearAcademy;
	}

	public void setRequestedCourseId(String courseId) {
		this.requestedCourseId = courseId;
	}

	public String getRequestedCourseId() {
		return requestedCourseId;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Boolean getLocked() {
		return locked;
	}

	@PlanningVariable(valueRangeProviderRefs = {"courseRange"}, strengthComparatorClass = CourseStrengthComparator.class)
	public CourseEntity getAssignedCourse() {
		return assignedCourse;
	}

	public void setAssignedCourse(CourseEntity course) {
		this.assignedCourse = course;
	}

	@Override
	public String toString() {
		if (assignedCourse == null) {
			return "Student: " + id + ", " + studentId + " - " + lastName + " " + firstName + ", requestedCourseId: " + requestedCourseId; // + ", assigned: " + assigned.toString();
		}
		else {
			return "Student: " + id + ", " + studentId + " - " + lastName + " " + firstName + ", requestedCourseId: " + requestedCourseId + ", assignedCourse: " + assignedCourse.toString();
		}
	}

}
