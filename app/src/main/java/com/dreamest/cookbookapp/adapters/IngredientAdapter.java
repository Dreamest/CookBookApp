package com.dreamest.cookbookapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.Ingredient;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder> {
    private ArrayList<Ingredient> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int mode;
    public static final int REMOVE_BUTTON = 1;
    public static final int CHECKBOX = 2;

    // data is passed into the constructor
    public IngredientAdapter(Context context, ArrayList<Ingredient> data, int mode) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mode = mode;
    }

    // inflates the row layout from xml when needed
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.ingredient_list_item, parent, false);
        return new MyViewHolder(view, mode);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Ingredient ingredient = mData.get(position);
        if (ingredient.getAmount() != 0) {
            holder.ingredient_TXT_amount.setText(ingredient.getAmount() + "");
            holder.ingredient_TXT_amount.setVisibility(View.VISIBLE);
        } else
            holder.ingredient_TXT_amount.setVisibility(View.GONE);

        holder.ingredient_TXT_item.setText(ingredient.getItem());

        if (!ingredient.getUnits().trim().equals("")) {
            holder.ingredient_TXT_units.setText(ingredient.getUnits());
            holder.ingredient_TXT_units.setVisibility(View.VISIBLE);
        } else
            holder.ingredient_TXT_units.setVisibility(View.GONE);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    Ingredient getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onRemoveClick(int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView ingredient_TXT_item;
        private TextView ingredient_TXT_units;
        private TextView ingredient_TXT_amount;
        private ImageView ingredient_IMG_check;
        private ImageButton ingredient_BTN_remove;
        private int mode;


        MyViewHolder(View itemView, int mode) {
            super(itemView);
            this.mode = mode;
            findViews(itemView);
            if (mode == IngredientAdapter.CHECKBOX) {
                setImage(ingredient_IMG_check, R.drawable.checkbox_inactive);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeBoxStatus();
                    }
                });
            }
            if (mode == IngredientAdapter.REMOVE_BUTTON) {
                ingredient_BTN_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mClickListener != null) {
                            mClickListener.onRemoveClick(getAdapterPosition());
                        }
                    }
                });
            }
        }

        private void setImage(ImageView img, int drawable) {
            img.setImageResource(drawable);
            img.setTag(drawable);
        }

        private void findViews(View itemView) {
            ingredient_TXT_item = itemView.findViewById(R.id.ingredient_TXT_item);
            ingredient_TXT_units = itemView.findViewById(R.id.ingredient_TXT_units);
            ingredient_TXT_amount = itemView.findViewById(R.id.ingredient_TXT_amount);
            ingredient_IMG_check = itemView.findViewById(R.id.ingredient_IMG_check);
            ingredient_BTN_remove = itemView.findViewById(R.id.ingredient_BTN_remove);
            if (mode == IngredientAdapter.CHECKBOX) {
                ingredient_BTN_remove.setVisibility(View.GONE);
            } else if (mode == IngredientAdapter.REMOVE_BUTTON) {
                ingredient_IMG_check.setVisibility(View.GONE);
            }
        }

        private void changeBoxStatus() {
            if (ingredient_IMG_check.getTag().equals(R.drawable.checkbox_active)) {
                setImage(ingredient_IMG_check, R.drawable.checkbox_inactive);
            } else if (ingredient_IMG_check.getTag().equals(R.drawable.checkbox_inactive)) {
                setImage(ingredient_IMG_check, R.drawable.checkbox_active);
            }
        }
    }
}


