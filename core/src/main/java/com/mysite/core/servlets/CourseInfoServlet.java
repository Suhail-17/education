package com.mysite.core.servlets;

import com.mysite.core.models.CourseModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/courseinfo")
public class CourseInfoServlet extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        // Get the resource resolver from the request - this is needed to fetch a resource
        // For this example, we will hardcode the path to our course1 node
        Resource courseResource = request.getResourceResolver().getResource("/content/education/course1");

        // Set the content type to JSON
        response.setContentType("application/json");

        if (courseResource != null) {
            // This is the key step: Adapt the resource to our CourseModel class
            CourseModel myCourse = courseResource.adaptTo(CourseModel.class);

            if (myCourse != null) {
                // Build a JSON response dynamically using data from the model's getters
                String jsonResponse = "{\"courseTitle\":\"" + myCourse.getCourseTitle() + "\"," +
                        "\"duration\":\"" + myCourse.getDuration() + "\"," +
                        "\"titleInUpperCase\":\"" + myCourse.getTitleInUpperCase() + "\"}";

                response.getWriter().write(jsonResponse);
            } else {
                response.getWriter().write("{\"error\":\"Failed to adapt resource to CourseModel.\"}");
            }
        } else {
            response.getWriter().write("{\"error\":\"Resource /content/education/course1 not found.\"}");
        }
    }
}
