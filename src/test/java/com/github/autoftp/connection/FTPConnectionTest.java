package com.github.autoftp.connection;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class FTPConnectionTest {

	@Test
	public void passingTest() throws JSchException, SftpException {
		
		/*
		JSch jsch = new JSch();
		
		Session session = null;
		
		session = jsch.getSession("mmhmm", "188.165.205.150", 80);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setPassword("-upr&fAQ_r9C");
		session.connect();
		
		Channel channel = session.openChannel("sftp");
		channel.connect();
		
		ChannelSftp sftpChannel = (ChannelSftp) channel;
		sftpChannel.cd("Done/TV");
		
		Vector<LsEntry> v = sftpChannel.ls(".");
		
		for(LsEntry entry : v)
		{
			System.out.println(entry.getFilename());
		}
		*/
		
	}
}
