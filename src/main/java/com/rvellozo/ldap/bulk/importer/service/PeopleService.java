package com.rvellozo.ldap.bulk.importer.service;

import javax.naming.NamingException;

import org.springframework.stereotype.Service;

@Service
public interface PeopleService{
	public String[] createUsers(String peopleDN, int numOfUsers, String userNamePrefix) throws NamingException;
}
