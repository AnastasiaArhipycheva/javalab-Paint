package anastasia.draw;

import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Администратор on 18.05.2017.
 */

public class API {

    private static final int PORT =2551;


    private static PrintWriter out = null;

  //  public static String[] json = null;
    public static ArrayList<String> json = new ArrayList<String>();

    public static PrintWriter getOut() {
        return out;
    }

    public static void setOut(PrintWriter out) {
        API.out = out;
    }

    private static  BufferedReader in = null;

    public static  BufferedReader getIn() {
        return in;
    }

    public static void setIn( BufferedReader in) {
        API.in = in;

    }

    private static Socket socket;

    public static void createSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("192.168.1.197", PORT);   //ip телефона 192.168.43.64 //ip дом 192.168.1.197
                    setOut(new PrintWriter(socket.getOutputStream(), true));
                    setIn(new BufferedReader(new InputStreamReader(socket.getInputStream())));


                    while (true)
                    {
                        String line = in.readLine();
                        System.out.println(line);
                      //  receiveJson(line);
                        json.add(line);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static void sendString(String msg) {
        PrintWriter out = getOut();
        // out = new ObjectOutputStream(socket.getOutputStream());
        out.println(msg);
        setOut(out);
        out.flush();

    }



    public static void sendClear() {
        JSONObject object = new JSONObject();
        try {
            object.put("type", "clear");
            String msg = object.toString();
            sendString(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void sendPoint(float x, float y, int color) {
        JSONObject object = new JSONObject();
        try {
            object.put("color", color);
            object.put("type", "point");
            object.put("x", x);
            object.put("y", y);
            String msg = object.toString();
            sendString(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void sendLine(float x1, float y1, float x2, float y2, int  color) {
        JSONObject object = new JSONObject();
        try {
            object.put("color", color);
            object.put("type", "line");
            object.put("x1", x1);
            object.put("x2", x2);
            object.put("y1", y1);
            object.put("y2", y2);
            String msg = object.toString();
            sendString(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void sendCircle(float x1, float y1, float x2, float y2, int  color) {
        JSONObject object = new JSONObject();
        try {
            object.put("color", color);
            object.put("type", "circle");
            object.put("x1", x1);
            object.put("x2", x2);
            object.put("y1", y1);
            object.put("y2", y2);
            String msg = object.toString();
            sendString(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void sendRect(float x1, float y1, float x2, float y2, int  color) {
        JSONObject object = new JSONObject();
        try {
            object.put("color", color);
            object.put("type", "rect");
            object.put("x1", x1);
            object.put("x2", x2);
            object.put("y1", y1);
            object.put("y2", y2);
            String msg = object.toString();
            sendString(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
