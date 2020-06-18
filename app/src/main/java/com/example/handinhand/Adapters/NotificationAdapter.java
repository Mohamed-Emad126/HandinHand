package com.example.handinhand.Adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handinhand.Models.NotificationResponse;
import com.example.handinhand.R;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationsViewHolder> {

    private NotificationAdapter.OnNotificationClickListener notificationClickListener;
    List<NotificationResponse.Data> notificationsList;

    public void setOnNotificationClickListener(NotificationAdapter.OnNotificationClickListener notificationClickListener) {
        this.notificationClickListener = notificationClickListener;
    }

    public void clearAll() {
        if (notificationsList != null) {
            this.notificationsList.clear();
            notifyDataSetChanged();
        }
    }

    public List<NotificationResponse.Data> getNotificationsList() {
        return notificationsList;
    }

    public NotificationResponse.Data getNotification(int position) {
        return notificationsList.get(position);
    }

    public interface OnNotificationClickListener {
        void OnNotificationClicked(int position);
    }
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    public void setNotificationsList(List<NotificationResponse.Data> notificationsList) {
        this.notificationsList = notificationsList;
        notifyDataSetChanged();
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public NotificationAdapter.NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification, parent, false);
        return new NotificationAdapter.NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationsViewHolder holder, int position) {
        holder.notificationIcon.setText(notificationsList.get(position).getBody().substring(0, 1));
        if (notificationsList.get(position).getUrl().contains("services")) {
            holder.notificationType.setText(R.string.services);
        } else if (notificationsList.get(position).getUrl().contains("events")) {
            holder.notificationType.setText(R.string.events);
        } else {
            holder.notificationType.setText(R.string.deal);
        }
        holder.notificationDescription.setText(notificationsList.get(position).getBody());
        holder.notificationTime.setText(
                DateUtils.getRelativeTimeSpanString(
                        Timestamp.valueOf(notificationsList.get(position).getUpdated_at()).getTime(),
                        new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_NUMERIC_DATE)
        );

        Random mRandom = new Random();
        int color = Color.argb(255,
                mRandom.nextInt(256),
                mRandom.nextInt(256),
                mRandom.nextInt(256));
        ((GradientDrawable) holder.notificationIcon.getBackground()).setColor(color);
    }

    @Override
    public int getItemCount() {
        return (notificationsList == null) ? 0 : notificationsList.size();
    }


/////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {

        TextView notificationIcon;
        TextView notificationType;
        TextView notificationDescription;
        TextView notificationTime;
        LinearLayout linearLayout;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);

            notificationIcon = itemView.findViewById(R.id.notification_icon);
            notificationType = itemView.findViewById(R.id.notification_type);
            notificationDescription = itemView.findViewById(R.id.notification_Details);
            notificationTime = itemView.findViewById(R.id.notification_Time);
            linearLayout = itemView.findViewById(R.id.layout);

            linearLayout.setOnClickListener(view -> {
                notificationClickListener.OnNotificationClicked(getAdapterPosition());
            });

        }
    }
}
