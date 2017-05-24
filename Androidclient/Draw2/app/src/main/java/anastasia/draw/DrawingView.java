package anastasia.draw;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;
import android.view.View;

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DrawingView extends View {

/*
    ArrayList<Shape> shapes = new ArrayList<Shape>();
    ArrayList<Color> shapeFill = new ArrayList<Color>();
    ArrayList<Color> shapeStroke = new ArrayList<Color>();
    ArrayList<Float> transPercent = new ArrayList<Float>();
    Point drawStart, drawEnd;
    */

    private int currentAction = 0;

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    private float STX, STY;

    public void setXOY(float X,float Y) {
        STX=X;
        STY=Y;
    }

    public float getSTX() {return STX;}
    public float getSTY() {return STY;}

    public void setcurrentAction(int c) {
        currentAction = c;
    }

    public int getCurrentAction() {return currentAction;}

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();

    }


    private void setupDrawing() {
//get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);

//начальные свойства
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);


    }





    //просмотр в виде чертежа View
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//view given size
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//draw the canvas and the drawing path
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);


       // update(API.json);
    }

    protected void update(String json) {
        if (json!=null)
            try {
                JSONObject object = new JSONObject(json);
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

    public void receivePoint(JSONObject json) {
        float x = (float) json.optDouble("x");
        float y = (float) json.optDouble("y");
        System.out.println("point"+x+y);
        drawPath.moveTo(x, y);
        drawPath.lineTo(x, y);
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();
        invalidate();

    }

    public void receiveLine(JSONObject json) {
        float x1 = (float) json.optDouble("x1");
        float x2 = (float) json.optDouble("x2");
        float y1 = (float) json.optDouble("y1");
        float y2 = (float) json.optDouble("y2");
        //    DrawingView.setX12Y12(1,x1,x2, y1, y2);

    }

    public static void receiveCircle(JSONObject json) {
        float x1 = (float) json.optDouble("x1");
        float x2 = (float) json.optDouble("x2");
        float y1 = (float) json.optDouble("y1");
        float y2 = (float) json.optDouble("y2");
        //   DrawingView.setX12Y12(2,x1,x2, y1, y2);
    }

    public static void receiveRect(JSONObject json) {
        float x1 = (float) json.optDouble("x1");
        float x2 = (float) json.optDouble("x2");
        float y1 = (float) json.optDouble("y1");
        float y2 = (float) json.optDouble("y2");
        //   DrawingView.setX12Y12(3,x1,x2, y1, y2);
    }





    @Override
    public boolean onTouchEvent(MotionEvent event) {

      //  API.createSocket();
//detect user touch
        boolean ret=false;
        float touchX = event.getX();
        float touchY = event.getY();
 //       System.out.println("currentAction = " + getCurrentAction());

        switch (getCurrentAction()) {
            case 1: {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:  //установка новой точки
                        drawPath.moveTo(touchX, touchY);
                        API.sendPoint(touchX, touchY, paintColor);
                        setXOY(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_MOVE:   //установка линий
                        drawPath.lineTo(touchX, touchY);
                        API.sendPoint(touchX, touchY, paintColor);
                   //     API.sendLine(getSTX(), getSTY(),touchX, touchY);
                        break;
                    case MotionEvent.ACTION_UP:  //прекращение касания
                        drawCanvas.drawPath(drawPath, drawPaint);
                        drawPath.reset();
                        break;
                    default:
                        ret = false;
                }
                invalidate();
                ret = true;
                break;
            }
            case 2: {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:  //установка новой точки
                        drawPath.moveTo(touchX, touchY);
                        setXOY(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_MOVE:   //установка линий
                        drawPath.lineTo(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_UP:  //прекращение касания
                        drawPath.reset();
                        drawPath = DrawLine(touchX, touchY);
                        drawCanvas.drawPath(drawPath, drawPaint);

                        drawPath.reset();
                        break;
                    default:
                        ret = false;
                }
                invalidate();
                ret = true;
                break;
            }
            case 3: {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:  //установка новой точки
                        drawPath.moveTo(touchX, touchY);
                        setXOY(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_MOVE:   //установка линий
                        drawPath = DrawCircle(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_UP:  //прекращение касания
                        drawPath.reset();
                        drawPath = DrawCircle(touchX, touchY);
                        API.sendCircle(getSTX(), getSTY(), touchX, touchY);
                        drawCanvas.drawPath(drawPath, drawPaint);
                        drawPath.reset();
                        break;
                    default:
                        ret = false;
                }
                invalidate();
                ret = true;
                break;
            }
            case 4: {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:  //установка новой точки
                        drawPath.moveTo(touchX, touchY);
                        setXOY(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_MOVE:   //установка линий
                        drawPath = DrawRect(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_UP:  //прекращение касания
                        drawPath.reset();
                        drawPath = DrawRect(touchX, touchY);
                        API.sendRect(getSTX(), getSTY(), touchX, touchY);
                        drawCanvas.drawPath(drawPath, drawPaint);
                        drawPath.reset();
                        break;
                    default:
                        ret = false;
                }
                invalidate();
                ret = true;
                break;
            }
            default:
                drawPath.moveTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
        }
        return ret;
    }



    public void setColor(String newColor){
//set color
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }


    public Path DrawLine(float x2, float y2) {
        Path line2d = new Path();
        line2d.moveTo(getSTX(), getSTY());
        line2d.lineTo(x2, y2);
        API.sendLine(getSTX(), getSTY(), x2, y2);
        return line2d;
    }

    public Path DrawCircle( float x2, float y2) {
        Path circle2d = new Path();
        RectF oval = new RectF(getSTX(),getSTY(),x2,y2);
        circle2d.addOval(oval,Path.Direction.CW);
        return circle2d;
    }

    public Path DrawRect( float x2, float y2) {
        Path rect2d = new Path();
// just example for line could be complex shape
        rect2d.addRect(getSTX(),getSTY(), x2, y2, Path.Direction.CW);
        return rect2d;
    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public static void DrawResive(int current, float x1, float x2, float y1, float y2) {
        Path pathdraw = new Path();
        switch (current) {
            case 1 :
                pathdraw.moveTo(x1, y1);
                pathdraw.lineTo(x2, y2);
                break;
            case 2:
                RectF oval = new RectF(x1,y1,x2,y2);
                pathdraw.addOval(oval,Path.Direction.CW);
                break;
            case 3:
                pathdraw.addRect(x1,y1, x2, y2, Path.Direction.CW);
                break;
            default:
                break;
        }

    }

}
