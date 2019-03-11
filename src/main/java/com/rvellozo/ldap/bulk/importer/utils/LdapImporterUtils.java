package com.rvellozo.ldap.bulk.importer.utils;

import java.util.UUID;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
// import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.rvellozo.ldap.bulk.importer.service.BindingService;


@Component("ldapImporterUtils")
public class LdapImporterUtils {
// private final static Logger logger = Logger.getLogger(LdapImporterUtils.class);
private static BindingService bindingService;

public LdapImporterUtils(BindingService bindingService) {
	LdapImporterUtils.bindingService=bindingService;
	
}	
    // Gets the BaseDn to be used as a starting point for searches.
	public static String getBaseDN() {
		String baseDN = null;
		try {
			Attributes atttrs = bindingService.getContext().getAttributes("", new String[] { "namingContexts" });
			baseDN = atttrs.get("namingContexts").get(0).toString();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return baseDN;
	}
	
	
	// Generates unique IDs for OUs, users and groups
	public static String generateUniqueID() {
		String uniqueId = UUID.randomUUID().toString().substring(24);
		return uniqueId;
	}
}
