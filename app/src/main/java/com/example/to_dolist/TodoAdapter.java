package com.example.to_dolist;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private ArrayList<DataModel> todoList;
    private Context context;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    public TodoAdapter(ArrayList<DataModel> todoList, Context context) {
        this.todoList = todoList;
        this.context = context;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.datal_layout, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        DataModel dataModel = todoList.get(position);
        holder.tv_title.setText(dataModel.getTitle());
        holder.todoTextView.setText(dataModel.getTodo());
        holder.tv_date.setText(dataModel.getDate());
        String check = dataModel.getCheck();
        if (check.contains("false")){
            holder.checkBox.setChecked(false);
        }else {
            holder.checkBox.setChecked(true);

        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // show dilog

                final Dialog dialog = new Dialog(context);
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


                String sTitle = holder.tv_title.getText().toString();
                String sText = holder.todoTextView.getText().toString();
                String sDate = holder.tv_date.getText().toString();


                bt_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditItem("Title","Hello World","12/12/1200");
                    }
                });

                return true;
            }

        });
    }


    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView todoTextView,tv_title,tv_date;
        CheckBox checkBox;
        LinearLayout linearLayout;


        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            todoTextView = itemView.findViewById(R.id.todoTextView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_date = itemView.findViewById(R.id.tv_date);
            checkBox = itemView.findViewById(R.id.checkBox);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }



    private void UpdateDatabase(String sTitle, String sText, String sDate, String chaked) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TODOLIST");
        DataModel dataModel = new DataModel(sTitle,sText,sDate,chaked);
        String usureId = firebaseUser.getUid();

        databaseReference.child(usureId).setValue(dataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().build();
                    firebaseUser.updateProfile(userProfileChangeRequest);

                }
            }
        });
    }
    //Edit request-----------------------------------------------------
    private void EditItem(String sTitle, String sText, String sDate) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TODOLIST");
        DataModel dataModel = new DataModel(sTitle,sText,sDate,"false");
        databaseReference.child(firebaseUser.getUid()).setValue(dataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "data update Success", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "data can no update", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
