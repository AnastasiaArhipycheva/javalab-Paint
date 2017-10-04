/**
 * Created by Администратор on 20.05.2017.
 */

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import org.json.*;

public class API {


    public static float x1, x2, y1, y2;
    public static ArrayList<Shape> shapes = new ArrayList<Shape>();
    public static ArrayList<Color> shapeStroke = new ArrayList<Color>();


    private static final int PORT =2551;

    private static PrintWriter out = null;
    private static BufferedReader in = null;

    public static boolean delFalg = false;

    public static BufferedReader getIn() {
        return in;
    }

    public static void setIn(BufferedReader in) {
        API.in = in;
    }

    public static PrintWriter getOut() {
        return out;
    }

    public static void setOut(PrintWriter out) {
        API.out = out;
    }

    private static Socket socket;

    public static void createSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("localhost", PORT);   //ip телефона 192.168.43.64 //ip дом 192.168.1.197
                    setOut(new PrintWriter(socket.getOutputStream(), true));
                    setIn(new BufferedReader(new InputStreamReader(socket.getInputStream())));

                    System.out.println("Connected!");

                    while (true)
                    {
                        String line = in.readLine();
                        receiveJson(line);
                    }

                } catch (IOException e) {
                    System.out.println("Can't connect to the server!");
                }
            }
        }).start();
    }



    public static void sendString(String msg) {
        PrintWriter out = getOut();
        out.println(msg);

        setOut(out);
    }


    public static void receiveJson(String json) {
        try {
            Object ob = json;
            JSONObject object = new JSONObject((String)ob);
            if (object.optString("type").equals("clear"))
                receiveClear();
            if(object.optString("type").equals("point"))
                receivePoint(object);
            if(object.optString("type").equals("line"))
                receiveLine(object);
            if(object.optString("type").equals("circle"))
                receiveCircle(object);
            if(object.optString("type").equals("rect"))
                receiveRect(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void receiveClear(){
        delFalg = true;
        shapeStroke.clear();
        shapes.clear();
    }


    public static void receivePoint(JSONObject json) {
        Shape aShape = null;

        x1 = (float) json.optDouble("x");
        y1 = (float) json.optDouble("y");
        Color col = new Color(Integer.parseInt((String)json.optString("color")));

        aShape = drawBrush((int)x1, (int)y1, 5, 5);
        shapes.add(aShape);
        shapeStroke.add(col);
    }


    private static Ellipse2D.Float drawBrush(
            int x1, int y1, int brushStrokeWidth, int brushStrokeHeight) {
        return new Ellipse2D.Float(
                x1, y1, brushStrokeWidth, brushStrokeHeight);
    }

    public static void receiveLine(JSONObject json) {
        Shape aShape = null;
        int x1 = (int) json.optDouble("x1");
        int x2 = (int) json.optDouble("x2");
        int y1 = (int) json.optDouble("y1");
        int y2 = (int) json.optDouble("y2");
        Color col = new Color(Integer.parseInt((String)json.optString("color")));

        aShape = drawLine(x1, y1, x2, y2);

        shapes.add(aShape);
        shapeStroke.add(col);
    }

    private static Line2D.Float drawLine(
            int x1, int y1, int x2, int y2) {
        return new Line2D.Float(
                x1, y1, x2, y2);
    }

    public static void receiveCircle(JSONObject json) {
        Shape aShape = null;
        int x1 = (int) json.optDouble("x1");
        int x2 = (int) json.optDouble("x2");
        int y1 = (int) json.optDouble("y1");
        int y2 = (int) json.optDouble("y2");
        Color col = new Color(Integer.parseInt((String)json.optString("color")));

        aShape = drawEllipse(x1,y1,x2,y2);
        shapes.add(aShape);
        shapeStroke.add(col);
    }

    private static Rectangle2D.Float drawRectangle(
            int x1, int y1, int x2, int y2) {


        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);

        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);
        return new Rectangle2D.Float(
                x, y, width, height);
    }

    private static Ellipse2D.Float drawEllipse(
            int x1, int y1, int x2, int y2) {
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);
        return new Ellipse2D.Float(
                x, y, width, height);
    }


    public static void receiveRect(JSONObject json) {
        Shape aShape = null;
        int x1 = (int) json.optDouble("x1");
        int x2 = (int) json.optDouble("x2");
        int y1 = (int) json.optDouble("y1");
        int y2 = (int) json.optDouble("y2");
        Color col = new Color(Integer.parseInt((String)json.optString("color")));

        aShape = drawRectangle(x1,y1,x2,y2);
        shapes.add(aShape);
        shapeStroke.add(col);
    }



    public static void sendPoint(float x, float y, Color color) {
        JSONObject object = new JSONObject();
        try {
            object.put("color", Integer.toString(color.getRGB()));
            object.put("type", "point");
            object.put("x", x);
            object.put("y", y);
            String msg = object.toString();
            sendString(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public  static void sendClear() {
        JSONObject object = new JSONObject();
        try {
            object.put("type", "clear");
            String msg = object.toString();
            sendString(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void sendLine(float x1, float y1, float x2, float y2,  Color color) {
        JSONObject object = new JSONObject();
        try {
            object.put("color", Integer.toString(color.getRGB()));
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

    public static void sendCircle(float x1, float y1, float x2, float y2, Color color) {
        JSONObject object = new JSONObject();
        try {
            object.put("color", Integer.toString(color.getRGB()));
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
    public static void sendRect(float x1, float y1, float x2, float y2, Color color) {
        JSONObject object = new JSONObject();
        try {
            object.put("color", Integer.toString(color.getRGB()));
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