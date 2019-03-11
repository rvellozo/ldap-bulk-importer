package com.rvellozo.ldap.bulk.importer.ui;

import javax.naming.Context;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.rvellozo.ldap.bulk.importer.service.AuthorityService;
import com.rvellozo.ldap.bulk.importer.service.BindingService;
import com.rvellozo.ldap.bulk.importer.utils.LdapImporterUtils;

@Configuration
@PropertySource("classpath:/application.properties")
@Component("userInterfaceManager")
public class UserInterfaceManager {
	
	// external props
	@Value("${top.level.groups.OU.name}")
	private String topLevelGroupsName;
	
	@Value("${top.level.users.OU.name}")
	private String topLevelUsersName;

	private final static Logger logger = Logger.getLogger(UserInterfaceManager.class);
	
	private AuthorityService authorityServiceOpenLdap;
	private AuthorityService authorityServiceActiveDirectory;
	private BindingService bindingService;
	
	public UserInterfaceManager(BindingService bindingService, AuthorityService authorityServiceOpenLdap, AuthorityService authorityServiceActiveDirectory) {
		this.authorityServiceOpenLdap=authorityServiceOpenLdap;
		this.authorityServiceActiveDirectory=authorityServiceActiveDirectory;
		this.bindingService = bindingService;
	}

    public void createStructure(int numOfGroups, String groupNamePrefix, int numOfUsers, String userNamePrefix, String serverType) throws NamingException{
		String baseDN = LdapImporterUtils.getBaseDN();
		Context groups,people;
		
		String generatedGroupId = this.topLevelGroupsName + LdapImporterUtils.generateUniqueID();
		String generatedPeopleId = this.topLevelUsersName + LdapImporterUtils.generateUniqueID();
		
		try {
			
			Attributes attrs = new BasicAttributes(true);
			Attribute attr = new BasicAttribute("objectclass");
			attr.add("top");
			attr.add("organizationalUnit");
			attrs.put(attr);
			// Creates top level OUs for Groups and People
			groups = bindingService.getContext().createSubcontext("ou=" + generatedGroupId + "," + baseDN, attrs);
			people = bindingService.getContext().createSubcontext("ou=" + generatedPeopleId + "," + baseDN, attrs);
			this.createGroupsAndUsers(groups.getNameInNamespace().toString(), numOfGroups, numOfUsers, people.getNameInNamespace().toString(), groupNamePrefix, userNamePrefix, serverType);

		} catch (NamingException ne) {
		    logger.debug(ne.getMessage());
		}
		   
     } 
	
	// Delegates the creation of groups and users
	public void createGroupsAndUsers(String rootGroupsOU, int numOfGroups, int numOfUsers, String rootPeopleOU, String groupNamePrefix,String userNamePrefix, String serverType) throws NamingException {
		if (serverType == "OpenLDAP")
			for (int index = 1; index <= numOfGroups; index++) {
				authorityServiceOpenLdap.createGroups(rootGroupsOU, numOfGroups, numOfUsers, rootPeopleOU, groupNamePrefix, userNamePrefix);
			}
		// if Active Directory
		else
			for (int index = 1; index <= numOfGroups; index++) {
				authorityServiceActiveDirectory.createGroups(rootGroupsOU, numOfGroups, numOfUsers, rootPeopleOU, groupNamePrefix, userNamePrefix);
			}
	}
}