package com.example.handinhand.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.handinhand.Models.EventPaginationObject;
import com.example.handinhand.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import xyz.hanks.library.bang.SmallBangView;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {
    private EventsAdapter.OnEventClickListener eventClickListener;
    private View rootView;
    List<EventPaginationObject.Data> eventsList;

    public EventsAdapter(View rootView) {
        this.rootView = rootView;
    }

    public void setOnEventClickListener(EventsAdapter.OnEventClickListener eventClickListener) {
        this.eventClickListener = eventClickListener;
    }

    public void clearAll(){
        if(eventsList != null){
            this.eventsList.clear();
            notifyDataSetChanged();
        }
    }

    public List<EventPaginationObject.Data> getEventsList() {
        return eventsList;
    }

    public EventPaginationObject.Data getEvent(int position) {
        return eventsList.get(position);
    }

    public void interestEvent(int position){
        EventPaginationObject.Data data = eventsList.get(position);
        if(data.getIs_interested()){
            data.setIs_interested(false);
            data.setInterests(data.getInterests()-1);
        }
        else{
            data.setIs_interested(true);
            data.setInterests(data.getInterests()+1);
        }
        eventsList.remove(position);
        eventsList.set(position, data);
        notifyItemChanged(position);
    }

    public interface OnEventClickListener{

        /**
         * Handle when the whole layout of the item clicked
         * @param position he position of the item
         */
        void OnEventClicked(int position, ImageView imageView);

        void OnEventInterest(int position);

        void onEventLongClicked(int position);
    }
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////


    public void setEventsList(List<EventPaginationObject.Data> eventsList){
        this.eventsList = eventsList;
        notifyDataSetChanged();
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public EventsAdapter.EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event, parent,false);
        return new EventsAdapter.EventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.EventsViewHolder holder, int position) {
        holder.eventTitle.setText(String.valueOf(eventsList.get(position).getId()));
        holder.interests.setText(String.valueOf(eventsList.get(position).getInterests()));
        holder.bangView.setSelected(eventsList.get(position).getIs_interested());

        //TODO: add complete image url
        Glide.with(rootView)
                .load("http://59cbcc73.ngrok.io/storage/events/" +
                        eventsList.get(position).getImage())
                .placeholder(R.drawable.ic_photo)
                .into((holder).eventImage);

    }

    @Override
    public int getItemCount() {
        return (eventsList==null)? 0 : eventsList.size();
    }



    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    public class EventsViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        ImageView eventImage;
        TextView eventTitle;
        TextView interests;
        SmallBangView bangView;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.event_material_card);
            eventImage = itemView.findViewById(R.id.event_image);
            eventTitle = itemView.findViewById(R.id.event_name);
            interests = itemView.findViewById(R.id.event_number_of_interests);
            bangView = itemView.findViewById(R.id.like_animation);

            cardView.setOnClickListener(view -> {
                eventClickListener.OnEventClicked(getAdapterPosition(), eventImage);
            });

            bangView.setOnClickListener(view -> {
                bangView.likeAnimation();
                eventClickListener.OnEventInterest(getAdapterPosition());
            });

            cardView.setOnLongClickListener(view -> {
                eventClickListener.onEventLongClicked(getAdapterPosition());
                return true;
            });

        }
    }
}
