package com.example.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.databinding.ItemRecieveBinding;
import com.example.chatapp.databinding.ItemRecieveGroupBinding;
import com.example.chatapp.databinding.ItemSendBinding;
import com.example.chatapp.databinding.ItemSendGroupBinding;
import com.example.chatapp.model.Message;
import com.example.chatapp.model.User;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupMessagesAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;


    public GroupMessagesAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_send_group, parent, false);
            return new SendViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recieve_group, parent, false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        int [] reactions = {
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
//            Log.e("index",pos+"");
            if(holder.getClass() == SendViewHolder.class){
                if(pos>=0){
                    SendViewHolder viewHolder = (SendViewHolder) holder;
                    viewHolder.binding.feeling.setImageResource(reactions[pos]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                }
            } else {
                if(pos>=0){
                    RecieverViewHolder viewHolder = (RecieverViewHolder) holder;
                    viewHolder.binding.feeling.setImageResource(reactions[pos]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                }
            }

            message.setFeeling(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child("public")
                    .child(message.getMessageId()).setValue(message);
            return true; // true is closing popup, false is requesting a new selection
        });

        if(holder.getClass() == SendViewHolder.class){
            SendViewHolder viewHolder = (SendViewHolder)holder;

            if(message.getMessage().equals("photo")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl()).
                        placeholder(R.drawable.placeholder).
                        into(viewHolder.binding.image);
            }

            FirebaseDatabase.getInstance().getReference()
                    .child("users").child(message.getSenderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                User user = snapshot.getValue(User.class);
                                viewHolder.binding.name.setText("@ "+user.getName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            viewHolder.binding.message.setText(message.getMessage());

            if(message.getFeeling()>= 0){
                //message.setFeeling(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view, motionEvent);
                    return false;
                }
            });
            viewHolder.binding.image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        } else {
            RecieverViewHolder viewHolder = (RecieverViewHolder) holder;

            if(message.getMessage().equals("photo")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl()).placeholder(R.drawable.placeholder).into(viewHolder.binding.image);
            }

            FirebaseDatabase.getInstance().getReference()
                    .child("users").child(message.getSenderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                User user = snapshot.getValue(User.class);
                                viewHolder.binding.name.setText("@ "+ user.getName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            viewHolder.binding.message.setText(message.getMessage());

            if(message.getFeeling()>= 0){
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view, motionEvent);
                    return false;
                }

            });
            viewHolder.binding.image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SendViewHolder extends RecyclerView.ViewHolder{

        ItemSendGroupBinding binding;
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSendGroupBinding.bind(itemView);
            //
        }
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder{

        ItemRecieveGroupBinding binding;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRecieveGroupBinding.bind(itemView);
        }
    }
}
