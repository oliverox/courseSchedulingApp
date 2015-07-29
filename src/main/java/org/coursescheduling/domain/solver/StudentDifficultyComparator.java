package org.coursescheduling.domain.solver;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.coursescheduling.domain.StudentEntity;

public class StudentDifficultyComparator implements Comparator<StudentEntity>, Serializable {

    public int compare(StudentEntity a, StudentEntity b) {
        return new CompareToBuilder()
                .append(b.getPriority(), a.getPriority())
                .append(a.getName(), b.getName())
                .toComparison();
    }

}
