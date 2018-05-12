/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaFX_NIST;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 *
 * @author ATG8
 */
public final class AuditAlert {
    
    // declare variables
    private String user;

    // constructor to get username from main
    public AuditAlert(String user) {
        // set user variable, current date/time, time to expire, and generate random token
        this.user = user;
        
        // send email to user with token
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                        "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                                //I guess I have to change my password after this
                                return new PasswordAuthentication("Gmail username goes here",
                                        "Gmail passowrd goes here");
                        }
                });

        try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("From email address goes here"));
                
                // Send email to admin
                message.setRecipients(Message.RecipientType.TO,
                                InternetAddress.parse("To email address goes here"));
                message.setSubject("Audit Error");
                message.setText("The audit log is no longer functioning correctly. "
                        + "\n\n Failure on user: " + user);

                Transport.send(message);

        } catch (MessagingException e) {
                throw new RuntimeException(e);
        }
    }
}