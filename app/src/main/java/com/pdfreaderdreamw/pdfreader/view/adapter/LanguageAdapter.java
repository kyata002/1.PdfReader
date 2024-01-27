package com.pdfreaderdreamw.pdfreader.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pdfreaderdreamw.pdfreader.Const;
import com.pdfreaderdreamw.pdfreader.base.BaseAdapter;
import com.pdfreaderdreamw.pdfreader.databinding.ItemLanguageBinding;
import com.pdfreaderdreamw.pdfreader.model.Language;

import java.util.List;

public class LanguageAdapter extends BaseAdapter<Language> {

    public LanguageAdapter(List<Language> mList, Context context) {
        super(mList, context);
    }

    @Override
    protected RecyclerView.ViewHolder viewHolder(ViewGroup parent, int viewType) {
        return new LanguageViewHolder(ItemLanguageBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    protected void onBindView(RecyclerView.ViewHolder viewHolder, int position) {
        LanguageViewHolder holder= (LanguageViewHolder) viewHolder;
        holder.loadData(mList.get(position));

    }

    private class LanguageViewHolder extends RecyclerView.ViewHolder {
        private final ItemLanguageBinding binding;

        public LanguageViewHolder(ItemLanguageBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            itemView.setOnClickListener(v->{
                if (mCallback!=null){
                    mCallback.callback(Const.KEY_CLICK_ITEM,itemView.getTag());
                }
            });
        }

        public void loadData(Language language) {
            itemView.setTag(language);
            Glide.with(context).load("android_asset/flag/"+language.getCode()+".webp").into(binding.ivIcon);
            binding.tvName.setText(language.getName());
        }
    }
}
