package org.coursescheduling.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.coursescheduling.CourseSchedulingApp;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;

@PlanningSolution
public class CourseSchedulingSolution implements Solution<HardSoftScore>, Serializable {
	private long id;
	// Problem facts
	private List<CourseEntity> courseList;

	// Planning entities
	private List<StudentEntity> studentList;

	private HardSoftScore score;

	public static final int COLUMN_WIDTH = 20;

	public static String spacer(String word, int length) {
		int wl = word.length();
		if (wl < length) {
			for (int i=0; i<(length - wl); i++) {
				word += " ";
			}
		}
		return word;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ValueRangeProvider(id = "courseRange")
	public List<CourseEntity> getCourseList() {
		return courseList;
	}

	public void setCourseList(List<CourseEntity> courseList) {
        this.courseList = courseList;
    }

	@PlanningEntityCollectionProperty
	public List<StudentEntity> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<StudentEntity> studentList) {
		this.studentList = studentList;
	}

	public void initializeCourseSemesters() {
		for (CourseEntity course : courseList) {
			if (course.getMarkingPeriods().contains("M1")) {
				course.setSemester1(true);
			}
			else {
				course.setSemester1(false);
			}
			if (course.getMarkingPeriods().contains("M3")) {
				course.setSemester2(true);
			}
			else {
				course.setSemester2(false);
			}

		}
	}

	public void doInitialStudentAssignment() {
		for (StudentEntity student : studentList) {
			if (student.getAssignedCourse() == null) {
				CourseEntity courseToAssign = null;
				for (CourseEntity course : courseList) {
					if (course.getCourseId().contains(student.getRequestedCourseId()) && (course.getCapacity() > 0)) {
						courseToAssign = course;
						break;
					}
				}
				student.setAssignedCourse(courseToAssign);
				student.setLocked(false);
				System.out.println("Assigned: " + student.toString());
			}
		}
	}

	public HardSoftScore getScore() {
		return score;
	}

	public void setScore(HardSoftScore score) {
		this.score = score;
	}

	public Collection<? extends Object> getProblemFacts() {
		List<Object> facts = new ArrayList<Object>();
		facts.addAll(courseList);
		return facts;
	}
}
