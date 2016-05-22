package com.harryio.taskhive.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.harryio.taskhive.R;
import com.harryio.taskhive.adapter.MessageListAdapter;
import com.harryio.taskhive.service.Singleton;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ch.dissem.bitmessage.entity.Plaintext;
import ch.dissem.bitmessage.entity.valueobject.Label;

public class MessageListFragment extends ListFragment {
    private static final String TAG = MessageListFragment.class.getSimpleName();
    private static final String ARG_LABEL = "com.harryio.ARG_LABEl";

    private Unbinder unbinder;
    private OnFragmentInteractionListener mListener;
    private Label currentLabel;

    public static MessageListFragment getInstance(Label label) {
        MessageListFragment fragment = new MessageListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LABEL, label);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentLabel = (Label) getArguments().getSerializable(ARG_LABEL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updateList(currentLabel);
    }

    @OnClick(R.id.fab)
    public void onClick() {
        //todo implement
    }

    public void updateList(Label label) {
        currentLabel = label;
        mListener.updateTitle(label.toString());

        List<Plaintext> messages = Singleton.getMessageRepository(getContext())
                .findMessages(label);
        MessageListAdapter adapter = new MessageListAdapter(getContext(), messages);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mListener.onItemClick((Plaintext) l.getItemAtPosition(position));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface OnFragmentInteractionListener {
        void onItemClick(Plaintext message);
        void updateTitle(String title);
    }
}
