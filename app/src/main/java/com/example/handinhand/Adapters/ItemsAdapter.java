package com.example.handinhand.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.handinhand.Models.ItemsPaginationObject;
import com.example.handinhand.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 1;
    private static final int VIEW_TYPE_ITEM = 2;
    private OnItemClickListener itemClickListener;
    List<ItemsPaginationObject.Data> itemsList;
    boolean lastPage = false;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        /**
         * Handle when the menu icon clicked
         * @param position the position of the item
         */
        void OnMenuClicked(int position);

        /**
         * Handle when the whole layout of the item clicked
         * @param position he position of the item
         */
        void OnItemClicked(int position);
    }
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////


    public boolean isLastPage() {
        return lastPage;
    }

    public void setLastPage(boolean lastPage) {
        this.lastPage = lastPage;
    }

    public void setItemsList(List<ItemsPaginationObject.Data> itemsList){
        int lastFinish =this.itemsList.size()-1;
        int newFinish =lastFinish + itemsList.size()-1;
        this.itemsList.addAll(itemsList);
        notifyItemRangeInserted(lastFinish, newFinish);
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM || lastPage){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent,false);
            return new ItemsViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_view, parent,false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemsViewHolder){
            ((ItemsViewHolder)holder).itemTitle.setText(itemsList.get(position).getTitle());
            ((ItemsViewHolder)holder).itemPrice.setText(itemsList.get(position).getPrice());

            //TODO: add complete image url
            Picasso.get()
                    .load(itemsList.get(position).getImage())
                    .placeholder(R.drawable.ic_photo)
                    .into(((ItemsViewHolder)holder).itemImage);
        }
    }

    @Override
    public int getItemCount() {
        return (itemsList==null)? 0 : itemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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
                itemClickListener.OnItemClicked(getAdapterPosition());
            });

            itemMoreMenu.setOnClickListener(view -> {
                itemClickListener.OnItemClicked(getAdapterPosition());
            });


        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.loading_view_progressbar);
        }
    }
}
