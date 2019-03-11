package com.rvellozo.ldap.bulk.importer.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.naming.NamingException;
import javax.naming.directory.InvalidSearchFilterException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.rvellozo.ldap.bulk.importer.service.BindingService;
import com.rvellozo.ldap.bulk.importer.service.SearchService;
import com.rvellozo.ldap.bulk.importer.utils.LdapImporterUtils;

@Configuration
@Component("userInterface")
@PropertySource("classpath:/application.properties")
public class UserInterface extends JFrame {
	
	private static final long serialVersionUID = 1L;	
	
	// Injecting services
	private BindingService bindingService;
	private SearchService searchService;
	UserInterfaceManager userInterfaceManager;

	// main Frame
	JTabbedPane tabsPane;

	
	JFrame mainFrame;
	
	// Connection tab
	private JTextField conn_ip_field, conn_port_field;
	private JLabel conn_ip_lbl, conn_status_lbl, conn_port_lbl, conn_details_lbl, conn_server_type_lbl;
	private JButton conn_test_btn;
	private JPanel connPanel;
	private JRadioButton conn_openldap_radiobtn,conn_ad_radiobtn;
	private ButtonGroup conn_btnGroup;

	// Authentication tab
	private JTextField auth_bind_dn_field, auth_bind_passwd_field;
	private JLabel auth_bind_dn_lbl, auth_bind_passwd_lbl, auth_status_lbl, auth_header_lbl, auth_method_lbl;
	private JPanel authPanel;
	private JButton auth_test_btn;
	private JCheckBox auth_type_chkbox;

	// Search tab
	private JLabel search_base_lbl, search_filter_lbl, search_result_lbl, search_base_dn_lbl;
	private JTextField search_base_field, search_filter_field;
	private JTextArea search_result_textArea;
	private JPanel searchPanel;
	private JButton search_btn;
	private JScrollPane scrollPane;
	
	// Users and Groups tab
	private JPanel strPanel;	
	private JLabel title_lbl, numofGroups_lbl, groupId_lbl, 
	               numOfUsers_lbl, userId_lbl, server_type_lbl, status_lbl;	
	private JTextField numOfGroups_field, groupId_field, 
	                   numOfUsers_field, userId_field;	
	private JButton createStructure_btn;
	

	// Constructor to build the UI
	public UserInterface(BindingService bindingService, UserInterfaceManager userInterfaceManager, SearchService searchService) {
	
		this.bindingService = bindingService;
		this.userInterfaceManager = userInterfaceManager;
		this.searchService = searchService;
		this.setTitle("Tabbed Pane");
		tabsPane = new JTabbedPane();
		this.getContentPane().add(tabsPane);

		this.buildConnectionUI();
		this.buildAuthenticationUI();
		this.buildUsersGroupsUI();
		this.buildSearchUI();

		tabsPane.addTab("Connect", connPanel);
		tabsPane.addTab("Authenticate", authPanel);
		tabsPane.addTab("Users and Groups", strPanel);
		tabsPane.addTab("Search", searchPanel);	

		 // Only Connection tab enabled at startup		
		 tabsPane.setEnabledAt(0, true); 
    	 tabsPane.setEnabledAt(1, false);
		 tabsPane.setEnabledAt(2, false); 
		 tabsPane.setEnabledAt(3, false);

		// Main window
		this.setTitle("LDAP Bulk Importer");
		this.setSize(700, 550);
		this.setVisible(true);
	}

