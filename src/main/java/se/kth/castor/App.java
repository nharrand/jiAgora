package se.kth.castor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

/**
 * Hello world!
 *
 */
public class App 
{
    static int SERVER_PORT = 8000;

    public static Map<String,Room> rooms = new ConcurrentHashMap<>();

    public static void log(Request req) {
        System.out.println("[In] " + req.body());
    }

    public static void main( String[] args )
    {
        System.out.println("Server is Starting on port " + SERVER_PORT);
        JSONParser p = new JSONParser();
        port(SERVER_PORT);

        get("/completeInfo", (Request req, Response res) -> {
            log(req);
            res.status(200);
            JSONArray json = new JSONArray();
            for(Room r: rooms.values()) {
                json.add(r.toJSON());
            }
            return json.toJSONString();
        });

        get("/rooms", (Request req, Response res) -> {
            log(req);
            res.status(200);
            JSONArray json = new JSONArray();
            for(Room r: rooms.values()) {
                json.add(r.toJSON());
            }
            return json.toJSONString();
        });

        get("/users", (Request req, Response res) -> {
            log(req);
            String raw = req.body();
            Object parsed = p.parse(raw);
            if(!(parsed instanceof  JSONObject)) {
                res.status(400);
                return "Error, unable to parse JSON.";
            }
            JSONObject room = (JSONObject) parsed;
            try {
                String roomId = (String) room.getOrDefault("roomId", null);
                Room r = rooms.get(roomId);
                JSONArray json = new JSONArray();
                for(User u: r.users.values()) {
                    json.add(u.toJSON());
                }
                res.status(200);
                return json.toJSONString();

            } catch (ClassCastException | NullPointerException e) {
                res.status(400);
                return "Error, unable to parse JSON.";
            }
        });

        post("/roomCreation", (Request req, Response res) -> {
            log(req);
            String raw = req.body();
            Object parsed = p.parse(raw);
            if(!(parsed instanceof  JSONObject)) {
                res.status(400);
                return "Error, unable to parse JSON.";
            }
            JSONObject room = (JSONObject) parsed;
            try {
                Room r = new Room((String) room.getOrDefault("roomId", null), (String) room.getOrDefault("name", null));
                rooms.put(r.roomId, r);
            } catch (ClassCastException | NullPointerException e) {
                res.status(400);
                return "Error, unable to parse JSON.";
            }
            res.status(200);
            return "";
        });

        post("/roomDeletion", (Request req, Response res) -> {
            log(req);
            String raw = req.body();
            Object parsed = p.parse(raw);
            if(!(parsed instanceof  JSONObject)) {
                res.status(400);
                return "Error, unable to parse JSON.";
            }
            JSONObject room = (JSONObject) parsed;
            try {
                String roomId = (String) room.getOrDefault("roomId", null);
                rooms.remove(roomId);
            } catch (ClassCastException | NullPointerException e) {
                res.status(400);
                return "Error, unable to parse JSON.";
            }
            res.status(200);
            return "";
        });

        post("/userJoin", (Request req, Response res) -> {
            log(req);
            String raw = req.body();
            Object parsed = p.parse(raw);
            if(!(parsed instanceof  JSONObject)) {
                res.status(400);
                return "Error, unable to parse JSON.";
            }
            JSONObject info = (JSONObject) parsed;
            try {
                String roomId = (String) info.getOrDefault("roomId", null);
                JSONObject user = (JSONObject) info.getOrDefault("user", null);
                Room r = rooms.get(roomId);
                User u = new User((String) user.getOrDefault("id", null), (String) user.getOrDefault("name", null));
                r.users.put(u.id,u);
            } catch (ClassCastException | NullPointerException e) {
                res.status(400);
                return "Error, unable to parse JSON.";
            }
            res.status(200);
            return "";
        });

        post("/userLeave", (Request req, Response res) -> {
            String raw = req.body();
            Object parsed = p.parse(raw);
            if(!(parsed instanceof  JSONObject)) {
                res.status(400);
                return "Error, unable to parse JSON.";
            }
            JSONObject info = (JSONObject) parsed;
            try {
                String roomId = (String) info.getOrDefault("roomId", null);
                Room r = rooms.get(roomId);
                String userId = (String) info.getOrDefault("userId", null);
                r.users.remove(userId);
            } catch (ClassCastException | NullPointerException e) {
                res.status(400);
                return "Error, unable to parse JSON.";
            }
            res.status(200);
            return "";
        });

        //get full rooms - user
        //get rooms
        //get users
        //put roomCreation
        //put roomDeletion
        //put userJoin
        //put userLeave

    }
}
