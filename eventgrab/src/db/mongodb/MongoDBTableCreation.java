package db.mongodb;

import java.text.ParseException;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;


public class MongoDBTableCreation {
	// Run as Java application to create MongoDB collections with index.
	public static void main(String[] args) throws ParseException {
		// Step 1: connetion to MongoDB.
		MongoClient mongoClient = MongoClients.create();
		MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);

		// Step 2: remove old collections.
		db.getCollection("users").drop();
		db.getCollection("items").drop();

		// Step 3: create new collections.
		IndexOptions indexOptions = new IndexOptions().unique(true);
		db.getCollection("users").createIndex(new Document("user_id", 1), indexOptions);
		db.getCollection("items").createIndex(new Document("item_id", 1), indexOptions);

		// Step 4: insert fake user data and create index.
		db.getCollection("users").insertOne(
				new Document().append("user_id", "1111").append("password", "6369f0832533e140100edb005805e72e")
				.append("first_name", "Erica").append("last_name", "Chen"));

		mongoClient.close();
		System.out.println("Import is done successfully.");
	}
}
