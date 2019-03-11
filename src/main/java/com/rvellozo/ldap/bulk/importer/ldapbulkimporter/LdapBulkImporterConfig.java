package com.rvellozo.ldap.bulk.importer.ldapbulkimporter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rvellozo.ldap.bulk.importer.service.AuthorityService;
import com.rvellozo.ldap.bulk.importer.service.BindingService;
import com.rvellozo.ldap.bulk.importer.service.PeopleService;
import com.rvellozo.ldap.bulk.importer.service.SearchService;
import com.rvellozo.ldap.bulk.importer.service.impl.AuthorityServiceActiveDirectoryImpl;
import com.rvellozo.ldap.bulk.importer.service.impl.AuthorityServiceOpenLdapImpl;
import com.rvellozo.ldap.bulk.importer.service.impl.BindingServiceImpl;
import com.rvellozo.ldap.bulk.importer.service.impl.PeopleServiceActiveDirectoryImpl;
import com.rvellozo.ldap.bulk.importer.service.impl.PeopleServiceOpenLdapImpl;
import com.rvellozo.ldap.bulk.importer.service.impl.SearchServiceOpenLdapImpl;
import com.rvellozo.ldap.bulk.importer.ui.UserInterface;
import com.rvellozo.ldap.bulk.importer.ui.UserInterfaceManager;
import com.rvellozo.ldap.bulk.importer.utils.LdapImporterUtils;

@Configuration
public class LdapBulkImporterConfig {
	
	    //Beans declaration
	
	    @Bean
	    public UserInterface userInterface() {
	        return new UserInterface(this.bindingService(), this.userManager(), this.searchService());
	    }
	    
	    @Bean
	    public UserInterfaceManager userManager() {
	        return new UserInterfaceManager(this.bindingService(), this.authorityServiceOpenLdap(), this.authorityServiceActiveDirectory());
	    }

	    @Bean
	    public BindingService bindingService() {
	      return new BindingServiceImpl();
	    } 
	    
	    @Bean
	    public AuthorityService authorityServiceOpenLdap() {
	        return (AuthorityService) new AuthorityServiceOpenLdapImpl(this.peopleServiceOpenLdap(), this.bindingService());
	    }  
	    
	    @Bean
	    public AuthorityService authorityServiceActiveDirectory() {
	        return (AuthorityService) new AuthorityServiceActiveDirectoryImpl(this.peopleServiceActiveDirectory(), this.bindingService());
	    } 
	    
	    @Bean
	    public PeopleService peopleServiceOpenLdap() {
	        return (PeopleService) new PeopleServiceOpenLdapImpl(this.bindingService());
	    }
	    
	    
	    @Bean
	    public PeopleService peopleServiceActiveDirectory() {
	        return (PeopleService) new PeopleServiceActiveDirectoryImpl(this.bindingService());
	    } 
	    
	    
	    @Bean
	    public SearchService searchService() {
	        return new SearchServiceOpenLdapImpl(this.bindingService());
	    } 
		
	    @Bean
	    public LdapImporterUtils ldapImpoerterUtils() {
	        return new LdapImporterUtils(this.bindingService());
	    } 	

}
