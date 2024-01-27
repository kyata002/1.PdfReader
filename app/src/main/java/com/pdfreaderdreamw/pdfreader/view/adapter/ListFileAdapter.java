package com.pdfreaderdreamw.pdfreader.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.pdfreaderdreamw.pdfreader.App;
import com.pdfreaderdreamw.pdfreader.Const;
import com.pdfreaderdreamw.pdfreader.R;
import com.pdfreaderdreamw.pdfreader.StorageCommon;
import com.pdfreaderdreamw.pdfreader.base.BaseActivity;
import com.pdfreaderdreamw.pdfreader.base.BaseAdapter;
import com.pdfreaderdreamw.pdfreader.base.BaseViewHolder;
import com.pdfreaderdreamw.pdfreader.databinding.ItemFileBinding;
import com.pdfreaderdreamw.pdfreader.model.FavoriteFile;
import com.pdfreaderdreamw.pdfreader.model.ItemFile;
import com.pdfreaderdreamw.pdfreader.model.RecentFile;
import com.pdfreaderdreamw.pdfreader.utils.CommonUtils;
import com.pdfreaderdreamw.pdfreader.utils.FileUtils;
import com.pdfreaderdreamw.pdfreader.utils.SharePreferenceUtils;
import com.pdfreaderdreamw.pdfreader.view.OnActionCallback;
import com.pdfreaderdreamw.pdfreader.view.dialog.DeleteDialog;
import com.pdfreaderdreamw.pdfreader.view.dialog.InfoDialog;
import com.pdfreaderdreamw.pdfreader.view.dialog.RenameDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListFileAdapter extends BaseAdapter<ItemFile> {

    public static final String ACTION_UPDATE_FAVOURITE = "action_update_favourite";

    public ListFileAdapter(List<ItemFile> mList, Context context) {
        super(mList, context);
    }

    private boolean isRecentTab;
    private boolean isFavouriteTab;

    public void setFavouriteTab(boolean favouriteTab) {
        isFavouriteTab = favouriteTab;
    }

    public void setRecentTab(boolean recentTab) {
        isRecentTab = recentTab;
    }


    @Override
    protected RecyclerView.ViewHolder viewHolder(ViewGroup parent, int viewType) {
        try {
            ItemFileBinding binding = ItemFileBinding.inflate(LayoutInflater.from(context), parent, false);
            return new ItemListHolder(binding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onBindView(RecyclerView.ViewHolder viewHolder, int position) {
        try {
            if (viewHolder == null) {
                return;
            }
            ItemFile itemFile = mList.get(position);
            ItemListHolder holder = (ItemListHolder) viewHolder;
            holder.loadData(itemFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateList(List<ItemFile> list) {
        updateList(list, true);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<ItemFile> list, boolean notify) {
        mList = list;
        if (notify) {
            notifyDataSetChanged();
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    public void sortSize() {
        for (int i = 0; i < mList.size() - 1; i++) {
            for (int j = i; j < mList.size(); j++) {
                if (new File(mList.get(i).getPath()).length() < new File(mList.get(j).getPath()).length()) {
                    Collections.swap(mList, i, j);
                }
            }
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void sortDate() {
        for (int i = 0; i < mList.size() - 1; i++) {
            for (int j = i; j < mList.size(); j++) {
                if (new File(mList.get(i).getPath()).lastModified() < new File(mList.get(j).getPath()).lastModified()) {
                    Collections.swap(mList, i, j);
                }
            }
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void sortName(boolean alpha) {
        for (int i = 0; i < mList.size() - 1; i++) {
            for (int j = i; j < mList.size(); j++) {
                if (alpha) {
                    if (new File(mList.get(i).getPath()).getName().toLowerCase()
                            .compareTo(new File(mList.get(j).getPath()).getName().toLowerCase()) > 0) {
                        Collections.swap(mList, i, j);
                    }
                } else {
                    if (new File(mList.get(i).getPath()).getName().toLowerCase()
                            .compareTo(new File(mList.get(j).getPath()).getName().toLowerCase()) < 0) {
                        Collections.swap(mList, i, j);
                    }
                }

            }
        }
        notifyDataSetChanged();
    }

    public void setFilterType(int filterType) {
        if (filterType == StorageCommon.FILTER_ALL) {
            filter("");
        } else if (filterType == StorageCommon.FILTER_READING) {
            filterReadingPDF();
        } else if (filterType == StorageCommon.FILTER_COMPLETED) {
            filterCompletedPDF();
        } else {
            filterNewPDF();
        }
    }

    public void filterReadingPDF() {
        List<ItemFile> list = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            File file = new File(mList.get(i).getPath());
            int currentPageReading = SharePreferenceUtils.getCurrentPageReading(context, file.getPath());
            int totalPage = mList.get(i).getNumPage();
            if (currentPageReading >= 1 && currentPageReading < totalPage) {
                list.add(mList.get(i));
            }
        }
        updateList(list);
    }

    public void filterCompletedPDF() {
        List<ItemFile> list = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            File file = new File(mList.get(i).getPath());
            int currentPageReading = SharePreferenceUtils.getCurrentPageReading(context, file.getPath());
            int totalPage = mList.get(i).getNumPage();
            if (currentPageReading == totalPage) {
                list.add(mList.get(i));
            }
        }
        updateList(list);
    }

    public void filterNewPDF() {
        List<ItemFile> list = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            File file = new File(mList.get(i).getPath());
            int currentPageReading = SharePreferenceUtils.getCurrentPageReading(context, file.getPath()) - 1;
            if (currentPageReading == -1) {
                list.add(mList.get(i));
            }
        }
        updateList(list);
    }

    public void filter(String str) {
        List<ItemFile> list = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            File file = new File(mList.get(i).getPath());
            if (file.getName().toLowerCase().contains(str.toLowerCase())) {
                list.add(mList.get(i));
            }
        }
        updateList(list);
    }

    public void search(String text, OnActionCallback callback) {
        List<ItemFile> tempList = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getPath().toLowerCase().contains(text.toLowerCase())) {
                tempList.add(mList.get(i));
            }
        }
        callback.callback(null, tempList);
//        updateList(tempList);
    }


    private class ItemListHolder extends BaseViewHolder implements View.OnClickListener {
        private final ItemFileBinding binding;

        public ItemListHolder(@NonNull ItemFileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
            binding.ivMore.setOnClickListener(this);
            binding.ivFavourite.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void loadData(ItemFile itemFile) {
            File file = new File(itemFile.getPath());
            binding.tvTitle.setText(file.getName().toLowerCase().replace(".pdf", ""));
            binding.tvPath.setText(FileUtils.formatFileSize(file.length()) + " | " +
                    CommonUtils.getInstance().formatDate(file.lastModified()));
            binding.ivFavourite.setImageResource(itemFile.isFavorite() ? R.drawable.ic_favourite_reading_checked : R.drawable.ic_favourite_reading);
            int currentPageReading = SharePreferenceUtils.getCurrentPageReading(context, file.getPath());

            int totalPage = itemFile.getNumPage();

            String[] rs = computeProgress(currentPageReading, totalPage);
            int progress = 0;
            try {
                progress = Integer.parseInt(rs[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            binding.progress.setProgress(progress);
            binding.tvStatus.setText(rs[1]);

            if (progress < 100) {
                binding.tvStatus.setTextColor(Color.parseColor("#0088EA"));
                binding.progress.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#0088EA")));
            } else {
                binding.tvStatus.setTextColor(Color.parseColor("#00CF78"));
                binding.progress.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#00CF78")));
            }
            itemView.setTag(itemFile);
        }


        @Override
        public void onClick(View view) {
            if (view == binding.ivMore) {
                showPopupMenu(view, (ItemFile) itemView.getTag());
                return;
            }
            if (view == binding.ivFavourite) {
                executeFavourite((ItemFile) itemView.getTag());
                return;
            }
            mCallback.callback(Const.KEY_CLICK_ITEM, itemView.getTag());
        }
    }

    private String[] computeProgress(int currentPageReading, int totalPage) {
        String rs;
        int progress = (int) ((currentPageReading * 1.0 / totalPage) * 100);

        if (progress >= 100) {
            rs = context.getString(R.string.complete);
            progress = 100;
        } else if (currentPageReading == 0) {
            rs = context.getString(R.string.news);
        } else {
            rs = progress + "%";
        }
        return new String[]{progress + "", rs};

    }


    @SuppressLint({"RestrictedApi", "NonConstantResourceId"})
    private void showPopupMenu(View it, ItemFile itemFile) {
        PopupMenu menu = new PopupMenu(context, it);
        menu.inflate(R.menu.popup_file);
        MenuItem btRename = menu.getMenu().findItem(R.id.bt_rename);
        MenuItem btDelete = menu.getMenu().findItem(R.id.bt_delete);
        if (isRecentTab || isFavouriteTab) {
            btRename.setVisible(false);
            btDelete.setVisible(false);
        }
//        if (isFavouriteTab) {
//            btDelete.setVisible(false);
//        }

        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.bt_detail:
                    executeShowInfo(itemFile);
                    break;
                case R.id.bt_rename:
                    executeRename(itemFile);
                    break;
                case R.id.bt_delete:
                    executeDelete(itemFile);
                    break;
                case R.id.bt_share:
                    CommonUtils.getInstance().shareFile(context, new File(itemFile.getPath()));
                    setUserProperty("CLICK_SHARE_AT_HOME");
                    break;
            }
            return true;
        });

        MenuBuilder menuBuilder = (MenuBuilder) menu.getMenu();
        MenuPopupHelper helper = new MenuPopupHelper(context, menuBuilder, it);
        helper.setForceShowIcon(true);
        helper.show();
    }

    private void executeFavourite(ItemFile itemFile) {
        setUserProperty("CLICK_FAVOURITE");
        if (!itemFile.isFavorite()) {
            App.getInstance().getDatabase().favoritetDao().add(new FavoriteFile(itemFile.getPath()));
//            Toast.makeText(context, "Removed file from favourite", Toast.LENGTH_SHORT).show();
        } else {
            App.getInstance().getDatabase().favoritetDao().delete(itemFile.getPath());
            Toast.makeText(context, "Removed file from favourite", Toast.LENGTH_SHORT).show();
        }
        if (isFavouriteTab) {
            mList.remove(itemFile);
            notifyItemRemoved(mList.indexOf(itemFile));
            Intent intent = new Intent(ACTION_UPDATE_FAVOURITE);
            intent.putExtra("data", itemFile.getPath());
            intent.putExtra("value", false);
            context.sendBroadcast(intent);
        } else {
            itemFile.setFavorite(!itemFile.isFavorite());
            notifyItemChanged(mList.indexOf(itemFile));
            Intent intent = new Intent(ACTION_UPDATE_FAVOURITE);
            intent.putExtra("data", itemFile.getPath());
            intent.putExtra("value", itemFile.isFavorite());
            context.sendBroadcast(intent);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void executeRename(ItemFile itemFile) {
        setUserProperty("CLICK_RENAME");
        RenameDialog.start(context, itemFile, (key, data) -> {
            File file = new File(itemFile.getPath());
            String path = (String) data;
            File newFile = new File(path);
            if (file.renameTo(newFile)) {
                renameRecentFile(itemFile.getPath(), newFile.getPath());
                renameFavouriteFile(itemFile.getPath(), newFile.getPath());

                itemFile.setPath(newFile.getAbsolutePath());
                notifyDataSetChanged();
                Toast.makeText(context, context.getString(R.string.rename_sc), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(newFile));
                context.sendBroadcast(intent);
            }
        });
    }

    private void renameFavouriteFile(String oldFile, String newFile) {
        FavoriteFile favoriteFile = App.getInstance().getDatabase().favoritetDao().getObject(oldFile);
        if (favoriteFile != null) {
            favoriteFile.setPath(newFile);
            App.getInstance().getDatabase().favoritetDao().update(favoriteFile);
        }
    }


    private void renameRecentFile(String oldFile, String newFile) {
        RecentFile recentFile = App.getInstance().getDatabase().recentDao().getObject(oldFile);
        if (recentFile != null) {
            recentFile.setPath(newFile);
            App.getInstance().getDatabase().recentDao().update(recentFile);
        }
    }

    private void executeDelete(ItemFile itemFile) {
        setUserProperty("CLICK_DELETE_FILE");
        if (isFavouriteTab || isRecentTab) {
            mCallback.callback(Const.KEY_DELETE, itemFile);
            return;
        }

        DeleteDialog.start(context, itemFile.getPath(),(key, data) -> {
            File file = new File(itemFile.getPath());
            if (file.delete()) {
                Toast.makeText(context, context.getString(R.string.deleted_file), Toast.LENGTH_SHORT).show();
                notifyItemRemoved(mList.indexOf(itemFile));
                mList.remove(itemFile);
            }
        });
    }

    private void executeShowInfo(ItemFile itemFile) {
        setUserProperty("CLICK_DETAIL");
        InfoDialog.start(context, itemFile);

    }

    private void setUserProperty(String name) {
        if (context == null) {
            return;
        }
        ((BaseActivity) context).setUserProperty(name);
    }

}
