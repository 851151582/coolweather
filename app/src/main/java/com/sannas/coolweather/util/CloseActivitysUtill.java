package com.sannas.coolweather.util;

import android.app.Activity;

import java.util.LinkedList;

/**
 * Created by Administrator on 2017/1/16.
 * 关闭所有Activity
 */

public class CloseActivitysUtill{
    private static LinkedList<Activity> acys = new LinkedList<Activity>();

    //添加activity
    public static void add(Activity acy){
        acys.add(acy);
    }

    //关闭所有的activity
    public static void close(){
        Activity acy;
        while (acys.size() != 0){

            //poll()方法检索并移除此列表的头元素(第一个元素)
            acy = acys.poll();
            if (!acy.isFinishing()){
                acy.finish();
            }
        }
    }
}
