package com.drjing.xibao.module.performance.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drjing.xibao.R;

import butterknife.ButterKnife;

/**
 * 动态列表页面
 * Created by kristain on 15/12/20.
 */
public class PersonRankFragment extends Fragment {



    public static PersonRankFragment newInstance(String content) {
        PersonRankFragment fragment = new PersonRankFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_rank, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
