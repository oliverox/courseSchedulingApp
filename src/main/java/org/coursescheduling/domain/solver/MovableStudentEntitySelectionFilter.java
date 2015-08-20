package org.coursescheduling.domain.solver;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.coursescheduling.domain.StudentEntity;

public class MovableStudentEntitySelectionFilter implements SelectionFilter<StudentEntity> {

    public boolean accept(ScoreDirector scoreDirector, StudentEntity studentEntity) {
        return !studentEntity.getLocked();
    }

}
