package com.mysite.core.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Suhail's Course Service Configuration",
        description = "A configurable service for the educational site.")
public @interface CourseServiceConfig {

    @AttributeDefinition(
            name = "Maximum Courses",
            description = "The maximum number of courses to be returned by the service.",
            type = AttributeType.INTEGER)
    int maxCourses() default 3; // Default value is 3
}
