package com.mysite.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/mysite/search")
public class CourseSearchServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(CourseSearchServlet.class);

    @Reference
    private QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        final String searchTerm = request.getParameter("term");
        JSONArray resultsArray = new JSONArray();

        if (searchTerm != null && !searchTerm.isEmpty()) {
            ResourceResolver resourceResolver = request.getResourceResolver();
            Session session = resourceResolver.adaptTo(Session.class);

            // UPDATED: Simplified Query Predicates
            Map<String, String> predicateMap = new HashMap<>();
            predicateMap.put("path", "/content/mysite/us/en");
            predicateMap.put("1_property", "sling:resourceType");
            predicateMap.put("1_property.value", "mysite/components/coursecard");

            // This is the main change: using a direct 'like' operation
            predicateMap.put("2_property", "courseTitle");
            predicateMap.put("2_property.value", "%" + searchTerm + "%");
            predicateMap.put("2_property.operation", "like");

            predicateMap.put("p.limit", "10");

            Query query = queryBuilder.createQuery(PredicateGroup.create(predicateMap), session);
            SearchResult result = query.getResult();

            for (Hit hit : result.getHits()) {
                try {
                    Resource hitResource = hit.getResource();
                    String title = hitResource.getValueMap().get("courseTitle", String.class);
                    String imagePath = hitResource.getValueMap().get("courseImage", String.class);
                    String pagePath = hit.getResource().getParent().getParent().getPath(); // Assumes component is 2 levels below page content

                    JSONObject resultObject = new JSONObject();
                    resultObject.put("title", title);
                    resultObject.put("imagePath", imagePath);
                    resultObject.put("pagePath", pagePath + ".html");
                    resultsArray.put(resultObject);

                } catch (RepositoryException | JSONException e) {
                    LOG.error("Error processing search hit", e);
                }
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(resultsArray.toString());
    }
}