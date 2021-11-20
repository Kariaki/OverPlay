package com.overplay.overplay;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.overplay.overplay.databinding.ActivityMain2Binding;

import javax.inject.Inject;

public class MainActivity2 extends AppCompatActivity {



    private ActivityMain2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        String value="i am going home next week";
        Log.d(TAG, "onCreate: "+value);


    }
}