	// Builds the Connection tab UI
	public void buildConnectionUI() {
		
		connPanel = new JPanel();
		connPanel.setLayout(null);

		conn_details_lbl = new JLabel();
		conn_details_lbl.setText("LDAP host connection details:");
		conn_details_lbl.setFont(new Font("", Font.BOLD, 15));
		conn_details_lbl.setBounds(100, 0, 300, 60);

		conn_ip_lbl = new JLabel();
		conn_ip_lbl.setText("IP Address:");
		conn_ip_lbl.setBounds(20, 85, 120, 60);

		conn_port_lbl = new JLabel();
		conn_port_lbl.setText("Port:");
		conn_port_lbl.setBounds(20, 125, 90, 60);

		conn_status_lbl = new JLabel();
		conn_status_lbl.setText("");
		conn_status_lbl.setBounds(20, 220, 350, 60);

		conn_ip_field = new JTextField();
		conn_ip_field.setBounds(110, 100, 140, 30);
		conn_ip_field.setToolTipText("IP address of LDAP host");
		conn_ip_field.setEnabled(false);

		conn_port_field = new JTextField();
		conn_port_field.setBounds(110, 140, 140, 30);
		conn_port_field.setText("389");
		
		conn_server_type_lbl = new JLabel();
		conn_server_type_lbl.setText("Choose LDAP server:");
		conn_server_type_lbl.setBounds(20, 60, 150, 33);
		
		conn_openldap_radiobtn = new JRadioButton();
		conn_openldap_radiobtn.setText("OpenLDAP");
		conn_openldap_radiobtn.setBounds(175, 60, 100, 30);
		conn_openldap_radiobtn.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					conn_test_btn.setEnabled(true);
					conn_ip_field.setEnabled(true);
					
				}
			}
		});	
		
		conn_ad_radiobtn = new JRadioButton();
		conn_ad_radiobtn.setText("Active Directory");
		conn_ad_radiobtn.setBounds(275, 60, 150, 30);
		conn_ad_radiobtn.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					conn_test_btn.setEnabled(true);
					conn_ip_field.setEnabled(true);
				}
			}
		});		
		
		conn_btnGroup = new ButtonGroup();
		conn_btnGroup.add(conn_openldap_radiobtn);
		conn_btnGroup.add(conn_ad_radiobtn);	
		conn_test_btn = new JButton("Check network");
		conn_test_btn.setBounds(20, 195, 200, 33);
		conn_test_btn.addActionListener(new ConnectionListener());		
		conn_test_btn.setEnabled(false);
		
		connPanel.add(conn_ip_lbl);
		connPanel.add(conn_ip_field);
		connPanel.add(conn_port_field);
		connPanel.add(conn_port_lbl);
		connPanel.add(conn_test_btn);
		connPanel.add(conn_status_lbl);
		connPanel.add(conn_details_lbl);
		connPanel.add(conn_server_type_lbl);
		connPanel.add(conn_openldap_radiobtn);
		connPanel.add(conn_ad_radiobtn);
	}
	
	// Connection Behaviour
	class ConnectionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				bindingService.connect(conn_ip_field.getText(),	conn_port_field.getText());
				conn_status_lbl.setText("The connection was established successfully.");
				conn_status_lbl.setForeground(Color.blue);
				// Once connected enable the Authentication tab
				tabsPane.setEnabledAt(1, true);
			} catch (Exception ne) {
				conn_status_lbl.setText(ne.getMessage());
				conn_status_lbl.setForeground(Color.red);
			}

		}
	}
	
	// Builds the Authentication tab UI
		public void buildAuthenticationUI() {
			authPanel = new JPanel();
			authPanel.setLayout(null);
			
			auth_header_lbl = new JLabel();
			auth_header_lbl.setText("Principal full DN and password:");
			auth_header_lbl.setFont(new Font("", Font.BOLD, 15));
			auth_header_lbl.setBounds(150, 0, 300, 60);
			
			auth_bind_dn_lbl = new JLabel();
			auth_bind_dn_lbl.setText("Bind DN or user:");
			auth_bind_dn_lbl.setBounds(20, 50, 130, 60);

			auth_bind_dn_field = new JTextField();
			auth_bind_dn_field.setBounds(130, 65, 500, 30);
			auth_bind_dn_field.setToolTipText("<html>OpenLDAP example: <br>cn=admin,dc=example,dc=com<br><br>Active Directory example: <br>Administrator@example.com </html>");

			auth_bind_passwd_lbl = new JLabel();
			auth_bind_passwd_lbl.setText("Bind password:");
			auth_bind_passwd_lbl.setBounds(20, 90, 150, 60);

			// Should use JPasswordField instead
			auth_bind_passwd_field = new JTextField();
			auth_bind_passwd_field.setBounds(130, 105, 150, 30);		

			auth_status_lbl = new JLabel();
			auth_status_lbl.setText("");
			auth_status_lbl.setBounds(20, 210, 350, 60);
			
			auth_method_lbl = new JLabel();
			auth_method_lbl.setText("Authentication Type: ");
			auth_method_lbl.setBounds(20, 130, 250, 60);
			
			auth_type_chkbox = new JCheckBox("Simple");
			auth_type_chkbox.setBounds(150, 126, 100, 70);
			auth_type_chkbox.setSelected(true);
			auth_type_chkbox.setEnabled(false);
			
			
			auth_test_btn = new JButton("Check Authentication");
			auth_test_btn.setBounds(20, 190, 200, 33);
			auth_test_btn.addActionListener(new AuthenticationListener());

			authPanel.add(auth_bind_dn_lbl);
			authPanel.add(auth_bind_dn_field);
			authPanel.add(auth_bind_passwd_lbl);
			authPanel.add(auth_bind_passwd_field);
			authPanel.add(auth_test_btn);
			authPanel.add(auth_status_lbl);
			authPanel.add(auth_header_lbl);
			authPanel.add(auth_method_lbl);
			authPanel.add(auth_type_chkbox);			
		}		
		

		// Authenticate Behaviour
		class AuthenticationListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				if ((auth_bind_dn_field.getText().equals("") || (auth_bind_passwd_field.getText().equals("")))) {
					auth_status_lbl.setText("Bind DN or Bind password fields must not be blank !");
					auth_status_lbl.setForeground(Color.red);
				} else {
					try {
						bindingService.authenticate(auth_bind_dn_field.getText(), auth_bind_passwd_field.getText() );
						auth_status_lbl.setText("The authentication was successfull.");
						auth_status_lbl.setForeground(Color.blue);
						// Only enable the other tabs once authentication is done
						tabsPane.setEnabledAt(2, true);
						tabsPane.setEnabledAt(3, true);
						// Once authenticated, display the BaseDN on the search page.
						search_base_dn_lbl.setText("Base DN: " + LdapImporterUtils.getBaseDN());
						search_base_field.setText(LdapImporterUtils.getBaseDN());					
					} catch (Exception ne) {
						auth_status_lbl.setText(ne.getMessage());
						auth_status_lbl.setForeground(Color.red);
					}
				}
			}
		}
		
		// Builds the Users and Groups tab UI
		public void buildUsersGroupsUI(){
			strPanel = new JPanel();
			strPanel.setLayout(null);
			title_lbl = new JLabel();
			title_lbl.setText("Users and Groups");
			title_lbl.setFont(new Font("", Font.BOLD, 15));
			title_lbl.setBounds(180, 0, 360, 30);
					
			numofGroups_lbl = new JLabel();
			numofGroups_lbl.setText("Number of Groups: ");
			numofGroups_lbl.setBounds(30, 50, 200, 30);
			numOfGroups_field = new JTextField();
			numOfGroups_field.setBounds(265, 50, 150, 30);
			numOfGroups_field.setText("5");
			numOfGroups_field.setToolTipText("Groups will be created under OU: Groups_xyz");
			
			groupId_lbl = new JLabel();
			groupId_lbl.setText("Group Name prefix:");
		
			groupId_lbl.setBounds(30, 170, 230, 30);
			groupId_field = new JTextField();
			groupId_field.setBounds(265, 170, 150, 30);
			groupId_field.setToolTipText("To be used to create group names with special characters for testing");
			groupId_field.setText("gr-");
			
			numOfUsers_lbl = new JLabel();
			numOfUsers_lbl.setText("Number of users in each group:");
			numOfUsers_lbl.setBounds(30, 90, 250, 30);
			numOfUsers_field = new JTextField();
			numOfUsers_field.setBounds(265, 90, 150, 30);
			numOfUsers_field.setText("5");
			numOfUsers_field.setToolTipText("Creates this number of users in each group specified above under OU: People_xyz");
			
			userId_lbl = new JLabel();
			userId_lbl.setText("User Name prefix:");
			userId_lbl.setBounds(30, 130, 150, 30);
			
			userId_field = new JTextField();
			userId_field.setBounds(265, 130, 150, 30);

			userId_field.setToolTipText("To be used to create user names with special characters for testing");
			userId_field.setText("usr-");
			
			server_type_lbl = new JLabel();
			server_type_lbl.setText("Choose LDAP server:");
			server_type_lbl.setBounds(30, 330, 150, 30);
				
			createStructure_btn = new JButton("Create Users & Groups");
			createStructure_btn.setBounds(30,260,200,33);
			createStructure_btn.setFocusable(true);		
			createStructure_btn.addActionListener(new CreateStructure());
				
			status_lbl = new JLabel();
			status_lbl.setText("");
			status_lbl.setBounds(30, 300, 300, 30);		

			strPanel.add(title_lbl);
			strPanel.add(numofGroups_lbl);
			strPanel.add(numOfGroups_field);
			strPanel.add(groupId_lbl);
			strPanel.add(groupId_field);
			strPanel.add(numOfUsers_lbl);
			strPanel.add(numOfUsers_field);
			strPanel.add(userId_lbl);
			strPanel.add(userId_field);
			strPanel.add(createStructure_btn);
			strPanel.add(status_lbl);	
		}
		
		// Create structure Behaviour
		class CreateStructure implements ActionListener {		
			public void actionPerformed(ActionEvent e) {
				int numOfGroups, numOfUsers = 0;
				numOfGroups = Integer.parseInt(numOfGroups_field.getText());
				numOfUsers = Integer.parseInt(numOfUsers_field.getText());
				
				if (numOfGroups <= 0)
				{
					status_lbl.setText("Number of groups must be positive");
					status_lbl.setForeground(Color.red);
					numOfGroups_field.requestFocus();
				} else{
					if (conn_openldap_radiobtn.isSelected()) {				
							try{
								userInterfaceManager.createStructure(numOfGroups,groupId_field.getText(), numOfUsers,userId_field.getText(),conn_openldap_radiobtn.getText());
								status_lbl.setText("Users and groups created successfully.");
								status_lbl.setForeground(Color.blue);	
							}catch(NamingException ne){
								status_lbl.setText(ne.getMessage());
								status_lbl.setForeground(Color.red);	
							}
									
					} else {
						if (conn_ad_radiobtn.isSelected()) {					
								try{									
									userInterfaceManager.createStructure(numOfGroups, groupId_field.getText(),numOfUsers, userId_field.getText(),conn_ad_radiobtn.getText());
									status_lbl.setText("Users and groups created successfully.");
									status_lbl.setForeground(Color.blue);
								}
								catch(NamingException ne){
									status_lbl.setText("Users and groups creation failed. See log.");
									status_lbl.setForeground(Color.red);
								}
							
							}
						
						}

					}
				}
			}
		// Builds the Search tab UI
		public void buildSearchUI() {
			searchPanel = new JPanel();
			searchPanel.setLayout(null);
			
			search_base_dn_lbl = new JLabel();
			search_base_dn_lbl.setText("");
			search_base_dn_lbl.setBounds(130, 0, 350, 60);
			
			search_base_dn_lbl.setFont(new Font("", Font.BOLD, 15));
			search_base_dn_lbl.setToolTipText("To be used as a reference when filling in the Search Base field");

			search_base_lbl = new JLabel();
			search_base_lbl.setText("Search base:");
			search_base_lbl.setBounds(10, 30, 130, 60);

			search_base_field = new JTextField();
			search_base_field.setBounds(125, 50, 500, 30);
			search_base_field.setToolTipText("Example: ou=Users_36e639a0e8bc,dc=company,dc=com");

			search_filter_lbl = new JLabel();
			search_filter_lbl.setText("Filter:");
			search_filter_lbl.setBounds(10, 75, 90, 60);

			search_filter_field = new JTextField();
			search_filter_field.setBounds(125, 90, 500, 30);
			search_filter_field.setToolTipText("<html>OpenLDAP example: <br>(objectClass=inetOrgPerson)<br><br>Active Directory example: <br>(objectClass=person) </html>");
			// Hard coded default search filter value - can be externilized
			search_filter_field.setText("(|(ou=Group*)(ou=People*))");
			search_btn = new JButton("Search");
			search_btn.addActionListener(new SearchListener());
			search_btn.setBounds(125, 130, 200, 33);

			search_result_textArea = new JTextArea();
			search_result_textArea.setBounds(20, 120, 300, 124);

			search_result_lbl = new JLabel();
			search_result_lbl.setText("");
			search_result_lbl.setBounds(10, 410, 395, 80);
			search_result_lbl.setFont(new Font("", Font.BOLD, 15));	

			scrollPane = new JScrollPane(search_result_textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.setBounds(16, 190, 600, 222);
			searchPanel.add(search_base_lbl);
			searchPanel.add(search_base_field);
			searchPanel.add(search_filter_lbl);
			searchPanel.add(search_filter_field);
			searchPanel.add(search_btn);
			searchPanel.add(search_result_lbl);
			searchPanel.add(scrollPane);
			searchPanel.add(search_base_dn_lbl);		
		}
		

		// Search Behaviour
		class SearchListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				String searchResult;
				// clear the search box
				search_result_textArea.setText("");
				if( (search_base_field.getText().equals("") || (search_filter_field.getText().equals("") )))
				{
					search_result_lbl.setText("Search base or filter fields must not be blank");
					search_result_lbl.setForeground(Color.red);			
				}
				else{			
					try{
						// Invoke the SearchService to perform the search
						searchResult = searchService.search(search_base_field.getText(), search_filter_field.getText());
						search_result_textArea.append(searchResult);
						// Invoke the SearchService to get the result set total.
						search_result_lbl.setText("Search result: " + searchService.getSearchCounter());
						search_result_lbl.setForeground(Color.blue);	
					}
					
					catch(InvalidSearchFilterException ife){
						search_result_lbl.setText(ife.getMessage());
						search_result_lbl.setForeground(Color.red);	
					}
					catch(NamingException ne){
						search_result_lbl.setText(ne.getMessage());
						search_result_lbl.setForeground(Color.red);	
					}
					
						
				}
			}

		}
	

}
