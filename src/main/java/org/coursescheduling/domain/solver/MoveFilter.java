package org.coursescheduling.domain.solver;

import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.coursescheduling.domain.StudentEntity;
import org.coursescheduling.domain.CourseEntity;

public class MoveFilter implements SelectionFilter<ChangeMove> {

    public boolean accept(ScoreDirector scoreDirector, ChangeMove move) {
        StudentEntity studentEntity = (StudentEntity)move.getEntity();
        CourseEntity courseEntity = (CourseEntity)move.getToPlanningValue();
        if (studentEntity.getRequestedCourseId().equals(courseEntity.getCourseId())) {
            return true;
        }
        else {
            return false;
        }
    }

}
