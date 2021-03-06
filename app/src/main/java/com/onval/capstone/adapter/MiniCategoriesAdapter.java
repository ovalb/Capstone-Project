package com.onval.capstone.adapter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onval.capstone.R;
import com.onval.capstone.room.Category;
import com.onval.capstone.utility.GuiUtility;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MiniCategoriesAdapter extends RecyclerView.Adapter<MiniCategoriesAdapter.MiniViewHolder> {
    public static final int NO_CATEGORIES = -1;
    private Context context;
    private List<Category> categories;
    private int selected, lastSelected;

    public MiniCategoriesAdapter(Context context) {
        this.context = context;
        categories = Collections.emptyList();
        selected = 0;
    }

    @NonNull
    @Override
    public MiniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_category_mini, parent, false);
        return new MiniViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiniViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(v -> {
            if (selected != position) {
                lastSelected = selected;
                selected = position;

                notifyItemChanged(lastSelected);
                notifyItemChanged(selected);
            }
        });
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return (categories == null) ? 0 : categories.size();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public int getSelectedCategoryId() {
        if (categories.size() != 0)
            return categories.get(selected).getId();
        else
            return NO_CATEGORIES;
    }

    class MiniViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mini_colorLabel) View colorLabel;
        @BindView(R.id.mini_category_name) TextView categoryName;

        MiniViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int position) {
            Category category = categories.get(position);
            colorLabel.setBackgroundColor(Color.parseColor(category.getColor()));
            categoryName.setText(category.getName());

            int bgColor = GuiUtility.isLightTheme(context) ? Color.WHITE : context.getResources().getColor(R.color.darkPrimary);
            int selectedBgColor = GuiUtility.isLightTheme(context) ?
                    context.getResources().getColor(R.color.lightSelectionGray)
                    : context.getResources().getColor(R.color.darkPrimaryDark);

            if (selected == position) {
//                categoryName.setTextColor(Color.BLACK);
                itemView.setBackgroundColor(selectedBgColor);
            } else {
//                categoryName.setTextColor(Color.BLACK);
                itemView.setBackgroundColor(bgColor);
            }
        }
    }
}
