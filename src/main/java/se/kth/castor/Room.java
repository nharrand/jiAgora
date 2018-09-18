package se.kth.castor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Room {
	protected Map<String, User> users;
	protected String roomId;
	protected String name;

	public Room(String id, String name) {
		this.name = name;
		this.roomId = id;
		this.users = new HashMap<>();
	}

	public void addUser(User user) {
		users.put(user.id, user);
	}

	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		o.put("roomId",roomId);
		o.put("name",name);
		JSONArray a = new JSONArray();
		for(User u: users.values()) {
			a.add(u.toJSON());
		}
		o.put("users", a);
		return o;
	}
}
