package com.example.stinternship.Adapters;


import android.graphics.Paint;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stinternship.Models.TaskModel;
import com.example.stinternship.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.stinternship.MainActivity.dialogbox;

import static com.example.stinternship.MainActivity.noBtn;
import static com.example.stinternship.MainActivity.yesBtn;

public class TaskAdapter extends FirebaseRecyclerAdapter<TaskModel, TaskAdapter.myViewHolder> {


    public TaskAdapter(@NonNull FirebaseRecyclerOptions<TaskModel> options) {
        super(options);
    }

    @NonNull
    @Override
    public TaskAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item,parent,false);
        return new myViewHolder(view);
    }


    protected void onBindViewHolder(@NonNull final TaskAdapter.myViewHolder holder, final int position, @NonNull final TaskModel model) {
        holder.task_title.setText(model.getTitle());
        holder.task_desc.setText(model.getDescription());
        holder.task_sdate.setText(model.getStartDate());
        holder.task_edate.setText(model.getEndDate());

        holder.isCompletedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(model.getCompleted()){
                    FirebaseDatabase.getInstance().getReference().child("Tasks").child(getRef(position).getKey()).child("completed").setValue(false);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Tasks").child(getRef(position).getKey()).child("completed").setValue(true);
                }
            }
        });

        if(model.getCompleted())
        {
            holder.task_title.setPaintFlags(holder.task_title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.task_desc.setPaintFlags(holder.task_title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.isCompletedBtn.setText("Completed");
        }else{
            holder.task_title.setPaintFlags(holder.task_title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.task_desc.setPaintFlags(holder.task_title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.isCompletedBtn.setText("InComplete");
        }

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogbox.setVisibility(VISIBLE);
                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference().child("Tasks").child(getRef(position).getKey()).removeValue();
                        dialogbox.setVisibility(INVISIBLE);
                    }
                });
                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogbox.setVisibility(INVISIBLE);
                }
                });
            }
        });

        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(v.getContext())
                        .setContentHolder(new ViewHolder(R.layout.edit_task_dialog))
                        .setExpanded(true,1000)
                        .create();

                View myView = dialogPlus.getHolderView();
                final EditText edit_title = myView.findViewById(R.id.edit_task_title);
                final EditText edit_desc = myView.findViewById(R.id.edit_task_desc);
                final EditText edit_start = myView.findViewById(R.id.edit_task_start_date);
                final EditText edit_end = myView.findViewById(R.id.edit_task_end_date);
                final ImageView back_arrow = myView.findViewById(R.id.edit_task_back_arrow);
                Button edit_done = myView.findViewById(R.id.done_btn);

                edit_title.setText(model.getTitle());
                edit_desc.setText(model.getDescription());
                edit_start.setText(model.getStartDate());
                edit_end.setText(model.getEndDate());

                dialogPlus.show();

                back_arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogPlus.dismiss();
                    }
                });

                edit_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("title",edit_title.getText().toString());
                        map.put("description",edit_desc.getText().toString());
                        map.put("startDate",edit_start.getText().toString());
                        map.put("endDate",edit_end.getText().toString());
                        map.put("completed",false);

                        FirebaseDatabase.getInstance().getReference().child("Tasks").child(getRef(position).getKey())
                                .setValue(map);

                        dialogPlus.dismiss();

                    }
                });
            }
        });

    }


    class myViewHolder extends RecyclerView.ViewHolder {

        TextView task_title,task_desc,task_sdate,task_edate,isCompletedBtn;
        View v;
        ImageView deleteIcon,editIcon;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            task_title = itemView.findViewById(R.id.task_title);
            task_desc = itemView.findViewById(R.id.task_desc);
            task_sdate = itemView.findViewById(R.id.start_date);
            task_edate = itemView.findViewById(R.id.end_date);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
            editIcon = itemView.findViewById(R.id.edit_icon);
            isCompletedBtn = itemView.findViewById(R.id.is_complete_btn);
            v=itemView;


        }
    }
}
