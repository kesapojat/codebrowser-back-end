package fi.helsinki.cs.codebrowser.service;

import fi.helsinki.cs.codebrowser.model.Exercise;

import java.io.IOException;
import java.util.Collection;

public interface ExerciseService {

    Collection<Exercise> findAllBy(String instance, String courseId) throws IOException;
    Collection<Exercise> findAllBy(String instance, String studentId, String courseId) throws IOException;

    Exercise findBy(String instance, String courseId, String exerciseId) throws IOException;
    Exercise findBy(String instance, String studentId, String courseId, String exerciseId) throws IOException;

}
