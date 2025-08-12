package com.mysite.core.services;

import java.util.List;

/**
 * A service for fetching course data.
 */
public interface CourseService {

    /**
     * Returns a list of course titles.
     * @return A list of strings representing course titles.
     */
    List<String> getCourseTitles();
}
