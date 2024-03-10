package com.kyata.pdfreader.view.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.kyata.pdfreader.Const;
import com.kyata.pdfreader.R;
import com.kyata.pdfreader.base.BaseActivity;
import com.kyata.pdfreader.base.BaseFragment;
import com.kyata.pdfreader.databinding.FragmentAllfileBinding;
import com.kyata.pdfreader.model.ItemFile;
import com.kyata.pdfreader.view.IFilter;
import com.kyata.pdfreader.view.OnActionCallback;
import com.kyata.pdfreader.view.activity.MainActivity;
import com.kyata.pdfreader.view.activity.SplashActivity;
import com.kyata.pdfreader.view.adapter.ListFileAdapter;

import java.util.List;

public class AllFileFragment extends BaseFragment<FragmentAllfileBinding> implements OnActionCallback, IFilter {
    private ListFileAdapter adapter;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainActivity.ACTION_SEARCH_MAIN)) {
                String text = intent.getStringExtra("data");
                adapter.search(text, (key, data) -> {
                    try {
                        MainActivity.categorySearchList.get(MainActivity.ITEM_ALL_FILE).setList((List<ItemFile>) data);
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
                    List<ItemFile> list = MainActivity.categoryList.get(MainActivity.ITEM_ALL_FILE).getList();
                    adapter.updateList(list);
                    if (list.size() > 0) {
                        binding.llEmpty.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            if (intent.getAction().equals(MainActivity.ACTION_SORT_DATE)) {
                sortDate();
                return;
            }
            if (intent.getAction().equals(MainActivity.ACTION_SORT_SIZE)) {
                sortSize();
                return;
            }
            if (intent.getAction().equals(MainActivity.ACTION_ALL_FILE)) {
                noFilter(true);
                return;
            }
            if (intent.getAction().equals(MainActivity.ACTION_SORT_NAME)) {
                boolean type = intent.getBooleanExtra("alpha", true);
                sortName(type);
                return;
            }
            if (intent.getAction().equals(ListFileAdapter.ACTION_UPDATE_FAVOURITE)) {
                try {
                    String path = intent.getStringExtra("data");
                    boolean isFavourite = intent.getBooleanExtra("value", false);
                    ItemFile itemFile = findItem(path, MainActivity.categoryList.get(MainActivity.ITEM_ALL_FILE).getList());
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
        return R.layout.fragment_allfile;
    }

    @Override
    protected void initView() {
        initList();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        try {
            if (MainActivity.categoryList.get(MainActivity.ITEM_ALL_FILE).getList().size() == 0) {
                binding.llEmpty.setVisibility(View.VISIBLE);
            } else {
                binding.llEmpty.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initList() {
        try {
            adapter = new ListFileAdapter(MainActivity.categoryList.get(MainActivity.ITEM_ALL_FILE).getList(), context);
            adapter.setmCallback(this);
            adapter.sortDate();

            binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            binding.recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

        registerReceiver(receiver);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(receiver);
    }

    @Override
    protected void addEvent() {

    }

    @Override
    public void filterCompletedPDF() {
        if (adapter != null) {
            adapter.filterCompletedPDF();
        }
    }

    @Override
    public void filterNewPDF() {
        if (adapter != null) {
            adapter.filterNewPDF();
        }
    }

    @Override
    public void filterReadingPDF() {
        if (adapter != null) {
            adapter.filterReadingPDF();
        }
    }

    @Override
    public void sortDate() {
        if (adapter != null) {
            adapter.sortDate();
        }
    }

    @Override
    public void sortName(boolean alpha) {
        if (adapter != null) {
            adapter.sortName(alpha);
        }
    }

    @Override
    public void noFilter(boolean notifi) {
        try {
            if (adapter != null) {
                adapter.updateList(MainActivity.categoryList.get(MainActivity.ITEM_ALL_FILE).getList(), notifi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void sortSize() {
        if (adapter != null) {
            adapter.sortSize();
        }
    }

    @Override
    public void callback(String key, Object data) {
        if (key.equals(Const.KEY_CLICK_ITEM)) {
            ItemFile itemFile = (ItemFile) data;
            if (context == null) {
                return;
            }
            ((BaseActivity) context).openPdfFile(itemFile.getPath());
        }
    }
}
