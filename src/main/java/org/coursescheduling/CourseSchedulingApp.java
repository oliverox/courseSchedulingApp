package org.coursescheduling;

import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.util.Set;
import java.util.HashSet;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import org.coursescheduling.domain.CourseSchedulingSolution;
import org.coursescheduling.domain.StudentEntity;
import org.coursescheduling.domain.CourseEntity;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;

public class CourseSchedulingApp {
	public static final String SOLVER_CONFIG_XML = "org/coursescheduling/solver/courseSchedulingSolverConfig.xml";
	public static final int CW = 20;
	public static ScoreDirector scoreDirector;

	public static ArrayList<StudentEntity> placeIntoCourseBySubjectRequestedArray = new ArrayList<StudentEntity>();
	public static ArrayList<CourseEntity> dontExceedCourseMaxCapacityArray = new ArrayList<CourseEntity>();
	public static ArrayList<StudentEntity> dontPlaceIntoCoursesAtTheSamePeriodsArray = new ArrayList<StudentEntity>();


	public static String spacer(String word, int length) {
		int wl = word.length();
		if (wl < length) {
			for (int i=0; i<(length - wl); i++) {
				word += " ";
			}
		}
		return word + "\t";
	}

	public static String readFile(String fileName) throws IOException {
		StringBuilder sb = new StringBuilder();
    	try {
			BufferedReader br = new BufferedReader(new FileReader(new File(CourseSchedulingApp.class.getResource(fileName).toURI())));
        	String line = br.readLine();

        	while (line != null) {
            	sb.append(line);
            	sb.append("\n");
            	line = br.readLine();
        	}
			br.close();
			System.out.println(sb.toString());
		} catch (URISyntaxException ex) {
			Logger.getLogger(CourseSchedulingApp.class.getName()).log(Level.SEVERE, null, ex);
		}
		return sb.toString();
	}

	public static boolean duplicates(List<CourseEntity> courseList) {
  		Set<String> uniqueSet = new HashSet<String>();
		String uniqueId;
  		for (CourseEntity course : courseList) {
			uniqueId = course.getCourseId() + "--" + course.getIndex() + "--" + course.getPeriod();
    		if (uniqueSet.contains(uniqueId)) {
				System.out.println("Duplicates encountered for: " + uniqueId);
				return true;
			}
    		uniqueSet.add(uniqueId);
  		}
  		return false;
	}

