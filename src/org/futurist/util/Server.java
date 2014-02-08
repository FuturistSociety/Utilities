package org.futurist.util;

import java.io.File;
import java.io.IOException;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;

public class Server extends Thread {

	public static final int DEFAULT_FTP_PORT = 10021;
	public static final int DEFAULT_SSH_PORT = 10022;
	
	private FtpServer ftpd;
	private SshServer sshd;

	public Server() {
		// create FTP factory for default port
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory factory = new ListenerFactory();
		factory.setPort(DEFAULT_FTP_PORT);

		// define SSL configuration
		SslConfigurationFactory ssl = new SslConfigurationFactory();
		ssl.setKeystoreFile(new File("src/test/resources/ftpserver.jks"));
		ssl.setKeystorePassword("password");

		// set the SSL configuration for the listener
		factory.setSslConfiguration(ssl.createSslConfiguration());
		factory.setImplicitSsl(true);

		// replace the default listener
		serverFactory.addListener("default", factory.createListener());
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		userManagerFactory.setFile(new File("myusers.properties"));
		serverFactory.setUserManager(userManagerFactory.createUserManager());
		ftpd = serverFactory.createServer();

		// create SSH server for default port
		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(DEFAULT_SSH_PORT);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		sshd.setShellFactory(new ProcessShellFactory(new String[] { "/bin/sh", "-i", "-l" }));

		run();
	}

	/**
	 * Start the SSH and FTP-SSL servers
	 */
	public void run() {
		try {
			sshd.start();
			ftpd.start();
		} catch (IOException | FtpException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop the SSH and FTP-SSL servers
	 */
	public void quit() {
		try {
			sshd.stop();
			ftpd.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Restart the SSH and FTP-SSL servers
	 */
	public void restart() {
		quit();
		run();
	}
	
	/**
	 * Shows the status of the SSH and FTP-SSL servers
	 */
	public void status() {
		System.out.println("SSH is running on " + sshd.getHost() + " on port " + sshd.getPort() + " with " + sshd.getActiveSessions().size() + " active sessions to " + sshd.getShellFactory() + ".");
		
		String status = "running ";
		if(ftpd.isSuspended()) { status = "suspended"; }
		else if(ftpd.isStopped()) { status = "stopped"; }
		System.out.println("FTP-SSL is " + status + ".");
	}
	
}