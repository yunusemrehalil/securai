package com.nomaddeveloper.securai;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.nomaddeveloper.securai.databinding.ActivityMainBinding;
import com.nomaddeveloper.securai.network.request.TestRequest;

public class MainActivity extends AppCompatActivity {
    private static ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        TestRequest.mockPostRequest();
    }
}