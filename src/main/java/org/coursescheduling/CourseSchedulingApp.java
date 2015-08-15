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

			System.out.println("JSON output file location: " + CourseSchedulingApp.class.getResource("/org/coursescheduling/data/output.json").toURI());
			Gson gson = new Gson();
			printWriter.println(gson.toJson(solvedSolution.getStudentList()));
			printWriter.flush();

			// List students by period
			System.out.println("**** Listing students by period ****");
			listStudentsByPeriod(solvedSolution);

			System.out.println("\n**** Listing students by courses ****");
			listStudentsByCourses(solvedSolution);

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
 			courselist = gson.fromJson(readFile("/org/coursescheduling/data/courses.json"), collectionType);
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
			studentlist = gson.fromJson(readFile("/org/coursescheduling/data/students_orig.json"), collectionType);
			for (StudentEntity student : studentlist) {
				System.out.println("+++++++++ " + student.toString() + "\n");
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
		solution.setStudentList(getStudentListFromJson());
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

	private static void listStudentsByCourses(CourseSchedulingSolution solution) {
		String output = spacer(" ", CW);
		int maxSize = 0;

		for (CourseEntity course : solution.getCourseList()) {
			output = output + spacer(course.getId() + " - " + course.getCourseId() + "--" + course.getIndex(), CW);
			for (StudentEntity student : solution.getStudentList()) {
				if (student.getAssignedCourse().getId() == course.getId()) {
					course.addStudent(student);
					if (course.getStudents().size() > maxSize) {
						maxSize = course.getStudents().size();
					}
				}
			}
		}
		System.out.println(output);

		for (int i=0; i<maxSize; i++) {
			output = spacer(Integer.toString(i + 1), CW);
			for (CourseEntity course : solution.getCourseList()) {
				if ((course.getStudents() == null) || (i > course.getStudents().size() - 1)) {
					output = output + spacer(" ", CW);
				}
				else {
					output = output + spacer(Integer.toString(course.getStudents().get(i).getStudentId()), CW);
				}
			}
			System.out.println(output);
		}
	}

	private static void listStudentsByPeriod(CourseSchedulingSolution solution) {
		String output = spacer("StudentId", CW) +
						spacer("StudentName", CW) +
						spacer("P0", CW) +
						spacer("P1", CW) +
						spacer("P2", CW) +
						spacer("P3", CW) +
						spacer("P4", CW) +
						spacer("P5", CW) +
						spacer("P6", CW) +
						spacer("P7", CW) +
						spacer("P8", CW) +
						spacer("P9", CW) +
						spacer("Courses Requested", CW);
		System.out.println(output);

		String[] courseAtPeriod;
		ArrayList<String> studentsProcessed = new ArrayList<String>();
		List<StudentEntity> studentList = solution.getStudentList();

		for (StudentEntity student : studentList) {
			if (!arrContains(studentsProcessed, Integer.toString(student.getStudentId()))) {
				studentsProcessed.add(Integer.toString(student.getStudentId()));
				courseAtPeriod = new String[] {"","","","","","","","","",""};
				ArrayList<String> coursesRequested = new ArrayList<String>();
				for (StudentEntity currentStudent : studentList) {
					if (currentStudent.getStudentId() == student.getStudentId()) {
						coursesRequested.add(currentStudent.getRequestedCourseId());
						if (courseAtPeriod[currentStudent.getAssignedCourse().getPeriod()] != "") {
							courseAtPeriod[currentStudent.getAssignedCourse().getPeriod()] = courseAtPeriod[currentStudent.getAssignedCourse().getPeriod()] + " | " + currentStudent.getAssignedCourse().getCourseId() + "--" + currentStudent.getAssignedCourse().getIndex();
						}
						else {
							courseAtPeriod[currentStudent.getAssignedCourse().getPeriod()] = currentStudent.getAssignedCourse().getCourseId() + "--" + currentStudent.getAssignedCourse().getIndex();
						}
					}
				}
				output = 	spacer(Integer.toString(student.getStudentId()), CW) +
							spacer(student.getLastName() + ", " + student.getFirstName(), CW) +
							spacer(courseAtPeriod[0], CW) +
							spacer(courseAtPeriod[1], CW) +
							spacer(courseAtPeriod[2], CW) +
							spacer(courseAtPeriod[3], CW) +
							spacer(courseAtPeriod[4], CW) +
							spacer(courseAtPeriod[5], CW) +
							spacer(courseAtPeriod[6], CW) +
							spacer(courseAtPeriod[7], CW) +
							spacer(courseAtPeriod[8], CW) +
							spacer(courseAtPeriod[9], CW) +
							spacer(coursesRequested.toString(), CW);
				System.out.println(output);
			}
		}
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
