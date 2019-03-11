package com.rvellozo.ldap.bulk.importer.service;

import javax.naming.NamingException;
import javax.naming.directory.InvalidSearchFilterException;
import org.springframework.stereotype.Service;

@Service
public interface SearchService {
	public String search(String dn, String filter) throws InvalidSearchFilterException, NamingException;
	public void setCounter(int searchCounter);
	public String getSearchCounter();
}
