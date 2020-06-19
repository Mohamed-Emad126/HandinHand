package com.example.handinhand.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.handinhand.Models.ServiceDescription;
import com.example.handinhand.R;

import java.util.List;

public class InterestersServiceAdapter  extends RecyclerView.Adapter<InterestersServiceAdapter.InterestersViewHolder> {
    private final View rootView;
    List<ServiceDescription.Interesters> interesters;

    public InterestersServiceAdapter(View rootView) {
        this.rootView = rootView;
    }

    public void clearAll() {
        if (interesters != null) {
            this.interesters.clear();
            notifyDataSetChanged();
        }
    }

    public void setEventsList(List<ServiceDescription.Interesters> eventsList) {
        this.interesters = eventsList;
        notifyDataSetChanged();
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public InterestersServiceAdapter.InterestersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest, parent, false);
        return new InterestersServiceAdapter.InterestersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestersServiceAdapter.InterestersViewHolder holder, int position) {
        holder.eventUserName.setText(
                interesters.get(position).getFirst_name() + " "+
                        interesters.get(position).getLast_name()
        );

        //TODO: add complete image url
        Glide.with(rootView)
                .load(R.string.avatar_url +
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