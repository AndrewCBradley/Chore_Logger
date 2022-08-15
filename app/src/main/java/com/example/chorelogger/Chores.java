package com.example.chorelogger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class Chores extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ListView mChoresListView;
    private FirebaseFirestore mFirestore;

    private TextView textCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chores);
        Calendar c = Calendar.getInstance();

        textCurrentDate = findViewById(R.id.text_current_day);
        textCurrentDate.setText(DateUtil.setDate);


        mFirestore = FirebaseFirestore.getInstance();
        mChoresListView = findViewById(R.id.chore_list_view);


        final List<String> mChoreNames = new ArrayList<>();
        final List<String> mCompletedByNames = new ArrayList<>();


        final String userID = MainActivity.userID;
        TextView userID1 =(TextView) findViewById(R.id.user_display);
        userID1.setText("User: " + userID);


        Button buttonChangeDate = (Button) findViewById(R.id.button_change_date);
        buttonChangeDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });


        Button addChoreButton = findViewById(R.id.button_add_chore);
        addChoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Chores.this);
                View innerView = getLayoutInflater().inflate(R.layout.create_chore_dialog, null);

                final EditText choreNameEditText = innerView.findViewById(R.id.createChoreName);
                final Button createChoreButton = innerView.findViewById(R.id.createChoreSubmitButton);

                createChoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String choreName = choreNameEditText.getText().toString();
                        if (!choreName.isEmpty()){
                            DocumentReference ref = mFirestore.collection("chores").document(choreName);
                            Map<String, Object> chore = new HashMap<>();
                            chore.put("completedBy", "INCOMPLETE");
                            chore.put("choreName", choreName);
                            chore.put("date", DateUtil.getCurrentDate());

                            ref.set(chore).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> innerTask) {
                                    if(innerTask.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Chore creation successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), Chores.class));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Oh oh, a woopsy happened.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "Input a chore name", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialogBuilder.setView(innerView);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        CollectionReference choresCollection = mFirestore.collection("chores");
        Query query = choresCollection.whereEqualTo("date", DateUtil.setDate);
        Task<QuerySnapshot> choresSnap = query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().size()==0&&DateUtil.getCurrentDate().equals(DateUtil.setDate)){
                        for (ChoresEnum chore:ChoresEnum.values()){
                            String choreName = chore.getName();
                            DocumentReference ref = mFirestore.collection("chores").document(choreName);
                            Map<String, Object> choreMap = new HashMap<>();
                            choreMap.put("completedBy", "INCOMPLETE");
                            choreMap.put("choreName", choreName);
                            choreMap.put("date", DateUtil.getCurrentDate());
                            ref.set(choreMap);
                        }
                        startActivity(new Intent(getApplicationContext(),Chores.class));
                    }
                    for(QueryDocumentSnapshot docSnap : task.getResult()) {
                        Map<String, Object> chores = docSnap.getData();
                        mChoreNames.add(Objects.requireNonNull(chores.get("choreName")).toString());
                        mCompletedByNames.add(Objects.requireNonNull(chores.get("completedBy")).toString());
                    }

                    ListItemsAdapter adapter = new ListItemsAdapter(getApplicationContext(), mChoreNames, mCompletedByNames);
                    mChoresListView.setAdapter(adapter);
                } else {
                    Toast.makeText(getApplicationContext(),"Failed to retrieve chores information", Toast.LENGTH_SHORT);
                }
            }
        });

        mChoresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Chores.this);
                View innerView = getLayoutInflater().inflate(R.layout.complete_chore_dialog, null);

                final Button confirmButton = innerView.findViewById(R.id.completeChoreButton);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentReference ref = mFirestore.collection("chores").document(mChoreNames.get(position));
                        Map<String, Object> info = new HashMap<>();
                        info.put("completedBy", userID);
                        info.put("choreName", mChoreNames.get(position));
                        info.put("date", DateUtil.setDate);
                        ref.set(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Successfully finished chore!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Chores.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Oh oh, a woopsy happened!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                });

                dialogBuilder.setView(innerView);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();

            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        DateUtil.setDate = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());
        textCurrentDate.setText(DateUtil.setDate);
        startActivity(new Intent(getApplicationContext(), Chores.class));
    }
}
