package com.onval.capstone.dialog_fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.onval.capstone.R;
import com.onval.capstone.adapter.MiniCategoriesAdapter;
import com.onval.capstone.viewmodel.CategoriesViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_ID;
import static com.onval.capstone.dialog_fragment.AddCategoryDialogFragment.ADD_CATEGORY_TAG;


public class ChooseCategoryDialogFragment extends DialogFragment {

    private static final String SAVE_RECORDING_TAG = "save-recording-tag";

    @BindView(R.id.mini_cat_rv) RecyclerView categories;
    @BindView(R.id.mini_add_cat) ImageView addCategory;

    private CategoriesViewModel viewModel;
    private MiniCategoriesAdapter adapter;
    private Bundle recInfoBundle;
    private FragmentManager fm;

    private View layout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recInfoBundle = getArguments();

        layout = LayoutInflater.from(getContext()).inflate(R.layout.category_list_mini, null);
        ButterKnife.bind(this, layout);

        assert getActivity() != null;
        fm = getActivity().getSupportFragmentManager();

        adapter = new MiniCategoriesAdapter(getContext());

        categories.setLayoutManager(new LinearLayoutManager(getContext()));
        categories.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        viewModel.getCategories().observe(this, adapter::setCategories);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setView(layout)
                .setPositiveButton("Save", new ChooseCategoryListener())
                .setNeutralButton("Delete", new AskConfirmationListener(fm))
                .setNegativeButton("Cancel", (dialogInterface, i) -> getDialog().cancel());

        return builder.create();
    }

    class ChooseCategoryListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            int categoryId = adapter.getSelectedCategoryId();
            recInfoBundle.putInt(CATEGORY_ID, categoryId);

            SaveRecordingDialogFragment saveRecording = new SaveRecordingDialogFragment();
            saveRecording.setArguments(recInfoBundle);
            saveRecording.show(fm, SAVE_RECORDING_TAG);
        }
    }

    @OnClick(R.id.mini_add_cat)
    public void addCategory() {
        AddCategoryDialogFragment addCatFragment = new AddCategoryDialogFragment();
        addCatFragment.show(fm, ADD_CATEGORY_TAG);
    }
}