package com.harryio.taskhive.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.harryio.taskhive.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.dissem.bitmessage.entity.Plaintext;

public class MessageListAdapter extends ArrayAdapter<Plaintext> {
    private LayoutInflater layoutInflater;

    public MessageListAdapter(Context context, List<Plaintext> objects) {
        super(context, 0, objects);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.message_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Plaintext message = getItem(position);

        holder.fromTextView.setText(message.getFrom().toString());
        holder.subjectTextView.setText(message.getSubject());
        holder.messageTextView.setText(message.getText());

        if (message.isUnread()) {
            holder.fromTextView.setTypeface(Typeface.DEFAULT_BOLD);
            holder.subjectTextView.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            holder.fromTextView.setTypeface(Typeface.DEFAULT);
            holder.subjectTextView.setTypeface(Typeface.DEFAULT);
        }

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.from_textView)
        TextView fromTextView;
        @BindView(R.id.subject_textView)
        TextView subjectTextView;
        @BindView(R.id.message_textView)
        TextView messageTextView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
