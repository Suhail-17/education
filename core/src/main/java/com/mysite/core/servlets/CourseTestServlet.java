package com.mysite.core.servlets;

import com.mysite.core.services.CourseService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.List;

@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/testcourses")
public class CourseTestServlet extends SlingSafeMethodsServlet {

    // Use @Reference to get our CourseService
    @Reference
    private transient CourseService courseService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        // Call the service to get the course titles
        List<String> courseTitles = courseService.getCourseTitles();

        // Print the results to the screen
        response.setContentType("text/plain");
        response.getWriter().write("Service returned " + courseTitles.size() + " course(s):\n");
        for (String title : courseTitles) {
            response.getWriter().write("- " + title + "\n");
        }
    }
}
