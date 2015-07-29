package org.coursescheduling.domain.solver;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.coursescheduling.domain.CourseEntity;

public class CourseStrengthComparator implements Comparator<CourseEntity>, Serializable {

    public int compare(CourseEntity a, CourseEntity b) {
        return new CompareToBuilder()
                .append(b.getCapacity(), a.getCapacity())
                .toComparison();
    }

}