	public static void main(String[] args) {
		try {
			File outfile = new File(CourseSchedulingApp.class.getResource("/org/coursescheduling/data/output.json").toURI());
			if (!outfile.exists()) {
				outfile.createNewFile();
			}
			PrintWriter printWriter = new PrintWriter(new FileWriter(outfile));

			Solver solver = SolverFactory.createFromXmlResource(SOLVER_CONFIG_XML).buildSolver();
			ScoreDirectorFactory scoreDirectorFactory = solver.getScoreDirectorFactory();
			scoreDirector = scoreDirectorFactory.buildScoreDirector();

			CourseSchedulingSolution unsolvedSolution = getUninitializedSolution();

			// Solve the problem
			solver.solve(unsolvedSolution);

			CourseSchedulingSolution solvedSolution = (CourseSchedulingSolution) solver.getBestSolution();

			// Display the result
			System.out.println("\nSolved courseSchedulingSolution.");
			System.out.println("BEST SCORE: " + solvedSolution.getScore() + "\n");

			System.out.println("JSON output " + CourseSchedulingApp.class.getResource("/org/coursescheduling/data/output.json").toURI());
			Gson gson = new Gson();
			printWriter.println(gson.toJson(solvedSolution.getStudentList()));
			printWriter.flush();

			// List students by period
			System.out.println("**** Listing students by period ****");
			listStudentsByPeriod(solvedSolution);

			System.out.println("\n**** Listing students by courses ****");
			listStudentsByCourses(solvedSolution);

			printReport(solvedSolution);

		} catch (IOException ex) {
			Logger.getLogger(CourseSchedulingApp.class.getName()).log(Level.SEVERE, null, ex);
		} catch (URISyntaxException ex) {
			Logger.getLogger(CourseSchedulingApp.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private static List<CourseEntity> getCourseListFromJson() throws IOException {
		List<CourseEntity> courselist = new ArrayList<CourseEntity>();
        try {
			Gson gson = new Gson();
 			Type collectionType = new TypeToken<ArrayList<CourseEntity>>(){}.getType();
 			courselist = gson.fromJson(readFile("/org/coursescheduling/data/courses-complete.json"), collectionType);
			for (CourseEntity course : courselist) {
				System.out.println(course.toString() + "\n");
			}
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CourseSchedulingApp.class.getName()).log(Level.SEVERE, null, ex);
        }
		return courselist;
	}

	private static List<StudentEntity> getStudentListFromJson() throws IOException {
		List<StudentEntity> studentlist = new ArrayList<StudentEntity>();
        try {
			Gson gson = new Gson();
			Type collectionType = new TypeToken<ArrayList<StudentEntity>>(){}.getType();
			studentlist = gson.fromJson(readFile("/org/coursescheduling/data/students-complete_orig.json"), collectionType);
			for (StudentEntity student : studentlist) {
				System.out.println("+++++++++ " + student.toString());
			}
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CourseSchedulingApp.class.getName()).log(Level.SEVERE, null, ex);
        }
		return studentlist;
	}


	private static CourseSchedulingSolution getUninitializedSolution() throws IOException {
		CourseSchedulingSolution solution = new CourseSchedulingSolution();
		solution.setId(0L);
		solution.setCourseList(getCourseListFromJson());
		solution.initializeCourseSemesters();
		solution.setStudentList(getStudentListFromJson());
		// solution.doInitialStudentAssignment();
		return solution;
	}

	private static boolean arrContains(ArrayList<String> arr, String item) {
        for (String arr1 : arr) {
            if (arr1.equals(item)) {
                return true;
            }
        }
		return false;
	}

	private static void printReport(CourseSchedulingSolution solution) {
		System.out.println("\n\n====================== REPORT ======================");
		System.out.println("BEST SCORE: " + solution.getScore() + "\n");
		System.out.println("Rule: place into course by subject requested");
		System.out.println("Broken: " + placeIntoCourseBySubjectRequestedArray.size());
		System.out.println(placeIntoCourseBySubjectRequestedArray.toString());
		System.out.println("\nRule: don't exceed course max capacity");
		System.out.println("Broken: " + dontExceedCourseMaxCapacityArray.size());
		System.out.println(dontExceedCourseMaxCapacityArray.toString());
		System.out.println("\nRule: don't place into courses at the same periods");
		System.out.println("Broken: " + dontPlaceIntoCoursesAtTheSamePeriodsArray.size());
		System.out.println(dontPlaceIntoCoursesAtTheSamePeriodsArray.toString());
		System.out.println("========================= END ========================");
	}

	private static void listStudentsByCourses(CourseSchedulingSolution solution) throws IOException, URISyntaxException {
		File csvOutfile = new File(CourseSchedulingApp.class.getResource("/org/coursescheduling/data/studentsbycourses.csv").toURI());
		if (!csvOutfile.exists()) {
			csvOutfile.createNewFile();
		}
		PrintWriter printWriter = new PrintWriter(new FileWriter(csvOutfile));

		String output = "-";
		int maxSize = 0;

		for (CourseEntity course : solution.getCourseList()) {
			output = output + "," + course.getId() + " - " + course.getCourseId() + "--" + course.getIndex();
			for (StudentEntity student : solution.getStudentList()) {
				if (student.getAssignedCourse() != null) {
					if (student.getAssignedCourse().getId() == course.getId()) {
						course.addStudent(student);
						if (course.getStudents().size() > course.getCapacity()) {
							dontExceedCourseMaxCapacityArray.add(course);
						}
						if (course.getStudents().size() > maxSize) {
							maxSize = course.getStudents().size();
						}
					}
				}
			}
		}
		System.out.println(output);
		printWriter.println(output);

		for (int i=0; i<maxSize; i++) {
			output = Integer.toString(i + 1);
			for (CourseEntity course : solution.getCourseList()) {
				if ((course.getStudents() == null) || (i > course.getStudents().size() - 1)) {
					output = output + "," + "-";
				}
				else {
					output = output + "," + Integer.toString(course.getStudents().get(i).getStudentId());
				}
			}
			System.out.println(output);
			printWriter.println(output);
		}
		System.out.println("\nStudents by courses csv " + CourseSchedulingApp.class.getResource("/org/coursescheduling/data/studentsbycourses.csv").toURI());
		printWriter.flush();
		printWriter.close();

	}

	private static void listStudentsByPeriod(CourseSchedulingSolution solution) throws URISyntaxException, IOException {
		File csvOutfile = new File(CourseSchedulingApp.class.getResource("/org/coursescheduling/data/studentsbyperiod.csv").toURI());
		if (!csvOutfile.exists()) {
			csvOutfile.createNewFile();
		}
		PrintWriter printWriter = new PrintWriter(new FileWriter(csvOutfile));

		String output = "StudentId, " +
						"StudentName, " +
						"P0, " +
						"P1, " +
						"P2, " +
						"P3, " +
						"P4, " +
						"P5, " +
						"P6, " +
						"P7, " +
						"P8, " +
						"P9, " +
						"Courses Requested";
		System.out.println(output);
		printWriter.println(output);

		ArrayList<String> studentsProcessed = new ArrayList<String>();
		List<StudentEntity> studentList = solution.getStudentList();

		for (StudentEntity student : studentList) {
			if (!student.getRequestedCourseId().equals(student.getAssignedCourse().getCourseId())) {
				placeIntoCourseBySubjectRequestedArray.add(student);
			}
			if (!arrContains(studentsProcessed, Integer.toString(student.getStudentId()))) {
				studentsProcessed.add(Integer.toString(student.getStudentId()));
				ArrayList<ArrayList<CourseEntity>> courseAtPeriod = new ArrayList<ArrayList<CourseEntity>>();

				courseAtPeriod.add(new ArrayList<CourseEntity>()); // period 0
				courseAtPeriod.add(new ArrayList<CourseEntity>()); // period 1
				courseAtPeriod.add(new ArrayList<CourseEntity>()); // period 2
				courseAtPeriod.add(new ArrayList<CourseEntity>()); // period 3
				courseAtPeriod.add(new ArrayList<CourseEntity>()); // period 4
				courseAtPeriod.add(new ArrayList<CourseEntity>()); // period 5
				courseAtPeriod.add(new ArrayList<CourseEntity>()); // period 6
				courseAtPeriod.add(new ArrayList<CourseEntity>()); // period 7
				courseAtPeriod.add(new ArrayList<CourseEntity>()); // period 8
				courseAtPeriod.add(new ArrayList<CourseEntity>()); // period 9
				ArrayList<String> coursesRequested = new ArrayList<String>();
				for (StudentEntity currentStudent : studentList) {
					if (currentStudent.getStudentId() == student.getStudentId()) {
						coursesRequested.add(currentStudent.getRequestedCourseId());
						if (currentStudent.getAssignedCourse() != null) {
							// System.out.println("\n\n**********************\nFor student: " + currentStudent.toString());
							// System.out.println("adding " + currentStudent.getAssignedCourse().toString() + " to period " + currentStudent.getAssignedCourse().getPeriod());
							courseAtPeriod.get(currentStudent.getAssignedCourse().getPeriod()).add(currentStudent.getAssignedCourse());
						}
					}
				}
				StringBuilder coursesRequestedStr = new StringBuilder();
				coursesRequestedStr.append("|");
				for (String value : coursesRequested) {
    				coursesRequestedStr.append(value + "|");
				}
				output = Integer.toString(student.getStudentId()) + ", " + student.getLastName() + " " + student.getFirstName() + ", ";
				for (int i=0; i<courseAtPeriod.size(); i++) {
					// System.out.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\nCourses at period " + i);
					if (courseAtPeriod.get(i).size() == 0) {
						output = output + ", ";
					}
					else {
						// System.out.println(courseAtPeriod.get(i).toString());
						if (courseAtPeriod.get(i).size() > 1) {
							// potential period conflict if semesters match
							for (int j=0; j<courseAtPeriod.get(i).size(); j++) {
								for (int k=0; k<courseAtPeriod.get(i).size(); k++) {
									if (k > j) {
										// System.out.println("\n^^^^^^^^ Comparing: \n" + courseAtPeriod.get(i).get(j).toString() + "\n" + courseAtPeriod.get(i).get(k).toString());
										if (courseAtPeriod.get(i).get(j).semesterMatch(courseAtPeriod.get(i).get(k))) {
											for (StudentEntity s1 : studentList) {
												if ((s1.getStudentId() == student.getStudentId()) && (s1.getAssignedCourse().getId() == courseAtPeriod.get(i).get(j).getId())) {
													// System.out.println("CONFLICT!! Adding student: " + student.toString());
													dontPlaceIntoCoursesAtTheSamePeriodsArray.add(s1);
												}
												if ((s1.getStudentId() == student.getStudentId()) && (s1.getAssignedCourse().getId() == courseAtPeriod.get(i).get(k).getId())) {
													// System.out.println("CONFLICT!! Adding student: " + student.toString());
													dontPlaceIntoCoursesAtTheSamePeriodsArray.add(s1);
												}
											}
										}
									}
								}
							}
						}
						for (int j=0; j<courseAtPeriod.get(i).size(); j++) {
							if (j == courseAtPeriod.get(i).size() - 1) {
								output = output + courseAtPeriod.get(i).get(j).getCourseId() + "--" + courseAtPeriod.get(i).get(j).getIndex() + ", ";
							}
							else {
								Boolean conflict = false;
								if (j < courseAtPeriod.get(i).size()) {
									if (courseAtPeriod.get(i).get(j).semesterMatch(courseAtPeriod.get(i).get(j+1))) {
										conflict = true;
									}
								}
								if (conflict) {
									output = output + courseAtPeriod.get(i).get(j).getCourseId() + "--" + courseAtPeriod.get(i).get(j).getIndex() + " / ";
								}
								else {
									output = output + courseAtPeriod.get(i).get(j).getCourseId() + "--" + courseAtPeriod.get(i).get(j).getIndex() + " | ";
								}
							}
						}
					}
				}
				output = output + coursesRequestedStr.toString();
				System.out.println(output);
				printWriter.println(output);
			}
		}
		System.out.println("\nStudents by period csv " + CourseSchedulingApp.class.getResource("/org/coursescheduling/data/studentsbyperiod.csv").toURI());
		printWriter.flush();
		printWriter.close();
	}


	private static String toDisplayString(CourseSchedulingSolution solution) {
		String output = spacer("id", CW) +
						spacer("StudentId", CW) +
						spacer("LastName", CW) +
						spacer("FirstName", CW) +
						spacer("Academy", CW) +
						spacer("RequestedCourseId", CW) +
						spacer("AssignedCourseId", CW) +
						spacer("MATCH?", CW) +
						spacer("AssignedCourseName", CW) +
						spacer("AssignedCourseAcademy", CW) +
						spacer("AssignedCoursePeriod", CW) +
						spacer("AssignedCourseCapacity", CW) + "\n";

		List<StudentEntity> studentList = solution.getStudentList();
        for (StudentEntity student : studentList) {
            output += 	spacer(String.valueOf(student.getId()), CW) +
						spacer(String.valueOf(student.getStudentId()), CW) +
                    	spacer(student.getLastName(), CW) +
						spacer(student.getFirstName(), CW) +
                    	spacer(student.getNextYearAcademy(), CW) +
                    	spacer(student.getRequestedCourseId(), CW);
			if (student.getAssignedCourse() != null) {
				output +=	spacer(student.getAssignedCourse().getCourseId() + "--" + Integer.toString(student.getAssignedCourse().getIndex()), CW) +
							spacer(Boolean.toString(student.getAssignedCourse().getCourseId().equals(student.getRequestedCourseId())), CW) +
							spacer(student.getAssignedCourse().getCourseName(), CW) +
							spacer(student.getAssignedCourse().getAcademy(), CW) +
							spacer(Integer.toString(student.getAssignedCourse().getPeriod()), CW) +
							spacer(Integer.toString(student.getAssignedCourse().getCapacity()), CW) + "\n";
			}
			else {
				output += "\n";
			}
        }

		return output;

	}

}
