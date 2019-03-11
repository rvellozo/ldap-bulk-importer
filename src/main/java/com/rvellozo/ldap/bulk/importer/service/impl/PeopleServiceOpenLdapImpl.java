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
@Service("peopleServiceOpenLdap")
public class PeopleServiceOpenLdapImpl implements PeopleService {
	
	// external props
	@Value("${ldap.attribute.given.name}")
	private String givenName;
	
	@Value("${ldap.attribute.sn}")
	private String sn;
	
	@Value("${ldap.attribute.o}")
	private String org;
	
	@Value("${ldap.attribute.mail}")
	private String mail;
	
	@Value("${ldap.attribute.userPassword}")
	private String userPassword;
	
	
	private final static Logger logger = Logger.getLogger(PeopleServiceOpenLdapImpl.class);
	
	BindingService bindingService;
	
	public PeopleServiceOpenLdapImpl(BindingService bindingService) {
		this.bindingService = bindingService;
	}
	
	public String[] createUsers(String peopleDN, int numOfUsers,String userNamePrefix) {	
			String[] peopleArr = null;
			String userId = null;
			try {
				Attributes attrs = new BasicAttributes();
				Attribute attr = new BasicAttribute("objectClass");
				attr.add("inetOrgPerson");
				attr.add("organizationalPerson");
				attr.add("top");
				attr.add("person");
				attrs.put(attr);
				attrs.put("sn", this.sn);
				attrs.put("cn", this.givenName + " " + this.sn);
				// Matching our personAttributeMapping
				attrs.put("o", this.org);
				attrs.put("mail", this.mail);
				attrs.put("userPassword",this.userPassword);
				attrs.put("givenName",this.givenName);

				peopleArr = new String[numOfUsers];
				for (int index = 1; index <= numOfUsers; index++) {
					userId = userNamePrefix + LdapImporterUtils.generateUniqueID();	
					bindingService.getContext().createSubcontext("uid=" + userId + "," + peopleDN, attrs);
					peopleArr[index - 1] = "uid=" + userId + "," + peopleDN;
				}

			      } catch (NamingException ne) {
				logger.debug(ne.getMessage());
			}		
			return peopleArr;
		}
	}

