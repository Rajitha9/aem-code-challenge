package com.anf.core.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes = "anf-code-challenge/components/page", methods = HttpConstants.METHOD_GET, selectors = "countries", extensions = "json")
@ServiceDescription("Countries Servlet")
public class CountriesServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		ResourceResolver resolver = req.getResourceResolver();
		Resource resource = resolver.getResource("/content/dam/anf-code-challenge/exercise-1/countries.json");
		Asset asset = resource.adaptTo(Asset.class);
		Rendition original = asset.getOriginal();
		InputStream content = original.adaptTo(InputStream.class);
		StringBuilder sb = new StringBuilder();
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(content, StandardCharsets.UTF_8));

		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		JsonObject convertedObject = new Gson().fromJson(sb.toString(), JsonObject.class);
		resp.getWriter().println(convertedObject);
	}

}
