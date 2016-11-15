package com.drjing.xibao.module.performance.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drjing.xibao.R;

import butterknife.ButterKnife;

/**
 * 门店选择排名页面
 * Created by kristain on 15/12/20.
 */
public class ShopRankFragment extends Fragment {


    public static ShopRankFragment newInstance(String content) {
        ShopRankFragment fragment = new ShopRankFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_rank, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
