package com.predrague.moviereviews.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.predrague.moviereviews.data.model.Review;
import com.predrague.moviereviews.databinding.ReviewSearchItemBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReviewSearchRecyclerViewAdapter extends RecyclerView.Adapter<ReviewSearchRecyclerViewAdapter.ViewHolder> {
    private List<Review> reviews = new ArrayList<Review>();
    private IReviewSearchListClickListener listener;

    public ReviewSearchRecyclerViewAdapter(IReviewSearchListClickListener listener) {
        this.listener = listener;
    }

    public void updateLocalDataSet(List<Review> updatedList) {
        this.reviews = updatedList;
        // TODO find a better solution
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ReviewSearchItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Review reviewItem = reviews.get(position);
        holder.binding.reviewTitle.setText(reviewItem.getDisplayTitle());
        holder.binding.reviewAuthor.setText(reviewItem.getAuthor());

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        String date;

        try {
            date = outputFormat.format(inputFormat.parse(reviewItem.getPublicationDateString()));
        } catch (Exception e) {
            e.printStackTrace();
            date = "-";
        }
        holder.binding.reviewDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ReviewSearchItemBinding binding;
        IReviewSearchListClickListener clickListener;

        public ViewHolder(ReviewSearchItemBinding binding, IReviewSearchListClickListener clickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.clickListener = clickListener;

            this.binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface IReviewSearchListClickListener {
        void onItemClick(int position);
    }
}