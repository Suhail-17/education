package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(
        service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Contact Form Servlet"
        }
)
@SlingServletPaths("/bin/mysite/contact")
public class ContactServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ContactServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject jsonResponse = new JSONObject();

        try {
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String subject = request.getParameter("subject");
            String message = request.getParameter("message");

            // Validate required fields
            if (name == null || name.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    subject == null || subject.trim().isEmpty() ||
                    message == null || message.trim().isEmpty()) {

                jsonResponse.put("success", false);
                jsonResponse.put("message", "All fields are required");
                response.setStatus(400);
            } else if (!isValidEmail(email)) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid email format");
                response.setStatus(400);
            } else {
                // Log the contact request (in production, send email or save to database)
                LOG.info("Contact form submission - Name: {}, Email: {}, Subject: {}", name, email, subject);

                // Simulate email sending
                boolean emailSent = sendContactEmail(name, email, subject, message);

                if (emailSent) {
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", "Thank you for contacting us! We'll get back to you soon.");
                    response.setStatus(200);
                } else {
                    throw new Exception("Failed to send email");
                }
            }
        } catch (Exception e) {
            LOG.error("Error processing contact form", e);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred. Please try again later.");
            response.setStatus(500);
        }

        response.getWriter().write(jsonResponse.toString());
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    private boolean sendContactEmail(String name, String email, String subject, String message) {
        // In production, integrate with email service
        // For now, just log and return true
        LOG.info("Sending email - From: {} ({}), Subject: {}, Message: {}", name, email, subject, message);
        return true;
    }
}
