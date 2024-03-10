package com.kyata.pdfreader.view.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.kyata.pdfreader.Const;
import com.kyata.pdfreader.R;
import com.kyata.pdfreader.base.BaseActivity;
import com.kyata.pdfreader.base.BaseFragment;
import com.kyata.pdfreader.databinding.FragmentBrowseBinding;
import com.kyata.pdfreader.model.Directory;
import com.kyata.pdfreader.model.FileModel;
import com.kyata.pdfreader.utils.CommonUtils;
import com.kyata.pdfreader.utils.FileUtils;
import com.kyata.pdfreader.view.OnActionCallback;
import com.kyata.pdfreader.view.adapter.DirectoryAdapter;
import com.kyata.pdfreader.view.adapter.FileBrowseAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BrowseFragment extends BaseFragment<FragmentBrowseBinding> implements OnActionCallback {
    private File currentDir;
    private File envr;
    private final List<FileModel> mList = new ArrayList<>();
    private final List<Directory> directoryList = new ArrayList<>();
    private FileBrowseAdapter adapter;
    private DirectoryAdapter directoryAdapter;
    private int currentIndex;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_browse;
    }

    @Override
    protected void initView() {
        iniComponent();
    }

    @Override
    protected void addEvent() {
        binding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void iniComponent() {
        binding.rvDirectory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        directoryAdapter = new DirectoryAdapter(directoryList, getContext());
        directoryAdapter.setmCallback(this);
        binding.rvDirectory.setAdapter(directoryAdapter);

        binding.rvFile.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FileBrowseAdapter(mList, getContext());
        adapter.setmCallback(this);
        binding.rvFile.setAdapter(adapter);
        envr = Environment.getExternalStorageDirectory();
        currentDir = envr;
        fetchData();
    }

    public void fetchData() {
        try {
            directoryList.remove(directoryList.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadFile();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadFile() {
        mList.clear();
        File[] dirs = currentDir.listFiles();
        List<FileModel> dir = new ArrayList<>();
        List<FileModel> fls = new ArrayList<>();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    assert dirs != null;
                    for (File ff : dirs) {
                        if (ff.isDirectory() && !ff.isHidden()) {
                            FileModel fileModel = new FileModel();
                            fileModel.setDir(true);
                            fileModel.setFilePath(ff.getPath());
                            fileModel.setFileName(ff.getName());
                            fileModel.setFileSize(ff.length());
                            fileModel.setFileDate(ff.lastModified());
                            fileModel.setFileIcon(FileUtils.getFileIcon(true));
                            String formatDate = CommonUtils.getInstance().formatDate(ff.lastModified());
                            fileModel.setFileDateShow(formatDate);
                            dir.add(fileModel);
                        } else {
                            if (!ff.getPath().toLowerCase().endsWith(".pdf")) {
                                continue;
                            }
                            FileModel fileModel = new FileModel();
                            fileModel.setDir(true);
                            fileModel.setFilePath(ff.getPath());
                            fileModel.setFileName(ff.getName());
                            fileModel.setFileSize(ff.length());
                            fileModel.setFileDate(ff.lastModified());
                            fileModel.setFileIcon(FileUtils.getFileIcon(false));
                            String formatDate = CommonUtils.getInstance().formatDate(ff.lastModified());
                            fileModel.setFileDateShow(formatDate);
                            fls.add(fileModel);
                        }
                    }
                    Collections.sort(dir);
                    Collections.sort(fls);
                    dir.addAll(fls);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mList.addAll(dir);
                directoryList.add(new Directory(currentDir, dir));
                return null;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                if (directoryList.size() > 1) {
                    directoryList.get(directoryList.size() - 2).setIndex(currentIndex);
                }
                directoryAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                binding.rvDirectory.scrollToPosition(directoryList.size());
            }
        }.execute();
    }

    public boolean isParentDir() {
        return currentDir.getName().equalsIgnoreCase(envr.getName());
    }

    @SuppressLint("NotifyDataSetChanged")
    public boolean onBackPress() {
        if (directoryList.size() > 1) {
            directoryList.remove(directoryList.size() - 1);
            mList.clear();
            mList.addAll(directoryList.get(directoryList.size() - 1).getData());
            adapter.notifyDataSetChanged();
            binding.rvFile.scrollToPosition(directoryList.get(directoryList.size() - 1).getIndex() + 5);
            directoryAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void callback(String key, Object data) {
        if (key.equals(Const.KEY_CLICK_FILE)) {
            FileModel fileModel = (FileModel) data;
            if (new File(fileModel.getFilePath()).isFile()) {
                openFile(fileModel);
                return;
            }
            currentIndex = mList.indexOf(fileModel);
            currentDir = new File(fileModel.getFilePath());
            loadFile();
            return;
        }
        if (key.contains(Const.KEY_CLICK_DIRECTORY)) {
            Directory directory = (Directory) data;
            List<Directory> temps = new ArrayList<>(directoryList);
            temps = temps.subList(0, directoryList.indexOf(directory) + 1);
            directoryList.clear();
            directoryList.addAll(temps);
            mList.clear();
            mList.addAll(directoryList.get(directoryList.size() - 1).getData());
            directoryAdapter.notifyDataSetChanged();
            binding.rvFile.scrollToPosition(directoryList.get(directoryList.size() - 1).getIndex() + 5);
            adapter.notifyDataSetChanged();

        }
    }

    private void openFile(FileModel fileModel) {
        if (context == null) {
            return;
        }
        ((BaseActivity) context).openPdfFile(fileModel.getFilePath());
    }


}
