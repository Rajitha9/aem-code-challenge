package com.anf.core.service;

import java.util.Iterator;

import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;

public interface SearchService {

	Iterator<Resource> findPagesWithProperty(String basePath, String propertyName, String limit, Session session);

	NodeIterator findPagesWithProperty(String basePath, String propertyName, int limit, Session session);
}
