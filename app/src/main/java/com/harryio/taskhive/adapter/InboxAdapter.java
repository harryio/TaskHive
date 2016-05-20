package com.harryio.taskhive.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.harryio.taskhive.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.dissem.bitmessage.entity.Plaintext;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxViewHolder> {
    List<Plaintext> messages;

    public InboxAdapter(List<Plaintext> messages) {
        this.messages = messages;
    }

    static class InboxViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.from_textView)
        TextView fromTextView;
        @BindView(R.id.subject_textView)
        TextView subjectTextView;
        @BindView(R.id.message_textView)
        TextView messageTextView;

        public InboxViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public InboxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_item, parent, false);
        return new InboxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InboxViewHolder holder, int position) {
        Plaintext message = messages.get(position);

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
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
