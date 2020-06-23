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
import com.example.handinhand.Models.ProductPaginationObject;
import com.example.handinhand.R;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder> {

    private ProductsAdapter.OnProductClickListener productClickListener;
    private View rootView;
    List<ProductPaginationObject.Data> productsList;

    public ProductsAdapter(View rootView) {
        this.rootView = rootView;
    }

    public void setOnProductClickListener(ProductsAdapter.OnProductClickListener productClickListener) {
        this.productClickListener = productClickListener;
    }

    public void clearAll() {
        if (productsList != null) {
            this.productsList.clear();
            notifyDataSetChanged();
        }
    }

    public ProductPaginationObject.Data getItem(int position) {
        return productsList.get(position);
    }

    public interface OnProductClickListener {
        /**
         * Handle when the menu icon clicked
         *
         * @param position the position of the item
         */
        void OnMenuClicked(int position, View view);

        /**
         * Handle when the whole layout of the item clicked
         *
         * @param position he position of the item
         */
        void OnProductClicked(int position, ImageView imageView);
    }
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////


    public void setProductsList(List<ProductPaginationObject.Data> itemsList) {
        this.productsList = itemsList;
        notifyDataSetChanged();
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public ProductsAdapter.ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new ProductsAdapter.ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ProductsViewHolder holder, int position) {
        holder.productTitle.setText(String.valueOf(productsList.get(position).getId()));
        holder.productPrice.setText(productsList.get(position).getPrice());

        //TODO: add complete image url
        Glide.with(rootView)
                .load("http://86819c25b434.ngrok.io/storage/products/" + productsList.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.color.gray)
                .into(((ProductsAdapter.ProductsViewHolder) holder).productImage);
    }


    @Override
    public int getItemCount() {
        return (productsList == null) ? 0 : productsList.size();
    }
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    public class ProductsViewHolder extends RecyclerView.ViewHolder {

        LinearLayout productLinearLayout;
        ImageView productImage;
        ImageView productMoreMenu;
        TextView productTitle;
        TextView productPrice;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            productLinearLayout = itemView.findViewById(R.id.item_linear_layout);
            productImage = itemView.findViewById(R.id.item_image);
            productTitle = itemView.findViewById(R.id.item_title);
            productPrice = itemView.findViewById(R.id.item_price);
            productMoreMenu = itemView.findViewById(R.id.more_menu);

            productLinearLayout.setOnClickListener(view -> {
                productClickListener.OnProductClicked(getAdapterPosition(), productImage);
            });

            productMoreMenu.setOnClickListener(view -> {
                productClickListener.OnMenuClicked(getAdapterPosition(), productMoreMenu);
            });

        }
    }
}
