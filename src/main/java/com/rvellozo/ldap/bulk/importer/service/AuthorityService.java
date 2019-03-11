package com.rvellozo.ldap.bulk.importer.service;

import javax.naming.NamingException;

import org.springframework.stereotype.Service;

@Service
public interface AuthorityService {
	public void createGroups(String rootGroupsOU, int numOfGroups,int numOfUsers, String rootPeopleOU, String groupNamePrefix, String userNamePrefix)throws NamingException;
}
