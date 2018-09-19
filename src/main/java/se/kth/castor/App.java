package se.kth.castor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import spark.Request;
import spark.Response;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static spark.Spark.*;

/**
 * Hello world!
 *
 */
public class App 
{
    static int SERVER_PORT = 48000;
    static String pathToIndex = "/app/resources/index.html";
    static String URL_JITSI_MET = "";
    static String bufferedFrontPage;

    public static Map<String,Room> rooms = new ConcurrentHashMap<>();

    public static void log(Request req) {
        System.out.println("[" + req.ip() + "] " + req.pathInfo() + " -> " + req.body());
    }

    public static String getIndex() {
        if(bufferedFrontPage == null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(new File(pathToIndex)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bufferedFrontPage = reader.lines().collect(Collectors.joining(System.lineSeparator())).replace("URL_JITSI_MEET", URL_JITSI_MET);
        }
        return bufferedFrontPage;
    }

    public static void main( String[] args )
    {
        System.out.println("Server is Starting on port " + SERVER_PORT);
        URL_JITSI_MET = args[0];
        JSONParser p = new JSONParser();
        port(SERVER_PORT);

        staticFiles.location("/app/resources");

        get("/", (Request req, Response res) -> {
            res.type("text/html");
            return getIndex();
        });

        get("/completeInfo", (Request req, Response res) -> {
            log(req);
            res.status(200);
            JSONArray json = new JSONArray();
            for(Room r: rooms.values()) {
                json.add(r.toJSON());
            }
            res.type("application/json");
            return json.toString();
        });

        get("/rooms", (Request req, Response res) -> {
            log(req);
            res.status(200);
            JSONArray json = new JSONArray();
            for(Room r: rooms.values()) {
                json.add(r.toJSON());
            }
            res.type("application/json");
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
                res.type("application/json");
                return json.toJSONString();

            } catch (ClassCastException | NullPointerException e) {
                res.status(400);
                return "Error, unable to parse JSON.";
            }
        });

        get("/usersRooms", (Request req, Response res) -> {
            log(req);
            JSONArray json = new JSONArray();
            for(Room r: rooms.values()) {
                for (User u : r.users.values()) {
                    JSONObject us = u.toJSON();
                    us.put("room", r.roomId);
                    json.add(us);
                }
            }
            res.status(200);
            res.type("application/json");
            return json.toJSONString();
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
                if(r == null) {
                    r = new Room(roomId, roomId);
                    rooms.put(roomId,r);
                }
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
