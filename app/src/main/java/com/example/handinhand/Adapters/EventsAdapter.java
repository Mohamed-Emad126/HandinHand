package com.example.handinhand.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.handinhand.Models.EventPaginationObject;
import com.example.handinhand.Models.ItemsPaginationObject;
import com.example.handinhand.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

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

    public EventPaginationObject.Data getEvent(int position) {
        return eventsList.get(position);
    }

    public interface OnEventClickListener{

        /**
         * Handle when the whole layout of the item clicked
         * @param position he position of the item
         */
        void OneventClicked(int position, ImageView imageView);
    }
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////


    public void setEventsList(List<EventPaginationObject.Data> eventsList){
        if(this.eventsList == null ){
            this.eventsList = eventsList;
            notifyDataSetChanged();
        }
        else{
            int lastFinish =this.eventsList.size()-1;
            this.eventsList.addAll(eventsList);
            notifyItemInserted(lastFinish);
        }

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

        //TODO: add complete image url
        Glide.with(rootView)
                .load("http://59cbcc73.ngrok.io/storage/items/" +
                        eventsList.get(position).getImage())
                .placeholder(R.color.gray)
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

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.event_material_card);
            eventImage = itemView.findViewById(R.id.event_image);
            eventTitle = itemView.findViewById(R.id.event_name);
            interests = itemView.findViewById(R.id.event_number_of_interests);

            cardView.setOnClickListener(view -> {
                eventClickListener.OneventClicked(getAdapterPosition(), eventImage);
            });



        }
    }
}
