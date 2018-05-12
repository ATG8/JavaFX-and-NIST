/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaFX_NIST;

import java.security.SecureRandom;
import java.time.LocalDateTime;
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
public final class SendEmail {
    
    // declare variables
    private String user;
    private LocalDateTime timeCode;
    private LocalDateTime expire;
    private SecureRandom sRand = new SecureRandom();
    private int token;
    private String strToken;

    // constructor to get username from main
    public SendEmail(String user) {
        // set user variable, current date/time, time to expire, and generate random token
        this.user = user;
        timeCode = LocalDateTime.now();
        expire = timeCode.plusMinutes(3);
        token = sRand.nextInt(1000000);
        strToken = String.format("%06d", token);
        
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
                                        "Gmail password goes here");
                        }
                });

        try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("From email address goes here"));
                
                // I would normally pull email address from "user" here.  For now,
                // I will hard code my own credentials for testing
                message.setRecipients(Message.RecipientType.TO,
                                InternetAddress.parse("To email address goes here"));
                message.setSubject("Authentication Token");
                message.setText("Hello " + this.user + ",\n\n" + "Please find your "
                        + "authentication token.  It is good for 3 minutes only."
                        + "\n\n" + strToken + "\n\nGood until " + expire);

                Transport.send(message);

        } catch (MessagingException e) {
                throw new RuntimeException(e);
        }
    }
    
    // method to get timeCode
    public LocalDateTime getExpire(){
        return expire;
    }
    
    // method to get token
    public String getToken(){
        return strToken;
    }
}