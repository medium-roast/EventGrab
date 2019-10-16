package db.mongodb;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;

import db.DBConnection;
import external.Item;
import external.TicketMasterClient;


public class MongoDBConnection implements DBConnection {
	private MongoClient mongoClient;
	private MongoDatabase db;

	public MongoDBConnection() {
		mongoClient = MongoClients.create();
		db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
	}
	
	@Override
	public void close() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if (db == null) {
			System.err.println("DB connection failed");
			return;
		}
		db.getCollection("users").updateOne(eq("user_id", userId),
				new Document("$push", new Document("favorite", new Document("$each", itemIds))));
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if (db == null) {
			System.err.println("DB connection failed");
			return;
		}
		db.getCollection("users").updateOne(eq("user_id", userId), 
				new Document("$pullAll", new Document("favorite", itemIds)));
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if (db == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> itemIds = getFavoriteItemIds(userId);
		for (String itemId : itemIds) {
			FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", itemId));
			if (iterable.first() != null) {
				Document doc = iterable.first();

				Item.ItemBuilder builder = new Item.ItemBuilder();
				builder.setItemId(doc.getString("item_id"));
				builder.setName(doc.getString("name"));
				builder.setAddress(doc.getString("address"));
				builder.setUrl(doc.getString("url"));
				builder.setImageUrl(doc.getString("image_url"));
				builder.setRating(doc.getDouble("rating"));
				builder.setDistance(doc.getDouble("distance"));
				builder.setDate(doc.getString("date"));
				builder.setCategories(getCategories(itemId));

				favoriteItems.add(builder.build());
			}
		}
		return favoriteItems;
	}
	
	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		if (db == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}
		Set<String> favoriteItems = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));
		if (iterable.first() != null && iterable.first().containsKey("favorite")) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get("favorite");
			favoriteItems.addAll(list);
		}
		return favoriteItems;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		if (db == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}
		Set<String> categories = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", itemId));
		if (iterable.first() != null && iterable.first().containsKey("categories")) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get("categories");
			categories.addAll(list);
		}
		return categories;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		TicketMasterClient client = new TicketMasterClient();
		List<Item> items = client.search(lat, lon, term);
		for(Item item : items) {
			saveItem(item);
		}
		Collections.sort(items, new Comparator<Item>() {
			@Override
			public int compare(Item o1, Item o2) {
				String date1 = o1.getDate();
				String date2 = o2.getDate();
				if (date1.length() > 0 && date2.length() > 0) {
					return date1.compareTo(date2);
				}
				return date1.length() > 0 ? -1 : 1; 
			}			
		});
		return items;		
	}

	@Override
	public void saveItem(Item item) {
		if (db == null) {
			System.err.println("DB connection failed");
			return;
		}
		FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", item.getItemId()));
		if (iterable.first() == null) {    // Insert a new document if the item_id does not exist.
			db.getCollection("items")
			.insertOne(new Document().append("item_id", item.getItemId()).append("distance", item.getDistance())
					.append("name", item.getName()).append("address", item.getAddress())
					.append("url", item.getUrl()).append("image_url", item.getImageUrl())
					.append("rating", item.getRating()).append("date", item.getDate())
					.append("categories", item.getCategories()));
		}
	}

	@Override
	public String getFullname(String userId) {
		if (db == null) {
			return "";
		}
		String name = "";
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));
		if (iterable.first() != null && iterable.first().containsKey("first_name") && iterable.first().containsKey("last_name")) {
			name = iterable.first().getString("first_name") + " " + iterable.first().getString("last_name");
		}
		return name;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		if (db == null) {
			System.err.println("DB connection failed");
			return false;
		}
		FindIterable<Document> iterable = db.getCollection("users").find(and(eq("user_id", userId), eq("password", password)));
		if (iterable.first() != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean registerUser(String userId, String password, String firstname, String lastname) {
		if (db == null) {
			System.err.println("DB connection failed");
			return false;
		}
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));
		if (iterable.first() == null) {
			db.getCollection("users").insertOne(
					new Document().append("user_id", userId).append("password", password)
					.append("first_name", firstname).append("last_name", lastname));
			return true;
		}
		return false;
	}

}
