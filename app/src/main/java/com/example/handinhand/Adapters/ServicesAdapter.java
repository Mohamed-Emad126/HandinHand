package com.example.handinhand.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handinhand.Models.ServicePaginationObject;
import com.example.handinhand.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import xyz.hanks.library.bang.SmallBangView;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServicesViewHolder> {
    private ServicesAdapter.OnServiceClickListener serviceClickListener;
    private View rootView;
    List<ServicePaginationObject.Data> servicesList;

    public ServicesAdapter(View rootView) {
        this.rootView = rootView;
    }

    public void setOnServiceClickListener(ServicesAdapter.OnServiceClickListener serviceClickListener) {
        this.serviceClickListener = serviceClickListener;
    }

    public void clearAll() {
        if (servicesList != null) {
            this.servicesList.clear();
            notifyDataSetChanged();
        }
    }

    public List<ServicePaginationObject.Data> getServicesList() {
        return servicesList;
    }

    public ServicePaginationObject.Data getService(int position) {
        return servicesList.get(position);
    }

    public void interestService(int position) {
        ServicePaginationObject.Data data = servicesList.get(position);
        if (data.getIs_interested()) {
            data.setIs_interested(false);
            data.setInterests(data.getInterests() - 1);
        } else {
            data.setIs_interested(true);
            data.setInterests(data.getInterests() + 1);
        }
        servicesList.remove(position);
        servicesList.set(position, data);
        notifyItemChanged(position);
    }

    public interface OnServiceClickListener {

        /**
         * Handle when the whole layout of the item clicked
         *
         * @param position he position of the item
         */
        void OnServiceClicked(int position);

        void OnServiceInterest(int position);

        void onServiceLongClicked(int position);
    }
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////


    public void setServicesList(List<ServicePaginationObject.Data> servicesList) {
        this.servicesList = servicesList;
        notifyDataSetChanged();
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public ServicesAdapter.ServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service, parent, false);
        return new ServicesAdapter.ServicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesAdapter.ServicesViewHolder holder, int position) {
        holder.serviceTitle.setText(servicesList.get(position).getTitle());
        holder.serviceDescription.setText(servicesList.get(position).getDescription());
        holder.bangView.setSelected(servicesList.get(position).getIs_interested());
    }

    @Override
    public int getItemCount() {
        return (servicesList == null) ? 0 : servicesList.size();
    }


    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    public class ServicesViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        TextView serviceTitle;
        TextView serviceDescription;
        SmallBangView bangView;

        public ServicesViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.service_material_card);
            serviceTitle = itemView.findViewById(R.id.service_title);
            serviceDescription = itemView.findViewById(R.id.service_description);
            bangView = itemView.findViewById(R.id.small_bang);

            cardView.setOnClickListener(view -> {
                serviceClickListener.OnServiceClicked(getAdapterPosition());
            });

            bangView.setOnClickListener(view -> {
                serviceClickListener.OnServiceInterest(getAdapterPosition());
                bangView.likeAnimation();
            });

            cardView.setOnLongClickListener(view -> {
                serviceClickListener.onServiceLongClicked(getAdapterPosition());
                return true;
            });

        }
    }
}