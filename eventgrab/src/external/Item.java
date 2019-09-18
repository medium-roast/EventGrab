package external;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Item object contains useful fields of an event
public class Item {
	private String itemId;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories;  // Categories of this event
	private String imageUrl;
	private String url;
	private double distance;
	private String date;
	
	private Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;
		this.date = builder.date;
	}
	
	public String getItemId() {
		return itemId;
	}
	
	public String getName() {
		return name;
	}
	
	public double getRating() {
		return rating;
	}
	
	public String getAddress() {
		return address;
	}
	
	public Set<String> getCategories() {
		return categories;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public String getUrl() {
		return url;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public String getDate() {
		return date;
	}
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories));
			obj.put("image_url", imageUrl);
			obj.put("url", url);
			obj.put("distance", distance);
			obj.put("date", date);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static class ItemBuilder {
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;
		private String date;
		
		public Item build() {
			return new Item(this);
		}
		
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setRating(double rating) {
			this.rating = rating;
		}
		
		public void setAddress(String address) {
			this.address = address;
		}
		
		public void setCategories(Set<String> categories) {
			this.categories = categories;
		}
		
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		
		public void setUrl(String url) {
			this.url = url;
		}
		
		public void setDistance(double distance) {
			this.distance = distance;
		}
		
		public void setDate(String date) {
			this.date = date;
		}
	}
}

/** 
 * Usage:
 * Item.ItemBuilder builder = new Item.ItemBuilder();
 * builder.setItemId(...);
 * builder.setName(...);
 * Item item = builder.build();
 */