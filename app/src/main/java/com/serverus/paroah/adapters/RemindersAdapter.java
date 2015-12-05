package com.serverus.paroah.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.serverus.paroah.DB.MyDBHandler;
import com.serverus.paroah.R;
import com.serverus.paroah.activities.ReminderPreviewActivity;
import com.serverus.paroah.models.ListInfo;

import java.util.List;

/**
 * Created by alvinvaldez on 9/20/15.
 */
public class RemindersAdapter extends CursorRecyclerViewAdapter<RemindersAdapter.ItemViewHolder>  {
    private final LayoutInflater inflater;
    private Context context;

    public RemindersAdapter(Context context, Cursor cursor){
        super(context, cursor);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.reminder_item, parent, false);

        ItemViewHolder holder = new ItemViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder viewHolder, Cursor cursor) {
        final int     id      = cursor.getInt(cursor.getColumnIndex(MyDBHandler.COLUMN_ID));
        final String  title   = cursor.getString(cursor.getColumnIndex(MyDBHandler.COLUMN_TITLE_REMINDER));
        final String  desc    = cursor.getString(cursor.getColumnIndex(MyDBHandler.COLUMN_DESC_REMINDER));
        final String  date    = cursor.getString(cursor.getColumnIndex(MyDBHandler.COLUMN_DATE_REMINDER));

        viewHolder.title.setText(title);

        // pass id to viewholder to get in swipe
        viewHolder.id   = id;
        viewHolder.date = date;
        viewHolder.desc = desc;

        removeTopDevider(viewHolder);
    }

    //this removes the top padding of cardview to remove the divider
    private void removeTopDevider(ItemViewHolder viewHolder){
        if(viewHolder.getLayoutPosition() == 0){
            viewHolder.parentLayout.setPadding(0,0,0,0);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public int id;
        public String date;
        public String desc;
        public TextView title;
        private CardView layoutContainer;
        private RelativeLayout parentLayout;
        public ItemViewHolder(View itemView) {
            super(itemView);

            parentLayout    = (RelativeLayout) itemView.findViewById(R.id.parentLayout);
            title           = (TextView) itemView.findViewById(R.id.reminderTitle);
            layoutContainer = (CardView) itemView.findViewById(R.id.cv);

            layoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent previewReminder = new Intent(context, ReminderPreviewActivity.class);
                    previewReminder.putExtra("title", title.getText());
                    previewReminder.putExtra("desc", desc);
                    previewReminder.putExtra("date", date);

                    context.startActivity(previewReminder);
                }
            });
        }
    }
}
