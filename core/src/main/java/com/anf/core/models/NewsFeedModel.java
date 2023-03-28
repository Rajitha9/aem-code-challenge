package com.anf.core.models;

import com.anf.core.beans.News;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsFeedModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewsFeedModel.class);

	@ValueMapValue
	private String rootPath;

	@SlingObject
	ResourceResolver resolver;

	private String date;

	@PostConstruct
	protected void init() {
		date = getCurrentDate();
	}

	public List<News> getNewsFeed() {
		LOGGER.debug("Inside getNewsFeed");
		List<News> newsList = new ArrayList<>();
		if (StringUtils.isNotEmpty(rootPath)) {
			Resource rootResource = resolver.getResource(rootPath);
			if (null != rootResource) {
				Iterator<Resource> iterator = rootResource.listChildren();
				while (iterator.hasNext()) {
					Resource newsResource = iterator.next();
					ValueMap properties = newsResource.getValueMap();
					News news = new News();
					news.setAuthor(properties.get("author", String.class));
					news.setDescription(properties.get("description", String.class));
					news.setImage(properties.get("urlImage", String.class));
					news.setTitle(properties.get("title", String.class));
					news.setPagePath(properties.get("url", String.class));
					news.setCurrentDate(date);
					newsList.add(news);
				}
			}
		}
		return newsList;
	}

	private String getCurrentDate() {

		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		Date todaysDate = new Date();
		return formatter.format(todaysDate);

	}

}
