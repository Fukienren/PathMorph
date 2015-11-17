package com.mlzhong.pathmorph;

import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.mlzhong.morphlib.PathDrawable;
import com.mlzhong.morphlib.PathUtils;
import com.mlzhong.morphlib.PointFs;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    PathDrawable mPathDrawbale;

    static final String testStrings[] = {

            "abc","ABC",
            "zho","ZHO",
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
            "0","1","2","3","4","5","6","7","8","9","@","#",

            "钟","明" ,"亮",

    };

    List<List<PointFs>> llSource;
    List<List<PointFs>> llDest;


    int du = 1000;
    int fontsize = 500;
    int gtestindex = 0;

    int meaturecount = 280;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Path ptxt = PathUtils.getPath(testStrings[0], fontsize);
        llSource = PathUtils.getPathPoints(ptxt, meaturecount);

        ptxt = PathUtils.getPath(testStrings[1], fontsize);
        llDest = PathUtils.getPathPoints(ptxt, meaturecount);



        Paint p = new Paint();
        p.setColor(0xFFac2a9d);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(10);
        p.setDither(true);
        p.setPathEffect(new CornerPathEffect(10));
        mPathDrawbale = new PathDrawable(llSource,llDest, p);

        ((ImageView)super.findViewById(R.id.imageview)).setImageDrawable(mPathDrawbale);
    }


    @Override
    public void onPause(){
        super.onPause();
        mPathDrawbale.stop();
    }


    class MyRunnable implements Runnable{
        @Override
        public void run() {
            if (mPathDrawbale.isRunning()) {

                if(gtestindex >= testStrings.length){
                    gtestindex = 0;
                }

                Path ptxt = PathUtils.getPath(testStrings[gtestindex], fontsize);
                llSource = PathUtils.getPathPoints(ptxt, meaturecount);
                gtestindex ++;
                if(gtestindex >= testStrings.length){
                    gtestindex = 0;
                }


                ptxt = PathUtils.getPath(testStrings[gtestindex], fontsize);
                llDest = PathUtils.getPathPoints(ptxt, meaturecount);

                mPathDrawbale.stop();
                mPathDrawbale.setPoints(llSource,llDest);
                mPathDrawbale.start();

                getWindow().getDecorView().
                        postDelayed(new MyRunnable(), du + 100);
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();


        mPathDrawbale.setAnimDuration(du);
        mPathDrawbale.start();
        getWindow().getDecorView().
        postDelayed(new MyRunnable(), du + 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
