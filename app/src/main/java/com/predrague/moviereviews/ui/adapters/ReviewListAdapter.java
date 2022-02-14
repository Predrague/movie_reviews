package com.predrague.moviereviews.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.predrague.moviereviews.data.model.Review;
import com.predrague.moviereviews.databinding.ReviewListItemBinding;
import com.predrague.moviereviews.databinding.ReviewListItemCriticsPickBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Initializing localDataSet to empty ArrayList
    private List<Review> localDataSet = new ArrayList<>();
    // Item click listener
    private final OnReviewItemClickListener reviewClickListener;

    public ReviewListAdapter(OnReviewItemClickListener reviewClickListener) {
        this.reviewClickListener = reviewClickListener;
    }

    public void updateLocalDataSet(List<Review> updatedList) {
        this.localDataSet = updatedList;
        // TODO find a better solution
        notifyDataSetChanged();
    }

    // Regular reviews
    public static class RegularPickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ReviewListItemBinding binding;
        OnReviewItemClickListener reviewClickListener;

        public RegularPickViewHolder(@NonNull ReviewListItemBinding binding, OnReviewItemClickListener reviewClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.reviewClickListener = reviewClickListener;

            // When a list item is clicked ViewHolder's onClick method is called
            this.binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            reviewClickListener.onReviewItemClick(getAdapterPosition());
        }
    }

    // Critic pick reviews
    public static class CriticsPickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ReviewListItemCriticsPickBinding binding;
        OnReviewItemClickListener reviewClickListener;

        public CriticsPickViewHolder(@NonNull ReviewListItemCriticsPickBinding binding, OnReviewItemClickListener reviewClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.reviewClickListener = reviewClickListener;

            // When a list item is clicked ViewHolder's onClick method is called
            this.binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            reviewClickListener.onReviewItemClick(getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return localDataSet.get(position).getCriticsPick();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1)
            return new CriticsPickViewHolder(ReviewListItemCriticsPickBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), reviewClickListener);
        else
            return new RegularPickViewHolder(ReviewListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), reviewClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Review reviewItem = localDataSet.get(position);

        if (holder.getItemViewType() == 1) {
            CriticsPickViewHolder viewHolder = (CriticsPickViewHolder) holder;
            viewHolder.binding.txtReviewTitle.setText(reviewItem.getDisplayTitle());
            viewHolder.binding.txtReviewSummary.setText(reviewItem.getSummaryShort());
            viewHolder.binding.txtAuthor.setText(reviewItem.getAuthor());
            viewHolder.binding.txtRating.setText(reviewItem.getMpaaRating());
            String rawDate = reviewItem.getPublicationDateString();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
            String date;

            try {
                date = outputFormat.format(inputFormat.parse(rawDate));
            } catch (Exception e) {
                e.printStackTrace();
                date = "-";
            }
            viewHolder.binding.txtDate.setText(date);
        } else {
            RegularPickViewHolder viewHolder = (RegularPickViewHolder) holder;
            viewHolder.binding.txtReviewTitle.setText(reviewItem.getDisplayTitle());
            viewHolder.binding.txtReviewSummary.setText(reviewItem.getSummaryShort());
            viewHolder.binding.txtAuthor.setText(reviewItem.getAuthor());
            viewHolder.binding.txtRating.setText(reviewItem.getMpaaRating());
            String rawDate = reviewItem.getPublicationDateString();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
            String date;

            try {
                date = outputFormat.format(inputFormat.parse(rawDate));
            } catch (Exception e) {
                e.printStackTrace();
                date = "-";
            }
            viewHolder.binding.txtDate.setText(date);
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    // On click listener interface
    // Needs to be implemented in View which uses this adapter
    public interface OnReviewItemClickListener {
        void onReviewItemClick(int position);
    }
}
