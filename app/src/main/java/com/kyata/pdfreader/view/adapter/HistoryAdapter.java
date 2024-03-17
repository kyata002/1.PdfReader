package com.kyata.pdfreader.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.kyata.pdfreader.App;
import com.kyata.pdfreader.base.BaseAdapter;
import com.kyata.pdfreader.base.BaseViewHolder;
import com.kyata.pdfreader.databinding.ItemHistoryBinding;
import com.kyata.pdfreader.data.model.SearchHistory;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends BaseAdapter<SearchHistory> {
    public HistoryAdapter(List<SearchHistory> mList, Context context) {
        super(mList, context);
    }

    @Override
    protected RecyclerView.ViewHolder viewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    protected void onBindView(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.loadData(mList.get(position));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<SearchHistory> list, boolean notify) {
        mList = list;
        if (notify) {
            notifyDataSetChanged();
        }
    }

    public void filter(String str) {
        List<SearchHistory> list = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getText().toLowerCase().contains(str.toLowerCase())) {
                list.add(mList.get(i));
            }
        }
        updateList(list, true);
    }

    private class ViewHolder extends BaseViewHolder {
        private final ItemHistoryBinding binding;

        public ViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(v -> mCallback.callback("", itemView.getTag()));
            binding.ivDelete.setOnClickListener(v -> {
                SearchHistory item = (SearchHistory) itemView.getTag();
                App.getInstance().getDatabase().historyDao().delete(item.getId());
                notifyItemRemoved(mList.indexOf(item));
                mList.remove(item);
            });
        }

        public void loadData(SearchHistory searchHistory) {
            itemView.setTag(searchHistory);
            binding.tvHistory.setText(searchHistory.getText());
        }
    }
}
