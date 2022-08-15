package com.example.chorelogger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    public static final String USER_ID= "com.example.chorelogger.USER_ID";
    public static String userID ="--Select User--";

    TextView userSelected;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Spinner spinner=findViewById(R.id.spinner1);
        DateUtil.setDate = DateUtil.getCurrentDate();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userID = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final Button button = (Button) findViewById(R.id.button);
        userSelected = findViewById(R.id.user_display);

        Intent intent = getIntent();
        final String user = intent.getStringExtra(MainActivity.USER_ID);

        Toast.makeText(MainActivity.this,"Firebase Connection Success", Toast.LENGTH_LONG).show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChores();
            }
        });
    }



    public void openChores(){
            Intent intent = new Intent(this, Chores.class);
            if(userID.equals("--Select User--")) {
                Toast.makeText(getApplicationContext(), "Please select a user!", Toast.LENGTH_SHORT).show();
                return;
            }

            intent.putExtra(USER_ID, userID);
            startActivity(intent);
    }

}
