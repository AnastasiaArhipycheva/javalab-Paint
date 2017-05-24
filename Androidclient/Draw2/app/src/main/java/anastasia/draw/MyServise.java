package anastasia.draw;

import android.app.Service;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static anastasia.draw.API.json;

/**
 * Created by Администратор on 24.05.2017.
 */

public class MyServise extends Service {

    final String LOG_TAG = "myLogs";
    ExecutorService es;
    Canvas drawCanvas;
    Path drawPath;
    Paint drawPaint;

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "MyService onCreate");
        es = Executors.newFixedThreadPool(1);
        drawCanvas = new Canvas();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "MyService onDestroy");
        drawCanvas = null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        drawCanvas = (Canvas)intent.getParcelableExtra("canvas");
        drawPaint = (Paint)intent.getParcelableExtra("paint");
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    void someTask() {
        new Thread(new Runnable() {
            public void run() {

                update(API.json);

             //   stopSelf();
            }
        }).start();
    }

    protected void update(String json) {
        if (json!=null)
            try {
                JSONObject object = new JSONObject(json);
                if(object.optString("type").equals("point"))
                    receivePoint(object);
               /* if(object.optString("type").equals("line"))
                        receiveLine(object);
                if(object.optString("type").equals("circle"))
                        receiveCircle(object);
                if(object.optString("type").equals("rect"))
                        receiveRect(object);*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void receivePoint(JSONObject json) {
        float x = (float) json.optDouble("x");
        float y = (float) json.optDouble("y");
        System.out.println("point"+x+y);
        drawPath.moveTo(x, y);
        drawPath.lineTo(x, y);
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();
    }

    /*
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "MyService onStartCommand");
        int time = intent.getIntExtra("time", 1);
        MyRun mr = new MyRun(time, startId);
        es.execute(mr);
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    class MyRun implements Runnable {

        int time;
        int startId;

        public MyRun(int time, int startId) {
            this.time = time;
            this.startId = startId;
            Log.d(LOG_TAG, "MyRun#" + startId + " create");
        }

        public void run() {

            Log.d(LOG_TAG, "MyRun#" + startId + " start, time = " + time);

            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Log.d(LOG_TAG, "MyRun#" + startId + " someRes = " + someRes.getClass() );
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, "MyRun#" + startId + " error, null pointer");
            }



            stop();
        }

        void stop() {
            Log.d(LOG_TAG, "MyRun#" + startId + " end, stopSelf(" + startId + ")");
            stopSelf(startId);
        }

    }
    */
}
