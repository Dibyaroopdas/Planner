package com.example.stinternship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stinternship.Adapters.TaskAdapter;
import com.example.stinternship.Models.TaskModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button addButton,addTaskButton;
    private RelativeLayout addRelativeLayout,yellowBox;
    private TextView addTaskTime,numberTask;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    ImageView backbtn;
    public static RelativeLayout dialogbox;
    private EditText addTaskTitle, addTaskDesc, endDate,startDate;
    private RecyclerView recyclerView;
    public static TaskAdapter taskAdapter;
    public static Button yesBtn,noBtn;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTaskTime = findViewById(R.id.add_task_time);
        addTaskButton = findViewById(R.id.add_task_btn);
        backbtn = findViewById(R.id.add_task_back_btn);
        addRelativeLayout = findViewById(R.id.addRelativeLayout);
        addTaskTitle = findViewById(R.id.add_task_title);
        addTaskDesc = findViewById(R.id.add_task_desc);
        recyclerView = findViewById(R.id.recycler_view_task);
        dialogbox = findViewById(R.id.dialog_box);
        yesBtn = findViewById(R.id.dialog_yes_btn);
        noBtn = findViewById(R.id.dialog_no_btn);
        endDate = findViewById(R.id.add_task_end_date);
        addButton = findViewById(R.id.add_btn);
        startDate = findViewById(R.id.add_task_start_date);
        numberTask =findViewById(R.id.number_task);


        addRelativeLayout.setVisibility(View.INVISIBLE);
        calendar = Calendar.getInstance();

        dateFormat = new SimpleDateFormat("EEE, MMM d,''yy", Locale.US);
        date = dateFormat.format(calendar.getTime());
        addTaskTime.setText(date);


        FirebaseDatabase.getInstance().getReference().child("Tasks").addValueEventListener(new ValueEventListener() {

            Integer count=0;


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    count++;
                }

                numberTask.setText(count.toString() + " tasks");
                count=0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRelativeLayout.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.GONE);

            }
        });

        final DatePickerDialog.OnDateSetListener  end,start;

        end = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();

            }
        };

        start = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartDateLabel();

            }
        };


        endDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MainActivity.this, end, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                       calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, start, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tasks");
                String taskId = ref.push().getKey();

                if(addTaskTitle.getText().toString().equals("")||addTaskDesc.getText().toString().equals("")
                        ||endDate.getText().toString().equals("") || startDate.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    HashMap<String , Object> map = new HashMap<>();
                    map.put("title",addTaskTitle.getText().toString());
                    map.put("description" , addTaskDesc.getText().toString());
                    map.put("startDate",startDate.getText().toString());
                    map.put("endDate",endDate.getText().toString());
                    map.put("completed",false);

                    assert taskId != null;
                    ref.child(taskId).setValue(map);

                    addTaskTitle.setText("");
                    addTaskDesc.setText("");
                    endDate.setText("");
                    startDate.setText("");
                    addRelativeLayout.setVisibility(View.GONE);
                    addButton.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addRelativeLayout.setVisibility(View.GONE);
                addButton.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                endDate.setText("");
                startDate.setText("");

            }
        });


        recyclerView = findViewById(R.id.recycler_view_task);
        LinearLayoutManager homeLayoutManager = new LinearLayoutManager(this);
        homeLayoutManager.setReverseLayout(true);
        homeLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(homeLayoutManager);

        FirebaseRecyclerOptions<TaskModel> options =
                new FirebaseRecyclerOptions.Builder<TaskModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Tasks"), TaskModel.class)
                        .build();



        taskAdapter = new TaskAdapter(options);
        recyclerView.setAdapter(taskAdapter);



    }

    private void updateStartDateLabel() {
        String myFormat = "EEE, MMM d, ''yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        startDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateLabel() {
        String myFormat = "EEE, MMM d, ''yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        endDate.setText(sdf.format(calendar.getTime()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        taskAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        taskAdapter.stopListening();
    }


}