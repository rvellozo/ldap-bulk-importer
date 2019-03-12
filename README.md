# ldap-bulk-importer - What for?

The main purpose of this tool is to test Alfresco LDAP sync, for a large data set of users and groups, on a UAT system
first before triggering the sync on a production environment.

The tool allows you to quickly bulk create users and groups on a LDAP server (OpenLdap and Active Directory) to be used
and tested against an Alfresco instance configured with LDAP sync.

# Prerequisites
* [Maven](https://maven.apache.org/download.cgi)
* [JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Git](https://git-scm.com/downloads)
* [Spring Tool Suite](https://spring.io/tools3/sts/all)

# Running the tool
* Assuming home directory, run the commands:
```
  # git clone https://github.com/rvellozo/ldap-bulk-importer.git
  # cd ldap-bulk-importer
  # mvn clean package
  # java -jar target/ldap-bulk-importer-0.0.1-SNAPSHOT.jar
```

# Using the tool

### The Connect tab

<img src="/src/main/resources/img/connect.png" >

* Check you can connect to your LDAP server
* The Authenticate, Users/Groups and Search tabs will be disabled until you have successfully connected to the LDAP server

### The Authenticate tab

<img src="/src/main/resources/img/authenticate.png">

* Check you can authenticate to your LDAP server
* The Users/Groups and Search tabs will be disabled until you have successfully authenticated to the LDAP server

## The Users and Groups tab
<img src="/src/main/resources/img/users-groups.png" width="400">

* Enter the number of groups and users to be created
* Users are created as they're added as a member of group, that is, for each group created, it will create 5 users, and in this case it   will create a total of 25 users - 5 users in each 
group.

* The prefix is there to allow special characters to be added to user/group name - testing purpose.
* The tool has been used to test a scenario of 10.000 users.
  
## The Search tab

<img src="/src/main/resources/img/search.png">

* The search has only been implemented for OpenLdap, a new implementation for AD is required.
* For convenience, the Search Base defaults to the Base DN and the search filter to the Groups and Users OUs.
* This is to allow a quick search to find the Group and People OUs created by the tool.
* The result can then be copied and pasted to Alfresco Group Search and User Search Base fields as img below:

## Alfresco Directory Management page

* _../alfresco/service/enterprise/admin/admin-directorymanagement_

<img src="/src/main/resources/img/alf-dir-mng.png">

# Final result at OpenLDAP server

<img src="/src/main/resources/img/ldap-server.png">

# Notes
* Once the Alfresco LDAP sync is completed, if you need to login with one of the synchronized users:
  * For OpenLDAP use password 'secret', see application.properties file
  * For Active Directory, users were created without password, reset the user password at Active Directory level then trigger Alfresco     LDAP sync.
