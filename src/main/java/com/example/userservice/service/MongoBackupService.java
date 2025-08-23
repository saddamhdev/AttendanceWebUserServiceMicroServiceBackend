package com.example.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class MongoBackupService {

    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String FROM_EMAIL = "saddamhossen8952@gmail.com";
    private static final String TO_EMAIL = "citabdulbari@gmail.com.com";
    private static final String PASSWORD = "wpxg ovzw dhfn gwuv";

    // Backup every 5 min (for testing). Use "0 0 2 * * *" for daily 2 AM
   // @Scheduled(cron = "0 */2 * * * *")
    @Scheduled(cron = "0 0 2 * * *")
    public void backupDatabase() {
        Path projectDir = Paths.get("").toAbsolutePath(); // project root
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String backupPath = projectDir + "/backup-" + timestamp;

        try {
            // 1️⃣ Dump collections into JSON
            Files.createDirectories(Paths.get(backupPath));
            for (String collectionName : mongoTemplate.getCollectionNames()) {
                List<?> documents = mongoTemplate.findAll(Object.class, collectionName);
                ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
                String json = writer.writeValueAsString(documents);

                File file = new File(backupPath, collectionName + ".json");
                try (FileWriter fw = new FileWriter(file)) {
                    fw.write(json);
                }
            }

            // 2️⃣ Zip backup folder
            File zipFile = zipBackupFolder(backupPath);

            // 3️⃣ Send email
            byte[] fileBytes = Files.readAllBytes(zipFile.toPath());
            Send(
                    FROM_EMAIL,
                    TO_EMAIL,
                    PASSWORD,
                    "MongoDB backup created at " + timestamp,
                    fileBytes,
                    zipFile.getName(),
                    "MongoDB Backup - Attendance UserData " + timestamp
            );

            System.out.println("✅ Backup created & mailed: " + zipFile.getAbsolutePath());

            // 4️⃣ Cleanup: delete folder + zip after sending
            deleteDirectory(Paths.get(backupPath));
            Files.deleteIfExists(zipFile.toPath());

            System.out.println("🗑️ Backup cleaned up after email.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Utility: Zip a folder
    private File zipBackupFolder(String sourceDirPath) throws Exception {
        File dir = new File(sourceDirPath);
        String zipFilePath = sourceDirPath + ".zip";

        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Path sourcePath = Paths.get(sourceDirPath);
            Files.walk(sourcePath).filter(Files::isRegularFile).forEach(path -> {
                ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(path).toString());
                try {
                    zos.putNextEntry(zipEntry);
                    Files.copy(path, zos);
                    zos.closeEntry();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return new File(zipFilePath);
    }

    // Utility: Delete folder recursively
    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted((a, b) -> b.compareTo(a)) // delete children before parent
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
    @Async
    public void Send(String fromEmailAddress, String toEmailAddress, String password,
                     String data, byte[] resumeBytes, String fileName,String subject) throws IOException {
        String to = toEmailAddress;
        String from = fromEmailAddress;

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmailAddress, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSentDate(new Date());
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();

            String htmlSignature = """
                <br/><br/>
                <table style="font-family: Arial; font-size: 14px;">
                  <tr>
                    <td>
                      <img src="https://drive.google.com/file/d/1L1o64-mZb-KtEsk37pIfd_hK5xFywe9R/view?usp=sharing" alt="Photograph" width="80" style="border-radius: 50%; margin-right: 15px;" />
                    </td>
                    <td>
                      <strong style="font-size: 16px;">Saddam Hossen</strong><br/>
                      Full Stack Java Developer<br/>
                      M: +8801647618952<br/>
                      E: <a href="mailto:saddamhossen8952@gmail.com">saddamhossen8952@gmail.com</a> |
                      <a href="https://earthface.biz">earthface.biz</a><br/>
                      Dhaka, Bangladesh<br/><br/>
                      <a href="https://www.linkedin.com/in/saddam-hossen-619a81174/"><img src="https://cdn-icons-png.flaticon.com/24/145/145807.png" alt="LinkedIn" /></a>
                      <a href="https://github.com/Saddam-Hossen"><img src="https://cdn-icons-png.flaticon.com/24/733/733553.png" alt="GitHub" /></a>
                    </td>
                  </tr>
                </table>
                """;

            String emailBody = "<html><body>" + data.replace("\n", "<br/>") + htmlSignature + "</body></html>";
            htmlPart.setContent(emailBody, "text/html");

            multipart.addBodyPart(htmlPart);

            if (resumeBytes != null && fileName != null) {
                MimeBodyPart attachment = new MimeBodyPart();
                attachment.setFileName(fileName);
                attachment.setContent(resumeBytes, "application/pdf");
                multipart.addBodyPart(attachment);
            }
            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Sent message successfully....");

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
