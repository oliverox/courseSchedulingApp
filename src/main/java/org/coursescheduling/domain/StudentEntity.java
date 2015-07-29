package org.coursescheduling.domain;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.coursescheduling.domain.solver.StudentDifficultyComparator;
import org.coursescheduling.domain.solver.CourseStrengthComparator;

@PlanningEntity(difficultyComparatorClass = StudentDifficultyComparator.class)
public class StudentEntity {
	private long id;
	private String name;
	private String academy;
	private String requested;
	private Integer priority;
	// Planning variables: changes during planning, between score calculations.
	private CourseEntity assigned;


	public StudentEntity() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
                this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setAcademy(String academy) {
		this.academy = academy;
	}

	public String getAcademy() {
		return academy;
	}

	public void setRequested(String courseName) {
		this.requested = courseName;
	}

	public String getRequested() {
		return requested;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getPriority() {
		return priority;
	}

	@PlanningVariable(valueRangeProviderRefs = {"courseRange"}, strengthComparatorClass = CourseStrengthComparator.class)
	public CourseEntity getAssigned() {
		return assigned;
	}

	public void setAssigned(CourseEntity course) {
		this.assigned = course;
	}

	@Override
	public String toString() {
		return "Student: " + name + ", requested: " + requested; // + ", assigned: " + assigned.toString();
	}

}
