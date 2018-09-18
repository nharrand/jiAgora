package se.kth.castor;

import org.json.simple.JSONObject;

public class User {
	protected String id;
	protected String name;

	public User(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		o.put("id",id);
		o.put("name",name);
		return o;
	}
}
