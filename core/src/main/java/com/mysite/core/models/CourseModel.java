package com.mysite.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.List;

@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CourseModel {

    // --- Existing Fields ---
    @ValueMapValue
    private String courseTitle;

    @ValueMapValue
    private String duration;

    @ValueMapValue
    private String courseDescription; // Assuming you have this from Day 10

    @ValueMapValue
    private String courseImage; // Assuming you have this from Day 10

    // --- NEW ---
    // Inject the child nodes from the 'modules' multifield. Sling will find the
    // child node named 'modules' and adapt its children into a List of Module objects.
    @ChildResource
    private List<Module> modules;

    private String titleInUpperCase;

    @PostConstruct
    protected void init() {
        if (courseTitle != null) {
            titleInUpperCase = courseTitle.toUpperCase();
        }
    }

    // --- Existing Getters ---
    public String getCourseTitle() {
        return courseTitle;
    }

    public String getDuration() {
        return duration;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public String getCourseImage() {
        return courseImage;
    }

    public String getTitleInUpperCase() {
        return titleInUpperCase;
    }

    // --- NEW Getter ---
    public List<Module> getModules() {
        return modules;
    }

    // --- NEW Nested Class for a Single Module ---
    // This is a simple Sling Model whose only job is to represent one
    // item from our multifield.
    @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class Module {

        // Injects the 'moduleName' property from the multifield item node.
        @ValueMapValue
        private String moduleName;

        public String getModuleName() {
            return moduleName;
        }
    }
}
