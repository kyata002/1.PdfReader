package com.kyata.pdfreader.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kyata.pdfreader.Const;
import com.kyata.pdfreader.R;
import com.kyata.pdfreader.base.BaseAdapter;
import com.kyata.pdfreader.base.BaseViewHolder;
import com.kyata.pdfreader.data.model.Directory;

import java.util.List;

public class DirectoryAdapter extends BaseAdapter<Directory> {

    public DirectoryAdapter(List<Directory> mList, Context context) {
        super(mList, context);
    }

    @Override
    protected RecyclerView.ViewHolder viewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_directory, parent, false);
        return new DirectoryViewModel(view);
    }

    @Override
    protected void onBindView(RecyclerView.ViewHolder viewHolder, int position) {
        DirectoryViewModel directoryViewModel = (DirectoryViewModel) viewHolder;
        directoryViewModel.loadData(mList.get(position));
    }

    private class DirectoryViewModel extends BaseViewHolder implements View.OnClickListener {
        private final TextView tvName;

        public DirectoryViewModel(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_file_name);
            itemView.setOnClickListener(this);
        }

        public void loadData(Directory fileModel) {
            tvName.setText(fileModel.getName());
            itemView.setTag(fileModel);
        }

        @Override
        public void onClick(View v) {
            mCallback.callback(Const.KEY_CLICK_DIRECTORY, itemView.getTag());
        }
    }
}
