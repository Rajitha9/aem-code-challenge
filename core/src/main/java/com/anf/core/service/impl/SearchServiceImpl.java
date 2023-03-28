package com.anf.core.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;

import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anf.core.service.SearchService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.NameConstants;

@Component(service = SearchService.class, immediate = true)
public class SearchServiceImpl implements SearchService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);

	@Reference
	QueryBuilder queryBuilder;

	@Override
	public Iterator<Resource> findPagesWithProperty(String basePath, String property, String limit, Session session) {

		Map<String, String> params = new HashMap<>();
		params.put("type", NameConstants.NT_PAGE);
		params.put("path", basePath);
		params.put("property", property);
		params.put("property.operation", "exists");
		params.put("p.limit", limit);
		Query query = queryBuilder.createQuery(PredicateGroup.create(params), session);
		SearchResult result = query.getResult();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Found [ {} ] Pages under [ {} ] that contains property {}", result.getHits().size(), basePath,
					property);
		}

		LOGGER.info("Query {}", result.getQueryStatement());
		return result.getResources();
	}

	@Override
	public NodeIterator findPagesWithProperty(String basePath, String property, int limit, Session session) {

		NodeIterator nodeIterator = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM [cq:Page] AS node WHERE ISDESCENDANTNODE([").append(basePath)
					.append("]) and node.[").append(property).append("] IS NOT NULL");

			String queryStr = sb.toString();
			QueryManager queryManager = session.getWorkspace().getQueryManager();
			javax.jcr.query.Query query = queryManager.createQuery(queryStr, javax.jcr.query.Query.JCR_SQL2);
			query.setLimit(limit);
			nodeIterator = query.execute().getNodes();
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException occurred while executing query ", e);
		}
		return nodeIterator;
	}

}
