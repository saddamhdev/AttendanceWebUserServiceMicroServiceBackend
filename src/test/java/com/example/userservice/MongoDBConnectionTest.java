package com.example.userservice;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConnectionTest {
    public static void main(String[] args) {
        // String uri = "mongodb://admin:ce18046@13.51.206.164:27017/admin?authSource=admin"; // Your MongoDB URI
        String uri = "mongodb://snvn:H3Af5kWz5PhhS8H8@159.89.172.251:27017/?authSource=snvn&tls=false"; // Your MongoDB URI

<<<<<<< HEAD

=======
>>>>>>> e578cacc7894235eed0f181de9721cbe75df770f
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("admin");
            Document ping = database.runCommand(new Document("ping", 1));
            System.out.println("MongoDB Connection Successful! Ping Response: " + ping.toJson());
        } catch (Exception e) {
            System.err.println("MongoDB Connection Failed: " + e.getMessage());
        }
    }
}
