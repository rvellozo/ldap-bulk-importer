package com.rvellozo.ldap.bulk.importer.service.impl;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;


import org.springframework.stereotype.Service;
import org.apache.log4j.Logger;

import com.rvellozo.ldap.bulk.importer.service.AuthorityService;
import com.rvellozo.ldap.bulk.importer.service.BindingService;
import com.rvellozo.ldap.bulk.importer.service.PeopleService;
import com.rvellozo.ldap.bulk.importer.utils.LdapImporterUtils;


@Service("authorityServiceActiveDirectory")
public class AuthorityServiceActiveDirectoryImpl implements AuthorityService{
	
	private PeopleService peopleServiceActiveDirectory;
	private BindingService bindingService;
	
	public AuthorityServiceActiveDirectoryImpl(PeopleService peopleServiceActiveDirectoryImpl, BindingService bindingService) {
		this.peopleServiceActiveDirectory = peopleServiceActiveDirectoryImpl;
		this.bindingService = bindingService;
	}
	
	private final static Logger logger = Logger.getLogger(AuthorityServiceActiveDirectoryImpl.class);
	
	public void createGroups(String rootGroupsOU, int numOfGroups,int numOfUsers, String rootPeopleOU, String groupNamePrefix,String userNamePrefix) throws NamingException {
		String[] usersDN = null;
		String groupName = null;
	
			Attributes attrs = new BasicAttributes();
			Attribute member = new BasicAttribute("member");
			groupName = groupNamePrefix + LdapImporterUtils.generateUniqueID();
			for (int index = 1; index <= numOfUsers; index++) {
				try{
				usersDN = peopleServiceActiveDirectory.createUsers(rootPeopleOU, numOfUsers,userNamePrefix);
				}
				catch (NamingException ne){
					logger.debug(ne.getMessage());
				}
				// Creates membership with the newly created users above
				member.add(usersDN[index - 1]);
			}		
			// Populate attributes
			attrs.put("objectClass", "group");
			attrs.put("sAMAccountName", groupName);
			attrs.put("cn", groupName);
			attrs.put("groupType", "-2147483646"); // Global security group
			attrs.put(member);
			try {
				bindingService.getContext().createSubcontext("CN=" + groupName + "," + rootGroupsOU, attrs);
		} catch (NamingException ne) {	
		    logger.debug(ne.getMessage());
		    throw new NamingException("Creating Active Directory groups failed");
		}
		
	}
	
}
