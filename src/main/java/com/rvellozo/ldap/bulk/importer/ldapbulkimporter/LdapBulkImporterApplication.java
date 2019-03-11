package com.rvellozo.ldap.bulk.importer.ldapbulkimporter;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class LdapBulkImporterApplication {

	   public static void main(String[] args) {
	        new SpringApplicationBuilder(LdapBulkImporterApplication.class)
	                .headless(false) // required for AWT app
	                .web(WebApplicationType.NONE) // no need for a web app
	                .run("--spring.main.allow-bean-definition-overriding=true");
	    }
	}
