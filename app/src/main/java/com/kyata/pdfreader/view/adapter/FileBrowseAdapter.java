package com.kyata.pdfreader.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kyata.pdfreader.Const;
import com.kyata.pdfreader.R;
import com.kyata.pdfreader.base.BaseAdapter;
import com.kyata.pdfreader.base.BaseViewHolder;
import com.kyata.pdfreader.data.model.FileModel;

import java.util.List;

public class FileBrowseAdapter extends BaseAdapter<FileModel> {

    public FileBrowseAdapter(List<FileModel> mList, Context context) {
        super(mList, context);
    }


    @Override
    protected RecyclerView.ViewHolder viewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file_browse, parent, false);
        return new FileViewModel(view);
    }

    @Override
    protected void onBindView(RecyclerView.ViewHolder viewHolder, int position) {
        FileViewModel fileViewModel = (FileViewModel) viewHolder;
        fileViewModel.loadData(mList.get(position));
    }

    private class FileViewModel extends BaseViewHolder implements View.OnClickListener {
        private final TextView tvName;
        private final TextView tvDate;
        private final ImageView iconFile;

        public FileViewModel(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_file_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            iconFile = itemView.findViewById(R.id.iv_icon_file);
            itemView.setOnClickListener(this);
        }

        public void loadData(FileModel fileModel) {
            tvName.setText(fileModel.getFileName());
            tvDate.setText(fileModel.getFileDateShow());
            iconFile.setImageResource(fileModel.getFileIcon());
            itemView.setTag(fileModel);
        }

        @Override
        public void onClick(View v) {
            mCallback.callback(Const.KEY_CLICK_FILE, itemView.getTag());
        }
    }
}
