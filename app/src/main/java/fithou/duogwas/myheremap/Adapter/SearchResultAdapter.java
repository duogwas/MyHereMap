package fithou.duogwas.myheremap.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fithou.duogwas.myheremap.Model.SearchResult;
import fithou.duogwas.myheremap.R;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    List<SearchResult> searchResults;
    Context context;
    String keyWord;

    public SearchResultAdapter(List<SearchResult> results, Context context, String keyWord) {
        this.searchResults = results;
        this.context = context;
        this.keyWord = keyWord;
    }

    private SpannableString highlightSearchKeyWord(String title, String searchKeyWord) {
        SpannableString spannableString = new SpannableString(title);
        Pattern pattern = Pattern.compile(Pattern.quote(searchKeyWord), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(title);
        while (matcher.find()) {
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResult searchResult = searchResults.get(position);
        if (searchResult == null) {
            return;
        }

        SpannableString highlightedTitle = highlightSearchKeyWord(searchResult.getTitle(), keyWord);
        holder.tvTitle.setText(highlightedTitle);

        holder.itemView.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + searchResult.getTitle());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            } else {
                Uri webpageUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + searchResult.getTitle());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpageUri);
                context.startActivity(webIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivLocation, ivDirection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivLocation = itemView.findViewById(R.id.ivLocation);
            ivDirection = itemView.findViewById(R.id.ivDirection);
        }
    }
}

