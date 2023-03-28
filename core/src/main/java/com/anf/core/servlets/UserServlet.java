package com.anf.core.servlets;

import com.day.cq.commons.jcr.JcrConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes = "anf-code-challenge/components/page", methods = HttpConstants.METHOD_POST, selectors = "user", extensions = "txt")
@ServiceDescription("User Response Servlet")
public class UserServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServlet.class);

	private static final String BASE_PATH = "/var/anf-code-challenge";

	@Override
	protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {

		String ageString = req.getParameter("age");
		int age = Integer.parseInt(ageString);
		ResourceResolver resolver = req.getResourceResolver();

		Resource varAgeResource = resolver.getResource("/etc/age");
		if (null != varAgeResource) {

			ValueMap ageProperties = varAgeResource.getValueMap();
			int minAge = ageProperties.get("minAge", Integer.class);
			int maxAge = ageProperties.get("maxAge", Integer.class);
			if (age >= minAge && age <= maxAge) {
				LOGGER.debug("Age is correct");
				String firstName = req.getParameter("firstName");
				String lastName = req.getParameter("lastName");
				String country = req.getParameter("country");
				String resourcePath = StringUtils.join(BASE_PATH, "/", country, "/", firstName, "_", lastName);

				Map<String, Object> userProperties = new HashMap<>();
				userProperties.put("firstname", firstName);
				userProperties.put("lastname", lastName);
				userProperties.put("country", country);
				userProperties.put("age", age);
				userProperties.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);

				Resource anfCodeResource = ResourceUtil.getOrCreateResource(resolver, resourcePath, userProperties,
						JcrResourceConstants.NT_SLING_FOLDER, true);
				resp.setContentType("text/plain");
				resp.getWriter().write("Anf code Resource " + anfCodeResource.getPath());
				resp.setStatus(HttpServletResponse.SC_OK);
			} else {
				LOGGER.debug("Age is incorrect");
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}

	}
}
