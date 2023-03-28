package com.anf.core.models;

import com.anf.core.service.SearchService;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SearchAnfPageModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchAnfPageModel.class);

	@ValueMapValue
	@Default(values = "/content/anf-code-challenge/us/en")
	private String searchIn;

	@ValueMapValue
	@Default(values = "anfCodeChallenge")
	private String propertyName;

	@ValueMapValue
	private String maxItems;

	@OSGiService
	SearchService service;

	@SlingObject
	ResourceResolver resolver;

	public List<Page> getPagesUsingQueryBuilder() {

		List<Page> pageList = new ArrayList<>();
		Session session = resolver.adaptTo(Session.class);
		String property = StringUtils.join(NameConstants.NN_CONTENT, "/", propertyName);
		Iterator<Resource> iterator = service.findPagesWithProperty(searchIn, property, maxItems, session);
		while (iterator.hasNext()) {
			Resource pageResource = iterator.next();
			Page page = pageResource.adaptTo(Page.class);
			if (null != page) {
				pageList.add(page);
			}
		}
		return pageList;
	}

	public List<Page> getPagesUsingSql2() {

		List<Page> pageList = new ArrayList<>();
		Session session = resolver.adaptTo(Session.class);
		String property = StringUtils.join(NameConstants.NN_CONTENT, "/", propertyName);
		int limit = Integer.parseInt(maxItems);
		NodeIterator iterator = service.findPagesWithProperty(searchIn, property, limit, session);
		PageManager pm = resolver.adaptTo(PageManager.class);
		while (iterator.hasNext()) {
			Node pageNode = iterator.nextNode();
			try {
				String pagePath = pageNode.getPath();
				if (StringUtils.isNotEmpty(pagePath)) {
					Page page = pm.getPage(pagePath);
					if (null != page) {
						pageList.add(page);
					}
				}
			} catch (RepositoryException e) {
				LOGGER.error("Repository Exception ", e);
			}

		}
		return pageList;
	}

}
