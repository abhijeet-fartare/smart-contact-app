package com.smart.service;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String subject, String message, String to) {

		String from = "hunkbaleno@gmail.com";

		// Variable of mail
		String host = "smtp.gmail.com";

		// get system property
		Properties properties = System.getProperties();

		// host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");



		// get session object
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("hunkbaleno@gmail.com", "8928938946");
			}
		});

		session.setDebug(true);

		// compose the message
		Message m = new MimeMessage(session);

		try {

			// from email
			m.setFrom(new InternetAddress(from));			
			m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			m.setSubject(subject);
			m.setText(message);

			Transport.send(m);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
