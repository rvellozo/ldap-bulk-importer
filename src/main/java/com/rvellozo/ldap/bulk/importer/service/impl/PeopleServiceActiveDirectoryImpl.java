package com.rvellozo.ldap.bulk.importer.service.impl;

import javax.naming.NamingException;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.rvellozo.ldap.bulk.importer.service.BindingService;
import com.rvellozo.ldap.bulk.importer.service.PeopleService;
import com.rvellozo.ldap.bulk.importer.utils.LdapImporterUtils;

@Configuration
@PropertySource("classpath:/application.properties")
@Service("peopleServiceActiveDirectory")
public class PeopleServiceActiveDirectoryImpl implements PeopleService {
	
	private final static Logger logger = Logger.getLogger(PeopleServiceActiveDirectoryImpl.class);
	private BindingService bindingService;
	
	// external props
	@Value("${ldap.attribute.given.name}")
	private String givenName;
	
	@Value("${ldap.attribute.sn}")
	private String sn;
	
	@Value("${ldap.attribute.o}")
	private String org;
	
	@Value("${ldap.attribute.mail}")
	private String mail;
	
	
	public PeopleServiceActiveDirectoryImpl(BindingService bindingService) {
		this.bindingService = bindingService;
	}	

	public String[] createUsers(String peopleDN, int numOfUsers, String userNamePrefix)throws NamingException {
	
		String[] peopleArr = null;
		String userName = null;
		try {
			Attributes attrs = new BasicAttributes();
			Attribute attr = new BasicAttribute("objectClass");
			attr.add("organizationalPerson");
			attr.add("person");
			attr.add("top");
			attr.add("user");
			attrs.put(attr);

			userName = userNamePrefix + LdapImporterUtils.generateUniqueID();

			// Matching our personAttributeMapping
			attrs.put("givenName",this.givenName);
			attrs.put("sn", this.sn);
			attrs.put("o", this.org);
			attrs.put("mail", this.mail);
			attrs.put("sAMAccountName", userName);
			attrs.put("userAccountControl", "544"); // NORMAL_ACCOUNT

			peopleArr = new String[numOfUsers];
			bindingService.getContext().createSubcontext("CN=" + userName + "," + peopleDN, attrs);
			for (int index = 1; index <= numOfUsers; index++) {
				peopleArr[index - 1] = "CN=" + userName + "," + peopleDN;
			}

		} catch (NamingException ne) {
			logger.debug(ne.getMessage());
		}
		return peopleArr;
	}

}
