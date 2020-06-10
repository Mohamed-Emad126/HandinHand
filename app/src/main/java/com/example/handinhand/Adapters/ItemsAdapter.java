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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.handinhand.Models.ItemsPaginationObject;
import com.example.handinhand.R;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder> {

    private OnItemClickListener itemClickListener;
    private View rootView;
    List<ItemsPaginationObject.Data> itemsList;

    public ItemsAdapter(View rootView) {
        this.rootView = rootView;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void clearAll(){
        if(itemsList != null){
            this.itemsList.clear();
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position){
        notifyItemRemoved(position);
    }

    public ItemsPaginationObject.Data getItem(int position) {
        return itemsList.get(position);
    }

    public interface OnItemClickListener{
        /**
         * Handle when the menu icon clicked
         * @param position the position of the item
         */
        void OnMenuClicked(int position, View view);

        /**
         * Handle when the whole layout of the item clicked
         * @param position he position of the item
         */
        void OnItemClicked(int position, ImageView imageView);
    }
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////


    public void setItemsList(List<ItemsPaginationObject.Data> itemsList){
        if(this.itemsList == null ){
            this.itemsList = itemsList;
            notifyDataSetChanged();
        }
        else{
            int lastFinish =this.itemsList.size()-1;
            this.itemsList.addAll(itemsList);
            notifyItemInserted(lastFinish);
            //notifyItemRangeInserted(lastFinish, newFinish);
        }

    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent,false);
        return new ItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        holder.itemTitle.setText(String.valueOf(itemsList.get(position).getId()));
        holder.itemPrice.setText(itemsList.get(position).getPrice());

        //TODO: add complete image url
        Glide.with(rootView)
                .load("http://ebf4988b3761.ngrok.io/storage/items/" + itemsList.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.color.gray)
                .into(((ItemsViewHolder)holder).itemImage);

    }

    @Override
    public int getItemCount() {
        return (itemsList==null)? 0 : itemsList.size();
    }



    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    public class ItemsViewHolder extends RecyclerView.ViewHolder {

        LinearLayout itemLinearLayout;
        ImageView itemImage;
        ImageView itemMoreMenu;
        TextView itemTitle;
        TextView itemPrice;

        public ItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            itemLinearLayout = itemView.findViewById(R.id.item_linear_layout);
            itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemMoreMenu = itemView.findViewById(R.id.more_menu);

            itemLinearLayout.setOnClickListener(view -> {
                itemClickListener.OnItemClicked(getAdapterPosition(), itemImage);
            });

            itemMoreMenu.setOnClickListener(view -> {
                itemClickListener.OnMenuClicked(getAdapterPosition() , itemMoreMenu);
            });

        }
    }

}
