package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletName;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component(
        service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Registration Form Servlet",
                "sling.auth.requirements=" + "-/bin/mysite/registration"
        }
)
@SlingServletPaths("/bin/mysite/registration")
@ServiceDescription("Registration Form Servlet")
public class RegistrationServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        // Set response headers
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        JSONObject jsonResponse = new JSONObject();

        try {
            // Extract form parameters
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String course = request.getParameter("course");
            String experience = request.getParameter("experience");
            String startDate = request.getParameter("startDate");

            LOG.info("Processing registration for: {}", email);

            // Basic validation
            if (fullName == null || fullName.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    course == null || course.trim().isEmpty() ||
                    startDate == null || startDate.trim().isEmpty()) {

                jsonResponse.put("success", false);
                jsonResponse.put("message", "Please fill in all required fields");
                response.setStatus(400);
            } else {
                // Process registration
                String registrationId = "REG-" + System.currentTimeMillis();

                LOG.info("Registration successful - ID: {}, Email: {}, Course: {}",
                        registrationId, email, course);

                jsonResponse.put("success", true);
                jsonResponse.put("message", "Registration successful! We will contact you soon.");
                jsonResponse.put("registrationId", registrationId);
                response.setStatus(200);
            }

        } catch (Exception e) {
            LOG.error("Error processing registration", e);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred. Please try again.");
            response.setStatus(500);
        }

        response.getWriter().write(jsonResponse.toString());
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("service", "Registration Servlet");
        jsonResponse.put("status", "active");
        response.getWriter().write(jsonResponse.toString());
    }
}
