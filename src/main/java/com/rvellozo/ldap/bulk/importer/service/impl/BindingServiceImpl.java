package com.rvellozo.ldap.bulk.importer.service.impl;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.rvellozo.ldap.bulk.importer.service.BindingService;

@Component("bindingService")
public class BindingServiceImpl implements BindingService {
	
	private String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private Hashtable<Object, String> env = new Hashtable<Object, String>(4);
	private String PROTOCOL_FILLER = "ldap://";
	
	private String SECURITY_AUTHENTICATION = "Simple";
	private DirContext ctx;
	private final static Logger logger = Logger.getLogger(BindingServiceImpl.class);

	@Override
	public void connect(String ip, String port) throws NamingException {
	    env.put(Context.INITIAL_CONTEXT_FACTORY,this.INITIAL_CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL, this.PROTOCOL_FILLER + ip + ":" + port);
			try {
				this.ctx = new InitialDirContext(env);
			} catch (NamingException e) {
				// To be caught by the UI
				throw new NamingException("Failed to connnect to the LDAP server!");
			}
		
	}

	@Override
	public void authenticate(String principal, String credential) throws NamingException {
		this.env.put(Context.SECURITY_PRINCIPAL, principal);
		this.env.put(Context.SECURITY_CREDENTIALS, credential);
		this.env.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
		this.env.put(Context.REFERRAL, "follow");
		try {			
			this.ctx = new InitialDirContext(this.env);		
		} catch (NamingException ne) {			
			logger.debug(ne.getMessage());
			// To be caught by the UI
			throw new NamingException("Invalid Credentials!");
			
		}
		
	}

	@Override
	public DirContext getContext() {
		return this.ctx;
	}

}
