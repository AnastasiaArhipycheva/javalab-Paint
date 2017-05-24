/**
 * Created by Администратор on 20.05.2017.
 */
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.json.*;

public class API {

    //   private static MainActivity.PostPointAsyncTask task;
//
//    static {
//        try {
//            task = new MainActivity.PostPointAsyncTask();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static JSONObject[] update = new JSONObject[10000];
    public static int count_update=0;

    private static final int PORT =2551;

    private static ObjectOutputStream out = null;
    private static ObjectInputStream in = null;

    public static ObjectInputStream getIn() {
        return in;
    }

    public static void setIn(ObjectInputStream in) {
        API.in = in;
    }

    public static ObjectOutputStream getOut() {
        return out;
    }

    public static void setOut(ObjectOutputStream out) {
        API.out = out;
    }

    private static Socket socket;

    public static void createSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("192.168.1.197", PORT);   //ip телефона 192.168.43.64 //ip дом 192.168.1.197
                    setOut(new ObjectOutputStream(socket.getOutputStream()));
                    setIn(new ObjectInputStream(socket.getInputStream()));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    public static void sendString(String msg) {
        ObjectOutputStream out = getOut();
        try {
            // out = new ObjectOutputStream(socket.getOutputStream());
            out.writeUTF(msg);
            setOut(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } /*finally {
            if(out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
           }
         */

    }

    public static void receiveString() {
        ObjectInputStream in = getIn();
        try {
            // out = new ObjectOutputStream(socket.getOutputStream());
            update[count_update] = new JSONObject(in.readUTF());
            count_update++;
         //   receiveJson(in.readUTF());
            setIn(in);
           // out.writeUTF(msg);
        //    setOut(out);
        //    out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } /*finally {
            if(out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
           }
         */


    }

    public static void receiveJson(String json) {
        try {
            Object ob = json;
     //       JSONObject object = new JSONObject((JSONObject)ob);  (String)
            JSONObject object = new JSONObject((String)ob);
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
    public static void receivePoint(JSONObject json) {
        float x = (float) json.optDouble("x");
        float y = (float) json.optDouble("y");
        System.out.println("point"+ "x" +x+ "y" +y);

    }

    public static void receiveLine(JSONObject json) {
        float x1 = (float) json.optDouble("x1");
        float x2 = (float) json.optDouble("x2");
        float y1 = (float) json.optDouble("y1");
        float y2 = (float) json.optDouble("y2");
    }

    public static void receiveCircle(JSONObject json) {
        float x1 = (float) json.optDouble("x1");
        float x2 = (float) json.optDouble("x2");
        float y1 = (float) json.optDouble("y1");
        float y2 = (float) json.optDouble("y2");
    }

    public static void receiveRect(JSONObject json) {
        float x1 = (float) json.optDouble("x1");
        float x2 = (float) json.optDouble("x2");
        float y1 = (float) json.optDouble("y1");
        float y2 = (float) json.optDouble("y2");
    }



    public static void sendPoint(float x, float y, Color color) {
        JSONObject object = new JSONObject();
        try {
            object.put("color", color.toString());
            object.put("type", "point");
            object.put("x", x);
            object.put("y", y);
            String msg = object.toString();
            sendString(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void sendLine(float x1, float y1, float x2, float y2) {
        JSONObject object = new JSONObject();
        try {
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

    public static void sendCircle(float x1, float y1, float x2, float y2) {
        JSONObject object = new JSONObject();
        try {
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
    public static void sendRect(float x1, float y1, float x2, float y2) {
        JSONObject object = new JSONObject();
        try {
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
