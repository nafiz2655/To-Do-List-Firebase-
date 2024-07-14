package com.example.to_dolist;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class Home extends AppCompatActivity {


    ImageView addButon;
    LottieAnimationView progerssbar;

    RecyclerView relativelayout;

    private TodoAdapter todoAdapter;
    private ArrayList<DataModel> todoList;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        addButon = findViewById(R.id.addButon);
        progerssbar = findViewById(R.id.progerssbar);
        relativelayout = findViewById(R.id.relativelayout);


        todoList = new ArrayList<>();
        todoAdapter = new TodoAdapter(todoList, this);
        relativelayout.setAdapter(todoAdapter);

        progerssbar.setVisibility(View.VISIBLE);

        fetchDataFromFirebase();



        addButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowDilog();


            }
        });

    }

    private void ShowDilog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        EditText ed_todo = dialog.findViewById(R.id.todo);
        EditText title = dialog.findViewById(R.id.title);
        TextView bt_add = dialog.findViewById(R.id.addItem);

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progerssbar.setVisibility(View.VISIBLE);
                String totdo = ed_todo.getText().toString();
                String stitle = title.getText().toString();
                Date d = new Date();
                String sdate  = (String) DateFormat.format("MMMM d, yyyy ", d.getTime());
                String check = "false";
                AddDataInFirebase(stitle,totdo,sdate,dialog,check);
            }
        });



    }

    private void AddDataInFirebase(String tile, String todo, String date, Dialog dialog, String check) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DataModel dataModel = new DataModel(tile,todo,date,check);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TODOLIST");

        databaseReference.child(firebaseUser.getUid()).push().setValue(dataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                try {
                if (task.isSuccessful()){
                    Toast.makeText(Home.this, "Data insert successfull", Toast.LENGTH_SHORT).show();
                    progerssbar.setVisibility(View.GONE);
                    dialog.dismiss();

                }else {

                    Toast.makeText(Home.this, "SomeThink Working is error", Toast.LENGTH_SHORT).show();
                    progerssbar.setVisibility(View.GONE);
                    dialog.dismiss();

                }
                }catch (Exception e){
                    progerssbar.setVisibility(View.GONE);
                    dialog.dismiss();
                    Toast.makeText(Home.this, "SomeThink Working is error\n"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void fetchDataFromFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TODOLIST").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataModel dataModel = snapshot.getValue(DataModel.class);
                    todoList.add(dataModel);
                }
                todoAdapter.notifyDataSetChanged();

                progerssbar.setVisibility(View.GONE);

                int i = todoList.size();
                if (i==0){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Home.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}