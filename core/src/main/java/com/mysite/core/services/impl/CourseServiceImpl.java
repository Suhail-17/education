package com.mysite.core.services.impl;

import com.mysite.core.configs.CourseServiceConfig; // NEW IMPORT
import com.mysite.core.services.CourseService;
import org.osgi.service.component.annotations.Activate; // NEW IMPORT
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate; // NEW IMPORT
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors; // NEW IMPORT

@Component(service = CourseService.class, immediate = true)
@Designate(ocd = CourseServiceConfig.class) // NEW ANNOTATION to link the config
public class CourseServiceImpl implements CourseService {

    // NEW CLASS VARIABLE to store the configured value
    private int maxCourses;

    // This method is called when the component is activated or its configuration is changed.
    @Activate
    protected void activate(final CourseServiceConfig config) {
        // Read the 'maxCourses' property from the configuration and store it
        maxCourses = config.maxCourses();
    }

    @Override
    public List<String> getCourseTitles() {
        List<String> allCourses = Arrays.asList(
                "AEM Basics",
                "Advanced Component Development",
                "AEM Headless",
                "AEM Workflows and Services",
                "AEM Dispatcher and Caching"
        );

        // UPDATED LOGIC to use the configured value
        return allCourses.stream().limit(maxCourses).collect(Collectors.toList());
    }
}
