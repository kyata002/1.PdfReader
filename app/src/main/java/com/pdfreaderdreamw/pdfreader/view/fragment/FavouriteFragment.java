package com.pdfreaderdreamw.pdfreader.view.fragment;

import static com.pdfreaderdreamw.pdfreader.view.activity.MainActivity.ITEM_FAVOURITE;
import static com.pdfreaderdreamw.pdfreader.view.activity.MainActivity.categoryList;
import static com.pdfreaderdreamw.pdfreader.view.adapter.ListFileAdapter.ACTION_UPDATE_FAVOURITE;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.pdfreaderdreamw.pdfreader.App;
import com.pdfreaderdreamw.pdfreader.Const;
import com.pdfreaderdreamw.pdfreader.R;
import com.pdfreaderdreamw.pdfreader.base.BaseActivity;
import com.pdfreaderdreamw.pdfreader.base.BaseFragment;
import com.pdfreaderdreamw.pdfreader.databinding.FragmentFavouriteBinding;
import com.pdfreaderdreamw.pdfreader.model.FavoriteFile;
import com.pdfreaderdreamw.pdfreader.model.ItemFile;
import com.pdfreaderdreamw.pdfreader.task.LoadPdfFile;
import com.pdfreaderdreamw.pdfreader.view.OnActionCallback;
import com.pdfreaderdreamw.pdfreader.view.activity.MainActivity;
import com.pdfreaderdreamw.pdfreader.view.activity.PdfReaderActivity;
import com.pdfreaderdreamw.pdfreader.view.activity.SplashActivity;
import com.pdfreaderdreamw.pdfreader.view.adapter.ListFileAdapter;

import java.io.File;
import java.util.List;

public class FavouriteFragment extends BaseFragment<FragmentFavouriteBinding> implements OnActionCallback {
    private ListFileAdapter adapter;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainActivity.ACTION_SEARCH_MAIN)) {
                String text = intent.getStringExtra("data");
//                adapter.updateList(MainActivity.categoryList.get(ITEM_FAVOURITE).getList());
                adapter.search(text, (key, data) -> {
                    try {
                        MainActivity.categorySearchList.get(ITEM_FAVOURITE).setList((List<ItemFile>) data);
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
                    adapter.updateList(MainActivity.categoryList.get(ITEM_FAVOURITE).getList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            if (intent.getAction().equals(ACTION_UPDATE_FAVOURITE)) {
                fetchData();
            }
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_favourite;
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


    @SuppressLint("StaticFieldLeak")
    private void fetchData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    categoryList.get(ITEM_FAVOURITE).clearData();
                    List<FavoriteFile> favoriteFiles = App.getInstance().getDatabase().favoritetDao().getList();
                    int size = favoriteFiles.size();
                    for (int i = 0; i < size; i++) {
                        File file = new File(favoriteFiles.get(i).getPath());
                        if (!file.exists()) {
                            App.getInstance().getDatabase().favoritetDao().delete(file.getPath());
                            continue;
                        }
                        int totalPage = LoadPdfFile.getNumberPage(context, file) - 1;
                        categoryList.get(ITEM_FAVOURITE).addFile(file, totalPage);
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
                    if (MainActivity.categoryList.get(ITEM_FAVOURITE).getList().size() == 0) {
                        binding.llEmpty.setVisibility(View.VISIBLE);
                    } else {
                        binding.llEmpty.setVisibility(View.GONE);
                    }
                    adapter.updateList(MainActivity.categoryList.get(ITEM_FAVOURITE).getList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }


    @Override
    protected void initView() {
        try {
            adapter = new ListFileAdapter(MainActivity.categoryList.get(MainActivity.ITEM_FAVOURITE).getList(), getContext());
            adapter.setmCallback(this);
            adapter.setFavouriteTab(true);

            binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            binding.recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        registerReceiver(receiver);
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
            ItemFile itemFile = (ItemFile) data;
            App.getInstance().getDatabase().favoritetDao().delete(itemFile.getPath());
            adapter.notifyItemRemoved(MainActivity.categoryList.get(MainActivity.ITEM_FAVOURITE).getList().indexOf(itemFile));
            MainActivity.categoryList.get(MainActivity.ITEM_FAVOURITE).getList().remove(itemFile);
            Intent intent = new Intent(ACTION_UPDATE_FAVOURITE);
            intent.putExtra("data", itemFile.getPath());
            intent.putExtra("value", false);
            context.sendBroadcast(intent);
        }
    }
}
