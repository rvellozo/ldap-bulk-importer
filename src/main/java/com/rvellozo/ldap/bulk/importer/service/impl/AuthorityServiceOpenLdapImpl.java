package com.rvellozo.ldap.bulk.importer.service.impl;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.rvellozo.ldap.bulk.importer.service.AuthorityService;
import com.rvellozo.ldap.bulk.importer.service.BindingService;
import com.rvellozo.ldap.bulk.importer.service.PeopleService;
import com.rvellozo.ldap.bulk.importer.utils.LdapImporterUtils;


@Service("authorityServiceOpenLdap")
public class AuthorityServiceOpenLdapImpl implements AuthorityService{
	
	private PeopleService peopleServiceOpenLdap;
	private BindingService bindingService;
	
	
	private final static Logger logger = Logger.getLogger(AuthorityServiceOpenLdapImpl.class);
	
	public AuthorityServiceOpenLdapImpl(PeopleService peopleServiceOpenLdap, BindingService bindingService) {
		this.peopleServiceOpenLdap = peopleServiceOpenLdap;
		this.bindingService = bindingService;
	}

	public void createGroups(String rootGroupsOU, int numOfGroups, int numOfUsers, String rootPeopleOU, String groupNamePrefix,	String userNamePrefix) throws NamingException {
		String[] usersDN = null;
		String groupName = null;

		Attributes attrs = new BasicAttributes();
		Attribute attr = new BasicAttribute("objectClass");
		attr.add("groupOfNames");
		attr.add("top");
		attrs.put(attr);
		Attribute member = new BasicAttribute("member");
		usersDN = peopleServiceOpenLdap.createUsers(rootPeopleOU, numOfUsers, userNamePrefix);
		for (String members : usersDN) {
			// Creates membership with the newly created users above
			member.add(members);
		}
		attrs.put(member);
		try {
			groupName = groupNamePrefix + LdapImporterUtils.generateUniqueID();
			bindingService.getContext().createSubcontext("cn=" + groupName + "," + rootGroupsOU, attrs);
		} catch (NamingException ne){
			logger.debug(ne.getMessage());
		  }
		
	}

}
