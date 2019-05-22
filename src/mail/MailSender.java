package mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;


public final class MailSender {

    private final String receiver;
    private final String text;
    public MailSender(String receiver, String text){
        this.receiver = receiver;
        this.text = text;
    }
    public void send() {
    //public static void main(String[] args) {
        final String username = "oreoflamespam@gmail.com";
        final String password = "filmmuwfuhlkdlpw";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            BodyPart messageBodyPart = new MimeBodyPart();
            MimeMultipart multipart = new MimeMultipart("related");
            String htmlText = "<H1>Hello, I am representative of oreoflame, would you like to try our new products?!!</H1><img src=\"http://logwoman.ru/wp-content/uploads/posts/2019-01/1548071154_oriflejm.jpg\"><h1>"+text+"</h1>";
            messageBodyPart.setContent(htmlText, "text/html");
            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setHeader("Content-ID", "<image>");

            messageBodyPart.setContent(htmlText, "text/html");
            multipart.addBodyPart(messageBodyPart);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("oreoflamespam@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(receiver)
            );
            message.setSubject("Account registration");
            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}