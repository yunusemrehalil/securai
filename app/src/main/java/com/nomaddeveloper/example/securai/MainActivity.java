package com.nomaddeveloper.example.securai;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.nomaddeveloper.example.securai.databinding.ActivityMainBinding;
import com.nomaddeveloper.example.securai.network.request.TestRequest;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;
    private Button makeRequest;
    private TextView result;
    private TextView request;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        setViews();
    }

    private void setViews() {
        makeRequest = binding.btnRequest;
        result = binding.tvResult;
        request = binding.tvRequest;

        makeRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        if (v != null) {
            if (v.getId() == makeRequest.getId()) {
                TestRequest.mockPostRequest();
            } else if (v.getId() == result.getId()) {
                result.setText("not secured");
            }
        }
    }
}