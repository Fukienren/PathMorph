package com.mlzhong.morphlib;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.text.DynamicLayout;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mlzhong on 2015/11/16.
 */
public class PathUtils {

    public static Path getPath(String str,int fontsize){

        TextPaint p = new TextPaint();
        p.setAntiAlias(true);
        DynamicLayout dl = new DynamicLayout(str, p,
                (int) DynamicLayout.getDesiredWidth(str, p) +1,
                DynamicLayout.Alignment.ALIGN_NORMAL, 0.f, 0.f, true);
        int height = dl.getHeight();
        Path ret = new Path();
        p.setTextSize(fontsize);
        int len = str.length();
        p.getTextPath(str,0,len,0,height/2,ret);

        return ret;
    }


    public static Path getPath(String str,int fontsize,Typeface tp){

        TextPaint p = new TextPaint();
        p.setAntiAlias(true);
        p.setTypeface(tp);
        DynamicLayout dl = new DynamicLayout(str, p,
                (int) DynamicLayout.getDesiredWidth(str, p) +1,
                DynamicLayout.Alignment.ALIGN_NORMAL, 0.f, 0.f, true);
        int height = dl.getHeight();
        Path ret = new Path();
        p.setTextSize(fontsize);
        int len = str.length();
        p.getTextPath(str,0,len,0,height/2,ret);

        return ret;
    }

    public static float getTextLength(String txt, TextPaint p){
        return DynamicLayout.getDesiredWidth(txt, p);
    }

    public static float getTextLength(String txt, int fontsize){
        TextPaint p = new TextPaint();
        p.setTextSize(fontsize);

        return getTextLength(txt, p);
    }

    public static float getTextLength(String txt, int fontsize,Typeface tp){
        TextPaint p = new TextPaint();
        p.setTypeface(tp);
        p.setTextSize(fontsize);

        return getTextLength(txt, p);
    }

    public static PointF getCenterGravity(Path path){
        PathMeasure m = new PathMeasure(path, false);
        float[] pos, tan;
        float speed, distance;

        float maxMeature = 200.0f;
        maxMeature = Math.min(m.getLength(), maxMeature);
        speed = m.getLength() / maxMeature;
        pos=new float[2];
        tan=new float[2];

        PointF retF = new PointF(0,0);
        int count = 0;
        while(true ){
            distance = 0;
            while(distance < m.getLength())
            {
                if(m.getPosTan(distance, pos, tan) == false){
                    continue;
                }
                distance += speed;
                retF.x += pos[0];
                retF.y += pos[1];
                count ++;
            }
            //if(m.nextContour() == false)
            {
                break;
            }
        }
        if(count > 0) {
            retF.x /= count;
            retF.y /= count;
            return retF;
        }else{
            return null;
        }
    }


    public static PointF getCenterGravity(List<PointFs> lp){

        PointF retF = new PointF(0,0);
        int count = lp.size();
        for(int i = 0; i< count; i++){
            retF.x += lp.get(i).pos.x;
            retF.y += lp.get(i).pos.y;
        }

        if(count > 0) {
            retF.x /= count;
            retF.y /= count;
            return retF;
        }else{
            return null;
        }
    }


    public static List<List<PointFs>> getPathPoints(Path path,int len){
        List<List<PointFs>> fp = new ArrayList<List<PointFs>>();
        PathMeasure m = new PathMeasure(path, false);

        float[] pos, tan;
        float speed, distance;
        speed = m.getLength();
        speed /= len;
        pos=new float[2];
        tan=new float[2];

        while(true ){
            distance = 0;
            List<PointFs> onePath = new ArrayList<PointFs>();
            while(distance < m.getLength())
            {
                if(m.getPosTan(distance, pos, tan)){
                    PointFs ret = new PointFs();
                    ret.pos.x = pos[0];
                    ret.pos.y = pos[1];
                    ret.tan.x = tan[0];
                    ret.tan.y = tan[1];
                    onePath.add(ret);
                }
                distance += speed;
            }

            //find left ,top point
            int index = 0;
            PointF leftpt = onePath.get(0).pos;
            for(int i = 1; i < onePath.size(); i++){
                if(onePath.get(i).pos.y < leftpt.y){
                    leftpt = onePath.get(i).pos;
                    index = i;
                }else if(onePath.get(i).pos.y == leftpt.y){
                    if(onePath.get(i).pos.x < leftpt.x){
                        leftpt = onePath.get(i).pos;
                        index = i;
                    }
                }
            }

            List<PointFs> onePathTemp = new ArrayList<PointFs>();

            for(int i = index; i < onePath.size(); i++){
                onePathTemp.add(onePath.get(i));
            }

            for(int i = 0; i < index; i++){
                onePathTemp.add(onePath.get(i));
            }

            //if(onePathTemp.get(0).pos.x < onePathTemp.get(1).pos.x)
              //  Collections.reverse(onePathTemp);

            fp.add(onePathTemp);

            if(m.nextContour() == false){
                break;
            }
        }
        return fp;
    }



    public static Path mergePath(List<PointFs> oneSrc,List<PointFs> oneDst,float merge/*0~1*/){
        Path p = new Path();

        float ff = (1.0f-merge);

        if(oneSrc == null){
            PointF pt = oneDst.get(0).pos;
            pt = PathUtils.getCenterGravity(oneDst);
            p.moveTo((pt.x * ff + oneDst.get(0).pos.x * merge),
                    (pt.y * ff + oneDst.get(0).pos.y * merge));
            for (int j = 1; j < oneDst.size(); j++) {
                p.lineTo((pt.x * ff + oneDst.get(j).pos.x * merge),
                        (pt.y * ff + oneDst.get(j).pos.y * merge));
            }
            p.close();
            return p;
        }

        if(oneDst == null){
            /*
            PointFs pt = oneSrc.get(0);
            p.moveTo(oneSrc.get(0).pos.x * ff + pt.pos.x * progress,
                    oneSrc.get(0).pos.y * ff + pt.pos.y * progress);
            for (int j = 1; j < oneSrc.size(); j++) {
                p.lineTo(oneSrc.get(j).pos.x*ff + pt.pos.x*progress,
                        oneSrc.get(j).pos.y*ff + pt.pos.y*progress);
            }
            p.close();
            */
            return null;
        }


        p.moveTo((oneSrc.get(0).pos.x * ff + oneDst.get(0).pos.x * merge),
                (oneSrc.get(0).pos.y * ff + oneDst.get(0).pos.y * merge));

        {
            double ratio = oneSrc.size() / (double)oneDst.size();
            int maxx = oneSrc.size()-1;
            for (int j = 1; j < oneDst.size(); j++) {
                int jj = (int) Math.floor(ratio * j);
                jj = Math.min(jj, maxx);
                p.lineTo((oneSrc.get(jj).pos.x * ff + oneDst.get(j).pos.x * merge),
                        (oneSrc.get(jj).pos.y * ff + oneDst.get(j).pos.y * merge));
            }
        }

        p.close();
        return p;
    }
}
