package com.example.taskmanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TacVuActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton btnThemTacVu;
    private ImageButton btnTroVe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tac_vu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tacvu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recyclerView);
        btnThemTacVu = findViewById(R.id.btnThemTacVu);
        btnTroVe = findViewById(R.id.btnTroVe);
        recyclerView.setLayoutManager(new LinearLayoutManager(TacVuActivity.this));
        recyclerView.setHasFixedSize(true);

        btnThemTacVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemTacVuActivity.newInstance().show(getSupportFragmentManager(), ThemTacVuActivity.TAG);
            }
        });

        btnTroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TacVuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}