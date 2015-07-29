package org.coursescheduling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;

import com.thoughtworks.xstream.XStream;
//import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.coursescheduling.domain.CourseSchedulingSolution;
import org.coursescheduling.domain.StudentEntity;
import org.coursescheduling.domain.CourseEntity;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;

public class CourseSchedulingApp {
	public static final String SOLVER_CONFIG_XML = "org/coursescheduling/solver/courseSchedulingSolverConfig.xml";
	public static final int COLUMN_WIDTH = 20;
	public static ScoreDirector scoreDirector;

	public static String spacer(String word, int length) {
		int wl = word.length();
		if (wl < length) {
			for (int i=0; i<(length - wl); i++) {
				word += " ";
			}
		}
		return word;
	}

	public static void main(String[] args) {
		try {
			File outfile = new File(CourseSchedulingApp.class.getResource("/org/coursescheduling/data/output.json").toURI());
			if (!outfile.exists()) {
				outfile.createNewFile();
			}
			PrintWriter printWriter = new PrintWriter(new FileWriter(outfile));
			XStream xstream = new XStream(new JettisonMappedXmlDriver());
			xstream.alias("course", CourseEntity.class);
			xstream.alias("student", StudentEntity.class);
			xstream.alias("CourseSchedulingSolution", CourseSchedulingSolution.class);
			Solver solver = SolverFactory.createFromXmlResource(SOLVER_CONFIG_XML).buildSolver();
			ScoreDirectorFactory scoreDirectorFactory = solver.getScoreDirectorFactory();
			scoreDirector = scoreDirectorFactory.buildScoreDirector();

			CourseSchedulingSolution unsolvedSolution = getUninitializedSolution();

			// Solve the problem
			solver.solve(unsolvedSolution);

			CourseSchedulingSolution solvedSolution = (CourseSchedulingSolution) solver.getBestSolution();

			// Display the result
			System.out.println("\nSolved courseSchedulingSolution:\n" + toDisplayString(solvedSolution));
			System.out.println("BEST SCORE: " + solvedSolution.getScore());
			String json = xstream.toXML(solvedSolution);
			System.out.println("JSON: " + json);
			printWriter.println(json);
		} catch (IOException ex) {
			Logger.getLogger(CourseSchedulingApp.class.getName()).log(Level.SEVERE, null, ex);
		} catch (URISyntaxException ex) {
			Logger.getLogger(CourseSchedulingApp.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private static List<CourseEntity> getCourseListFromJson() {
		List<CourseEntity> courselist = new ArrayList<CourseEntity>();
         try {
			FileReader fileReader = new FileReader(new File(CourseSchedulingApp.class.getResource("/org/coursescheduling/data/courses.json").toURI()));
			XStream xstream = new XStream(new JettisonMappedXmlDriver());
			xstream.alias("courselist", List.class);
			xstream.alias("course", CourseEntity.class);
			courselist = (List<CourseEntity>)xstream.fromXML(fileReader);
			for (CourseEntity course : courselist) {
				System.out.println(course.toString() + "\n");
			}
         } catch (URISyntaxException ex) {
             Logger.getLogger(CourseSchedulingApp.class.getName()).log(Level.SEVERE, null, ex);
         } catch (FileNotFoundException ex) {
             Logger.getLogger(CourseSchedulingApp.class.getName()).log(Level.SEVERE, null, ex);
         }
		return courselist;
	}

	private static List<StudentEntity> getStudentListFromJson() {
		List<StudentEntity> studentlist = new ArrayList<StudentEntity>();
         try {
			FileReader fileReader = new FileReader(new File(CourseSchedulingApp.class.getResource("/org/coursescheduling/data/students.json").toURI()));
			XStream xstream = new XStream(new JettisonMappedXmlDriver());
			xstream.alias("studentlist", List.class);
			xstream.alias("student", StudentEntity.class);
			studentlist = (List<StudentEntity>)xstream.fromXML(fileReader);
			for (StudentEntity student : studentlist) {
				System.out.println("+++++++++ " + student.toString() + "\n");
			}
         } catch (URISyntaxException ex) {
             Logger.getLogger(CourseSchedulingApp.class.getName()).log(Level.SEVERE, null, ex);
         } catch (FileNotFoundException ex) {
             Logger.getLogger(CourseSchedulingApp.class.getName()).log(Level.SEVERE, null, ex);
         }
		return studentlist;
	}


	private static CourseSchedulingSolution getUninitializedSolution() {
		CourseSchedulingSolution solution = new CourseSchedulingSolution();
		solution.setId(0L);
		solution.setCourseList(getCourseListFromJson());
		solution.setStudentList(getStudentListFromJson());
		return solution;
	}

	/*
	private static List<CourseEntity> createCourseList() {
		int numOfCourses = 4;
		List<String> subjects  = Arrays.asList("Computer Science", "English", "Spanish", "Mathematics");
		List<String> academies = Arrays.asList("Science", "Language", "Language", "Science");
		List<Integer> capacities = Arrays.asList(5, 2, 2, 5);

		List<CourseEntity> courseList = new ArrayList<CourseEntity>(numOfCourses);
		for (int i=0; i<numOfCourses; i++) {
			courseList.add(createCourse(i, subjects.get(i), academies.get(i), i+1, capacities.get(i)));
		}

		return courseList;
	}

	private static List<StudentEntity> createStudentList() {
		int numOfEntries = 8;
		List<String> names = Arrays.asList("Celedon", "Garcia", "Diaz", "Guerrero", "Celedon", "Garcia", "Diaz", "Guerrero");
		List<String> academies = Arrays.asList("Science", "Language", "Science", "Language", "Science", "Language", "Science", "Language");
		List<String> requested = Arrays.asList("Mathematics", "English", "Computer Science", "Spanish", "Spanish", "Spanish", "English", "English");
		List<Integer> priorities = Arrays.asList(1, 2, 1, 4, 4, 3, 5, 1);

		List<StudentEntity> studentList = new ArrayList<StudentEntity>(numOfEntries);
		for (int i=0; i<numOfEntries; i++) {
			studentList.add(createStudent(i, names.get(i), academies.get(i), requested.get(i), priorities.get(i)));
		}

		return studentList;
	}

	private static CourseEntity createCourse(int id, String subject, String academy, int period, Integer capacity) {
		CourseEntity course = new CourseEntity();
		course.setId((long) id);
		course.setSubject(subject);
		course.setAcademy(academy);
		course.setPeriod(period);
		course.setCapacity(capacity);
		return course;
	}

	private static StudentEntity createStudent(int id, String name, String academy, String requested, Integer priority) {
		StudentEntity student = new StudentEntity();
		student.setId((long) id);
		student.setName(name);
		student.setAcademy(academy);
		student.setRequested(requested);
		student.setPriority(priority);
		return student;
	}
	*/
	private static String toDisplayString(CourseSchedulingSolution solution) {
		String output = spacer("Sid", COLUMN_WIDTH) + "\t" +
						spacer("Name", COLUMN_WIDTH) + "\t" +
						spacer("Academy", COLUMN_WIDTH) + "\t" +
						spacer("Priority", COLUMN_WIDTH) + "\t" +
						spacer("Requested", COLUMN_WIDTH) + "\t" +
						spacer("Assigned", COLUMN_WIDTH) + "\t" +
						spacer("OK?", COLUMN_WIDTH) + "\n" +
						spacer("=============\t", COLUMN_WIDTH) +
						spacer("=============\t", COLUMN_WIDTH) +
						spacer("=============\t", COLUMN_WIDTH) +
						spacer("=============\t", COLUMN_WIDTH) +
						spacer("=============\t", COLUMN_WIDTH) +
						spacer("=============\t", COLUMN_WIDTH) +
						spacer("=============", COLUMN_WIDTH) + "\n";

		List<StudentEntity> studentList = solution.getStudentList();
        for (StudentEntity student : studentList) {
            output += 	spacer(String.valueOf(student.getId()), COLUMN_WIDTH) + "\t" +
                    	spacer(student.getName(), COLUMN_WIDTH) + "\t" +
                    	spacer(student.getAcademy(), COLUMN_WIDTH) + "\t" +
						spacer(student.getPriority().toString(), COLUMN_WIDTH) + "\t" +
                    	spacer(student.getRequested(), COLUMN_WIDTH) + "\t" +
                    	spacer(student.getAssigned().getSubject(), COLUMN_WIDTH) + "\t" +
                    	spacer(String.valueOf(student.getRequested().equals(student.getAssigned().getSubject())), COLUMN_WIDTH) + "\n";
        }

		return output;

	}

}
