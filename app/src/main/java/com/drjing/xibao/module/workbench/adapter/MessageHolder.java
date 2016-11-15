package com.drjing.xibao.module.workbench.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.drjing.xibao.R;
import com.drjing.xibao.module.entity.MessageEntity;

/**
 * Created by kristain on 15/12/31.
 */
public class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView textTitle;
    TextView delet_btn;
    TextView textDuration;

    TextView textSinger;
    public IMyViewHolderClicks mListener;

    View vi;

    MessageEntity msong;

    public MessageHolder(View itemView, IMyViewHolderClicks listener)
    {
        super(itemView);
        mListener = listener;
        vi = itemView;
        textTitle = (TextView) itemView.findViewById( R.id.textTitle);
        delet_btn = (TextView) itemView.findViewById(R.id.delet_btn);
        itemView.setOnClickListener(this);
       // delet_btn.setOnClickListener(this);
    }

    public void bindSong(MessageEntity song,int pos)
    {
        msong = song;
        vi.setTag(pos);
        textTitle.setText(song.getMsg());
    }

    @Override
    public void onClick(View view)
    {

        mListener.onPlay(view);

    }

    public static interface IMyViewHolderClicks {
        public void onPlay(View caller);
    }

}
