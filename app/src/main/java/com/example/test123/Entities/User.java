package com.example.test123.Entities;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class User {
    public int id;
    public String login;
    public String pass;
    public String name;

    public User() {

    }

    public User(int id, String login, String pass, String name) {
        this.id = id;
        this.login = login;
        this.pass = pass;
        this.name = name;
    }

    public static User parse(JsonReader jsonReader) throws IOException {
        User user = new User();
        jsonReader.beginObject();
        while(jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case "ID":
                    user.id = jsonReader.nextInt();
                    break;
                case "login":
                    user.login = jsonReader.nextString();
                    break;
                case "pass":
                    user.pass = jsonReader.nextString();
                    break;
                case "name":
                    user.name = jsonReader.nextString();
                    break;
                default:
                    jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return user;
    }

    public String delete() throws IOException, JSONException {
        HttpURLConnection conn = getConnection("http://188.120.248.48:20080/product/delete.php", "DELETE");
        conn.getOutputStream().write(("ID="+this.id).getBytes());


        BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder total = new StringBuilder();
        for (String line; (line = r.readLine()) != null; ) {
            total.append(line).append('\n');
        }
        JSONObject obj = new JSONObject(total.toString());
        String out = obj.getString("message");
        return out;
    }

    public String addUser() throws IOException, JSONException {
        HttpURLConnection conn = getConnection("http://188.120.248.48:20080/product/create.php", "POST");
        JSONObject object = new JSONObject();
        object.put("ID", this.id);
        object.put("login", this.login);
        object.put("pass", this.pass);
        object.put("name", this.name);
        conn.getOutputStream().write(object.toString().getBytes());
        conn.getOutputStream().flush();

        BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder total = new StringBuilder();
        for (String line; (line = r.readLine()) != null; ) {
            total.append(line).append('\n');
        }
        JSONObject obj = new JSONObject(total.toString());
        String out = obj.getString("message");
        conn.disconnect();
        return out;
    }

    public static HttpURLConnection getConnection(String toSend, String method) throws IOException {
        URL url = new URL(toSend);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        //conn.setDoInput(true);
        //conn.setDoOutput(true);
        return conn;
    }

    public static ArrayList<User> getUsersList() throws IOException {
        HttpURLConnection conn = getConnection("http://188.120.248.48:20080/product/readList.php", "GET");
        JsonReader jsonReader = new JsonReader(new InputStreamReader(conn.getInputStream()));
        jsonReader.beginArray();
        ArrayList<User> users = new ArrayList<>();
        while(jsonReader.hasNext()) {
            users.add(parse(jsonReader));
        }
        jsonReader.endArray();
        return users;
    }
}
