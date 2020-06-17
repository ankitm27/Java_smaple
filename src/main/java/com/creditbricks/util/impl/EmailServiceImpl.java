package com.creditbricks.util.impl;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceImpl {

    @Autowired
    public JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) {

    	MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            mimeMessage.setContent(text, "text/html");
            helper.setTo(to);
            helper.setSubject(subject);
        } catch (MessagingException ex) {
            Logger.getLogger(EmailServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        emailSender.send(mimeMessage);

    }
   @Async
   public static void sendSimpleMessageWithProperties(String to, String subject, String text) {
	 String user = "ekank.rana@gmaillcom";
	 String password = "uoylhsqpuibxrovr";
//	   String user = "panchalraja2@gmail.com";
//		 String password = "100nu5al";
	 Properties properties = System.getProperties();  
     properties.setProperty("mail.smtp.host", "smtp.gmail.com");
     properties.setProperty("mail.smtp.port", "587");
     properties.setProperty("mail.smtp.user", user);
     properties.setProperty("mail.smtp.password",password );
     properties.setProperty("mail.smtp.starttls.enable", "true");
     properties.setProperty("mail.smtp.auth", "true");
   
     Session session = Session.getInstance(properties, 
   		    new javax.mail.Authenticator(){
   		        protected PasswordAuthentication getPasswordAuthentication() {
   		            return new PasswordAuthentication(
   		           		user, password);// Specify the Username and the PassWord
   		        }
   		}); 
 
    //compose the message  
     try{  
    	 
        MimeMessage message = new MimeMessage(session); 
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setFrom(user);
        helper.setSubject(subject);
        helper.setText(text, true);
       
        
        Transport.send(message);  
        System.out.println("message sent successfully....");  
 
     }catch (MessagingException mex) {mex.printStackTrace();}  
  }  

//===============================  email with attachment===============
 
 @Async
 public static void sendSimpleMessageWithAttachment(String to, String subject, String text,String attachmentFilename, DataSource dataSource) {
	 String user = "panchalraja2@gmail.com";
	 String password = "100nu5al";

	 Properties properties = System.getProperties();  
     properties.setProperty("mail.smtp.host", "smtp.gmail.com");
     properties.setProperty("mail.smtp.port", "587");
     properties.setProperty("mail.smtp.user", user);
     properties.setProperty("mail.smtp.password",password );
     properties.setProperty("mail.smtp.starttls.enable", "true");
     properties.setProperty("mail.smtp.auth", "true");
   
     Session session = Session.getInstance(properties, 
   		    new javax.mail.Authenticator(){
   		        protected PasswordAuthentication getPasswordAuthentication() {
   		            return new PasswordAuthentication(
   		           		user, password);
   		        }
   		}); 
 
    //compose the message  
     try{  
    	 
        MimeMessage message = new MimeMessage(session); 
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setFrom(user);
        helper.setSubject(subject);
        helper.setText(text, true);
//        helper.addAttachment(attachmentFilename, dataSource);
        MimeBodyPart mbp2 = new MimeBodyPart();
        mbp2.setDataHandler(new DataHandler(dataSource)); 
        mbp2.setFileName(attachmentFilename);
        Multipart mp = new MimeMultipart();   
        mp.addBodyPart(mbp2);   
        message.setContent(mp);
        Transport.send(message);  
        System.out.println("message sent successfully....");  
 
     }catch (MessagingException mex) {mex.printStackTrace();}  
  }  

    
 
 
 
 
    	 public static void main(String [] args){  
    	    
    	      sendSimpleMessageWithProperties("sonupanchal9@gmail.com", "New Message", "TEXT");
    	     
    

}
}