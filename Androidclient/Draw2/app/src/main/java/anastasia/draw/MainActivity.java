package anastasia.draw;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;


public class MainActivity extends Activity implements  OnClickListener {

    public static class PostPointAsyncTask extends AsyncTask<String, Void, String>  {
        Socket socket;

        @Override
        protected String doInBackground(String... strings) {
            String msg = strings[0];
            String msgret = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeUTF(msg);
                out.flush();
                msgret = in.readUTF().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(out != null)
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if(in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return msgret;
        }


        protected void onPostExecute(String... strings) {

        }
/*
        public PostPointAsyncTask() throws IOException {
            socket = new Socket("192.168.43.64", PORT);
            System.out.println();
        }
*/


        /*
        @Override
        protected Boolean doInBackground(Float ... point) {
            float x = point[0];
            float y = point[1];
            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeFloat(x);
                out.writeFloat(y);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if(out != null)
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.d("PostPointAsyncTask", String.valueOf(aBoolean));
        }*/

    }

    private DrawingView drawView;

    private ImageButton currPaint, newBtn, saveBtn;   //цвет краски

    private int currentAction=1;

    public void setcurrentAction(int c) {
        currentAction = c;
    }

    public int getCurrentAction() {return currentAction;}

    private static int PORT = 2550;

    ObjectInputStream in;
    ObjectOutputStream out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        API.createSocket();

        drawView = (DrawingView)findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
    }

    public void paintClicked(View view){
        //use chosen color
        if(view!=currPaint){
//update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }

    }

    public void clickb(View view) {
        setcurrentAction(1);
        drawView.setcurrentAction(getCurrentAction());
    }

    public void clickl(View view) {
        setcurrentAction(2);
        drawView.setcurrentAction(getCurrentAction());
    }

    public void clickc(View view) {
        setcurrentAction(3);
        drawView.setcurrentAction(getCurrentAction());
    }

    public void clickr(View view) {
        setcurrentAction(4);
        drawView.setcurrentAction(getCurrentAction());
    }

    public void clickUpdate(View v) {
        API.receiveString();
    }

/*
    public void clickf(View view) {
        switch (findViewById(view.getId())) {
            case R.id.line_btn:
                setcurrentAction(2);
            case R.id.circle_btn:
                setcurrentAction(3);
            case R.id.rect_btn:
                setcurrentAction(4);
            default:
                setcurrentAction(0);
        }
        System.out.println("currentActionM1 = " + getCurrentAction());
        drawView.setcurrentAction(getCurrentAction());
        System.out.println("currentActionM2 = " + getCurrentAction());
    }
*/
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.new_btn){
            //new button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("Новый рисунок");
            newDialog.setMessage("Начать новый рисунок(потерять старый)?");
            newDialog.setPositiveButton("Да", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();

        }
        else if(view.getId()==R.id.save_btn){
            //save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Сохранить рисунок");
            saveDialog.setMessage("Сохранить рисунок в галерею устройства?");
            saveDialog.setPositiveButton("Да", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    //save drawing
                }
            });
            saveDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();


            drawView.setDrawingCacheEnabled(true);

            String imgSaved = MediaStore.Images.Media.insertImage(
                    getContentResolver(), drawView.getDrawingCache(),
                    UUID.randomUUID().toString()+".png", "drawing");
            if(imgSaved!=null){
                Toast savedToast = Toast.makeText(getApplicationContext(),
                        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                savedToast.show();
            }
            else{
                Toast unsavedToast = Toast.makeText(getApplicationContext(),
                        "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                unsavedToast.show();
            }
            drawView.destroyDrawingCache();
        }
    }
}



