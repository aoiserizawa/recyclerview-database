package com.serverus.paroah.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.serverus.paroah.DB.MyDBHandler;
import com.serverus.paroah.R;
import com.serverus.paroah.models.ListInfo;

import java.util.Collections;
import java.util.List;

/**
 * Created by alvinvaldez on 9/20/15.
 */
public class RemindersAdapter extends CursorRecyclerViewAdapter<RemindersAdapter.ItemViewHolder> {
    private final LayoutInflater inflater;
    List<ListInfo> data = Collections.emptyList();
    private Context context;
    ListInfo temporaryBucket;

    public RemindersAdapter(Context context, Cursor cursor){
        super(context, cursor);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.reminder_item, parent, false);

        ItemViewHolder holder = new ItemViewHolder(view);
        temporaryBucket = new ListInfo();

        return holder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder viewHolder, Cursor cursor) {
            final int     id      = cursor.getInt(cursor.getColumnIndex(MyDBHandler.COLUMN_ID));
            final String  title   = cursor.getString(cursor.getColumnIndex(MyDBHandler.COLUMN_TITLE_REMINDER));
            final String  desc    = cursor.getString(cursor.getColumnIndex(MyDBHandler.COLUMN_DESC_REMINDER));
            final String  date    = cursor.getString(cursor.getColumnIndex(MyDBHandler.COLUMN_DATE_REMINDER));

            viewHolder.title.setText(title);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(context, String.valueOf(id), Toast.LENGTH_SHORT).show();
                }
            });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView title;

        public ItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.reminderTitle);
        }

    }
}
