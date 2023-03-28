package com.anf.core.models;

import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.anf.core.beans.News;
import com.google.common.collect.ImmutableMap;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class NewsFeedModelTest {

	public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

	@InjectMocks
	private NewsFeedModel newsFeedModel;

	@BeforeEach
	void Setup() throws NoSuchFieldException, RepositoryException {
		context.addModelsForClasses(NewsFeedModel.class);
		Resource newsFeedResourceContext = context.create().resource("/content/anf-code-challenge/jcr:content/newsFeed",
				new ValueMapDecorator(
						ImmutableMap.<String, Object>of("rootPath", "/content/anf-code-challenge/newsFeeds")));

		context.create().resource("/content/anf-code-challenge/newsFeeds/node1",
				new ValueMapDecorator(ImmutableMap.<String, Object>builder().put("jcr:title", "News Feeds Page 1")
						.put("author", "rajitha").put("description", "test").put("urlImage", "/content/dam/asset.jpg")
						.put("title", "news feed page 1").put("url", "/content/anf-code-challenge/newsFeeds/page1")
						.build()));

		context.create().resource("/content/anf-code-challenge/newsFeeds/node2",
				new ValueMapDecorator(ImmutableMap.<String, Object>builder().put("jcr:title", "News Feeds Page 2")
						.put("author", "srini").put("description", "test1").put("urlImage", "/content/dam/asset1.jpg")
						.put("title", "news feed page 2").put("url", "/content/anf-code-challenge/newsFeeds/page2")
						.build()));
		newsFeedModel = newsFeedResourceContext.adaptTo(NewsFeedModel.class);
	}

	@Test
	void testGetNewsFeed() {
		List<News> newsFeed = newsFeedModel.getNewsFeed();
		Assertions.assertEquals(2, newsFeed.size());
	}
}
