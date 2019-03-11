package com.rvellozo.ldap.bulk.importer.service.impl;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.rvellozo.ldap.bulk.importer.service.BindingService;
import com.rvellozo.ldap.bulk.importer.service.SearchService;


@Service("searchService")
public class SearchServiceOpenLdapImpl implements SearchService {
	private int searchCounter;
	private final static Logger logger = Logger.getLogger(SearchServiceOpenLdapImpl.class);
	private BindingService bindingService;
	
	public SearchServiceOpenLdapImpl(BindingService bindingService) {
		this.bindingService = bindingService;
	}
	
	public String search(String dn, String filter) throws InvalidSearchFilterException, NamingException {
		String fullDN = null;
		StringBuilder sb  = new StringBuilder();
		try {
			SearchControls ctls = new SearchControls();	
			// To search for all objects under the given base
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);	
			NamingEnumeration<?> answer = bindingService.getContext().search(dn, filter, ctls);
			searchCounter = 0;
			while (answer.hasMore()) {
				SearchResult sr = (SearchResult) answer.next();
				fullDN = sr.getNameInNamespace() + "\n";		
				sb.append(fullDN);
				sb.append("\n");	
				searchCounter++;
			}			
		} catch (InvalidSearchFilterException isf) {
			logger.debug(isf.getMessage());
			throw new NamingException("Search failed - see log!");
					}
		 catch (NamingException ne) {
			 logger.debug(ne.getMessage());
				throw new NamingException("Search failed - see log!");
		}
		this.setCounter(searchCounter);
		return sb.toString();
	}

	public void setCounter(int searchCounter) {
		this.searchCounter = searchCounter;
		
	}

	public String getSearchCounter() {
		String str = Integer.toString(this.searchCounter);
		return str;
	}

}
