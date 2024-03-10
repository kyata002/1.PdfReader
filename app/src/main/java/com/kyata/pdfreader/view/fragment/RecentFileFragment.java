package com.kyata.pdfreader.view.fragment;

import static com.kyata.pdfreader.view.activity.MainActivity.ITEM_RECENT;
import static com.kyata.pdfreader.view.activity.MainActivity.categoryList;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.kyata.pdfreader.App;
import com.kyata.pdfreader.Const;
import com.kyata.pdfreader.R;
import com.kyata.pdfreader.base.BaseActivity;
import com.kyata.pdfreader.base.BaseFragment;
import com.kyata.pdfreader.databinding.FragmentRecentFileBinding;
import com.kyata.pdfreader.model.ItemFile;
import com.kyata.pdfreader.model.RecentFile;
import com.kyata.pdfreader.task.LoadPdfFile;
import com.kyata.pdfreader.view.OnActionCallback;
import com.kyata.pdfreader.view.activity.MainActivity;
import com.kyata.pdfreader.view.activity.SplashActivity;
import com.kyata.pdfreader.view.adapter.ListFileAdapter;

import java.io.File;
import java.util.List;

public class RecentFileFragment extends BaseFragment<FragmentRecentFileBinding> implements OnActionCallback {
    private ListFileAdapter adapter;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainActivity.ACTION_SEARCH_MAIN)) {
                String text = intent.getStringExtra("data");
                adapter.search(text, (key, data) -> {
                    try {
                        MainActivity.categorySearchList.get(ITEM_RECENT).setList((List<ItemFile>) data);
                        context.sendBroadcast(new Intent(MainActivity.SEARCH_UPDATE));
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.categorySearchList = MainActivity.categoryList;
                    }
                });
                return;
            }
            if (intent.getAction().equals(SplashActivity.ACTION_UPDATE_DATA)) {
                try {
                    adapter.updateList(MainActivity.categoryList.get(ITEM_RECENT).getList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            if (intent.getAction().equals(ListFileAdapter.ACTION_UPDATE_FAVOURITE)) {
                try {
                    String path = intent.getStringExtra("data");
                    boolean isFavourite = intent.getBooleanExtra("value", false);
                    ItemFile itemFile = findItem(path, MainActivity.categoryList.get(ITEM_RECENT).getList());
                    if (itemFile == null) {
                        return;
                    }
                    itemFile.setFavorite(isFavourite);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recent_file;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        fetchData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(receiver);
    }

    @Override
    protected void initView() {
        try {
            adapter = new ListFileAdapter(MainActivity.categoryList.get(MainActivity.ITEM_RECENT).getList(), getContext());
            adapter.setmCallback(this);
            adapter.setRecentTab(true);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            binding.recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        registerReceiver(receiver);
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    categoryList.get(ITEM_RECENT).clearData();
                    List<RecentFile> recentFiles = App.getInstance().getDatabase().recentDao().getList();
                    int size = recentFiles.size();
                    for (int i = 0; i < size; i++) {
                        File file = new File(recentFiles.get(i).getPath());
                        if (!file.exists()) {
                            App.getInstance().getDatabase().recentDao().delete(file.getPath());
                            continue;
                        }
                        int totalPage = LoadPdfFile.getNumberPage(context, file) - 1;
                        categoryList.get(ITEM_RECENT).addFile(file, totalPage);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                try {
                    if (MainActivity.categoryList.get(MainActivity.ITEM_RECENT).getList().size() == 0) {
                        binding.llEmpty.setVisibility(View.VISIBLE);
                    } else {
                        binding.llEmpty.setVisibility(View.GONE);
                    }
                    adapter.updateList(MainActivity.categoryList.get(ITEM_RECENT).getList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @Override
    protected void addEvent() {

    }


    @Override
    public void callback(String key, Object data) {
        if (key.equals(Const.KEY_CLICK_ITEM)) {
            ItemFile itemFile = (ItemFile) data;
            if (context == null) {
                return;
            }
            ((BaseActivity) context).openPdfFile(itemFile.getPath());
            return;
        }
        if (key.equals(Const.KEY_DELETE)) {
            try {
                ItemFile itemFile = (ItemFile) data;
                App.getInstance().getDatabase().recentDao().delete(itemFile.getPath());
                adapter.notifyItemRemoved(MainActivity.categoryList.get(MainActivity.ITEM_RECENT).getList().indexOf(itemFile));
                MainActivity.categoryList.get(MainActivity.ITEM_RECENT).getList().remove(itemFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
