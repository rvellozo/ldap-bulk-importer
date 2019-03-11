package com.rvellozo.ldap.bulk.importer.service;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import org.springframework.stereotype.Service;

@Service
public interface BindingService {
	public void connect(String ip, String port) throws NamingException;
	public void authenticate(String principal, String credential) throws NamingException;
	public DirContext getContext();

}
