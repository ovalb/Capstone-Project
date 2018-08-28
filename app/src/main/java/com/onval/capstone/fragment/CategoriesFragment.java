package com.onval.capstone.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onval.capstone.adapter.CategoriesAdapter;
import com.onval.capstone.viewmodel.CategoriesViewModel;
import com.onval.capstone.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesFragment extends Fragment {
    Context context;
    private CategoriesViewModel viewModel;

    @BindView(R.id.categories) public RecyclerView categories;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        context = getContext();

        ButterKnife.bind(this, view);

        CategoriesAdapter adapter = new CategoriesAdapter(context, viewModel.getData().getValue());
        categories.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        categories.setLayoutManager(layoutManager);

        return view;
    }
}