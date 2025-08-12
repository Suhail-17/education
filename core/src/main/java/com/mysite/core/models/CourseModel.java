package com.mysite.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

// @Model declares this class as a Sling Model
// We are adapting it from a Resource (a JCR node)
// DefaultInjectionStrategy.OPTIONAL means the model won't break if a property is missing
@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CourseModel {

    // Inject the 'courseTitle' property from the JCR node into this variable
    @ValueMapValue
    private String courseTitle;

    // Inject the 'duration' property
    @ValueMapValue
    private String duration;

    // This variable is not injected. We will calculate it in our business logic.
    private String titleInUpperCase;

    // A @PostConstruct method runs after all the properties have been injected.
    // It's the perfect place for any business logic.
    @PostConstruct
    protected void init() {
        // Simple business logic: create an uppercase version of the title
        if (courseTitle != null) {
            titleInUpperCase = courseTitle.toUpperCase();
        }
    }

    // Public "getter" methods to allow other classes (like servlets or HTL)
    // to read the private data.
    public String getCourseTitle() {
        return courseTitle;
    }

    public String getDuration() {
        return duration;
    }

    public String getTitleInUpperCase() {
        return titleInUpperCase;
    }
}
