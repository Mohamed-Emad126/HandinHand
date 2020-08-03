package com.example.handinhand.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.handinhand.Models.EventDescription;
import com.example.handinhand.R;

import java.util.List;

public class InterestersAdapter extends RecyclerView.Adapter<InterestersAdapter.InterestersViewHolder> {
    private final View rootView;
    List<EventDescription.Interesters> interesters;

    public InterestersAdapter(View rootView) {
        this.rootView = rootView;
    }

    public void clearAll() {
        if (interesters != null) {
            this.interesters.clear();
            notifyDataSetChanged();
        }
    }

    public void setEventsList(List<EventDescription.Interesters> eventsList) {
        this.interesters = eventsList;
        notifyDataSetChanged();
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public InterestersAdapter.InterestersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest, parent, false);
        return new InterestersAdapter.InterestersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestersAdapter.InterestersViewHolder holder, int position) {
        holder.eventUserName.setText(
                interesters.get(position).getFirst_name() + " "+
                        interesters.get(position).getLast_name()
                );

        //TODO: add complete image url
        Glide.with(rootView)
                .load("http://2a25ce9546cf.ngrok.io/storage/avatars/" +
                        interesters.get(position).getAvatar())
                .placeholder(R.drawable.ic_photo)
                .into((holder).userImage);

    }

    @Override
    public int getItemCount() {
        return (interesters == null) ? 0 : interesters.size();
    }


/////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////

    public class InterestersViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView eventUserName;

        public InterestersViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.interester_image);
            eventUserName = itemView.findViewById(R.id.user_nme);

        }
    }
}