package com.predrague.moviereviews.ui.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.predrague.moviereviews.R;
import com.predrague.moviereviews.data.model.Review;
import com.predrague.moviereviews.databinding.ReviewListItemBinding;
import com.predrague.moviereviews.databinding.ReviewListItemCriticsPickBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Initializing localDataSet to empty ArrayList
    private List<Review> localDataSet = new ArrayList<>();
    // Item interaction listener
    private final IReviewInteractionListener reviewClickListener;
    private final Context context;

    static final long MENU_ANIMATION_DURATION = 4000;

    public ReviewListAdapter(Context context, IReviewInteractionListener reviewClickListener) {
        this.reviewClickListener = reviewClickListener;
        this.context = context;
    }

    public void updateLocalDataSet(List<Review> updatedList) {
        this.localDataSet = updatedList;
        // TODO find a better solution
        notifyDataSetChanged();
    }

    // Regular reviews
    public static class RegularPickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final ReviewListItemBinding binding;
        IReviewInteractionListener reviewClickListener;

        public RegularPickViewHolder(@NonNull ReviewListItemBinding binding, IReviewInteractionListener reviewClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.reviewClickListener = reviewClickListener;

            // When a list item is clicked ViewHolder's onClick method is called
            this.binding.getRoot().setOnClickListener(this);

            // Long click calls ViewHolders' showMenu() method to show menu with remove button.
            this.binding.getRoot().setOnLongClickListener(this);
            this.binding.btnRemoveItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reviewClickListener.onReviewItemLongPress(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) {
            reviewClickListener.onReviewItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            showMenu();
            return true;
        }

        private void showMenu() {
            binding.reviewItemMenu.setVisibility(View.VISIBLE);
            binding.reviewItemMenu.setAlpha(1f);

            // Animating menu to disappear after short time.
            binding.reviewItemMenu.animate()
                    .setStartDelay(MENU_ANIMATION_DURATION)
                    .alpha(0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            binding.reviewItemMenu.setVisibility(View.GONE);
                        }
                    });
        }
    }

    // Critic pick reviews
    public static class CriticsPickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final ReviewListItemCriticsPickBinding binding;
        IReviewInteractionListener reviewClickListener;

        public CriticsPickViewHolder(@NonNull ReviewListItemCriticsPickBinding binding, IReviewInteractionListener reviewClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.reviewClickListener = reviewClickListener;

            // When a list item is clicked ViewHolder's onClick method is called
            this.binding.getRoot().setOnClickListener(this);

            // Long click calls ViewHolders' showMenu() method to show menu with remove button.
            this.binding.getRoot().setOnLongClickListener(this);
            this.binding.btnRemoveItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reviewClickListener.onReviewItemLongPress(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) {
            reviewClickListener.onReviewItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            showMenu();
            return true;
        }

        private void showMenu() {
            binding.reviewItemMenu.setVisibility(View.VISIBLE);
            binding.reviewItemMenu.setAlpha(1f);

            // Animating menu to disappear after short time.
            binding.reviewItemMenu.animate()
                    .setStartDelay(MENU_ANIMATION_DURATION)
                    .alpha(0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            binding.reviewItemMenu.setVisibility(View.GONE);
                        }
                    });
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

            // Setting a background image to critics pick items
            try {
                String imageUrl = reviewItem.getMultimedia().getSrc();
                Glide.with(context).load(imageUrl).into(viewHolder.binding.imgCriticsReviewIcon);
                viewHolder.binding.imgCriticsReviewIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            } catch (Exception e) {
                // If an error occurs or there is no multimedia for a review there is default image.
                Glide.with(context).load(AppCompatResources.getDrawable(context, R.drawable.critics_review_background)).into(viewHolder.binding.imgCriticsReviewIcon);
                viewHolder.binding.imgCriticsReviewIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }

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
    public interface IReviewInteractionListener {
        void onReviewItemClick(int position);
        void onReviewItemLongPress(int position);
    }
}
