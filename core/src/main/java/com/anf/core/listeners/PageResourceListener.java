package com.anf.core.listeners;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ResourceChangeListener.class, immediate = true, property = {
		ResourceChangeListener.PATHS + "=/content/anf-code-challenge/us/en", ResourceChangeListener.CHANGES + "=ADDED",
		ResourceChangeListener.PROPERTY_NAMES_HINT + "=jcr:title" })
@ServiceDescription("Save a property on creation of Page")
public class PageResourceListener implements ResourceChangeListener {

	private static final Logger LOG = LoggerFactory.getLogger(PageResourceListener.class);

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Override
	public void onChange(List<ResourceChange> changes) {

		changes.forEach(change -> {
			String pagePath = change.getPath();
			LOG.debug("Page Path {}", pagePath);
			if (pagePath.endsWith("jcr:content")) {
				addPageCreatedToPage(pagePath);
			}

		});

	}

	private void addPageCreatedToPage(String pagePath) {
		final Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE,
				(Object) "getResourceResolver");

		try (ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(param)) {
			Resource res = resolver.getResource(pagePath);
			if (null != res) {
				ModifiableValueMap properties = res.adaptTo(ModifiableValueMap.class);
				properties.put("pageCreated", true);
				resolver.commit();
				LOG.info("CREATE pagecreated property FOR page {}", pagePath);
			}

		} catch (PersistenceException | LoginException e) {
			LOG.error("Can't save pageId on page {}", pagePath);
		}
	}
}
