package com.overplay.overplay.DependencyInjection;

import static android.content.ContentValues.TAG;

import android.util.Log;

import javax.inject.Inject;

public class Remote {

    @Inject
    public Remote(){

    }
    public void setListener(Car car){
        Log.d(TAG, "setListener: listening to drive");
    }
}
