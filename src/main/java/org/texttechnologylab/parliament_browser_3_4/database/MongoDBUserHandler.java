package org.texttechnologylab.parliament_browser_3_4.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


/**
 * @author Maximilian
 */
public class MongoDBUserHandler {

    private final MongoDatabase db;

    public MongoDBUserHandler(MongoDBHandler client) {
        this.db = client.getDatabase();
    }

    public void createUser(String username, String password) {
        MongoCollection<Document> UserCollection = db.getCollection("User");
        UserCollection.insertOne(new Document("username", username).append("password", password));
    }

    public void deleteUser(String username) {
        MongoCollection<Document> UserCollection = db.getCollection("User");
        UserCollection.deleteOne(new Document("username", username));
    }
}
