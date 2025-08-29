package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(
        service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Course Search Servlet"
        }
)
@SlingServletPaths("/bin/mysite/search/courses")
public class CourseSearchServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(CourseSearchServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String searchQuery = request.getParameter("q");
        String level = request.getParameter("level");
        String category = request.getParameter("category");

        JSONObject jsonResponse = new JSONObject();
        JSONArray results = new JSONArray();

        ResourceResolver resolver = request.getResourceResolver();
        Session session = resolver.adaptTo(Session.class);

        try {
            // Build JCR SQL2 query to find course components
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM [nt:unstructured] AS course ");
            sql.append("WHERE ISDESCENDANTNODE(course, '/content/mysite') ");
            sql.append("AND course.[sling:resourceType] = 'mysite/components/coursecard'");

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                sql.append(" AND (");
                sql.append("LOWER(course.[courseTitle]) LIKE '%").append(searchQuery.toLowerCase()).append("%'");
                sql.append(" OR LOWER(course.[courseDescription]) LIKE '%").append(searchQuery.toLowerCase()).append("%'");
                sql.append(")");
            }

            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(sql.toString(), Query.JCR_SQL2);
            QueryResult queryResult = query.execute();
            NodeIterator nodeIterator = queryResult.getNodes();

            while (nodeIterator.hasNext()) {
                Node courseNode = nodeIterator.nextNode();
                Resource courseResource = resolver.getResource(courseNode.getPath());

                if (courseResource != null) {
                    ValueMap properties = courseResource.getValueMap();

                    JSONObject course = new JSONObject();
                    course.put("title", properties.get("courseTitle", ""));
                    course.put("description", properties.get("courseDescription", ""));
                    course.put("path", courseResource.getPath());

                    // Get course level and category from modules if available
                    String courseLevel = properties.get("courseLevel", "");
                    String courseCategory = properties.get("courseCategory", "");

                    // Apply filters
                    boolean includeInResults = true;
                    if (level != null && !level.isEmpty() && !courseLevel.equalsIgnoreCase(level)) {
                        includeInResults = false;
                    }
                    if (category != null && !category.isEmpty() && !courseCategory.equalsIgnoreCase(category)) {
                        includeInResults = false;
                    }

                    if (includeInResults) {
                        course.put("level", courseLevel);
                        course.put("category", courseCategory);
                        course.put("price", properties.get("coursePrice", ""));

                        // Get parent page URL
                        Resource pageResource = findParentPage(courseResource);
                        if (pageResource != null) {
                            course.put("pageUrl", pageResource.getPath() + ".html");
                        }

                        results.put(course);
                    }
                }
            }

            jsonResponse.put("success", true);
            jsonResponse.put("results", results);
            jsonResponse.put("count", results.length());

        } catch (Exception e) {
            LOG.error("Error searching courses", e);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Search failed: " + e.getMessage());
        }

        response.getWriter().write(jsonResponse.toString());
    }

    private Resource findParentPage(Resource resource) {
        Resource parent = resource.getParent();
        while (parent != null) {
            if (parent.getResourceType().equals("cq:Page")) {
                return parent;
            }
            parent = parent.getParent();
        }
        return null;
    }
}