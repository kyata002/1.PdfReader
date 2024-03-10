package com.kyata.pdfreader.view.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.ActionMode.Callback;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.kyata.pdfreader.R;
import com.kyata.pdfreader.model.PDFPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShareAsPictureAdapter extends Adapter<ShareAsPictureAdapter.OrganizePagesViewHolder> {
    public ActionMode actionMode;
    public ActionModeCallback actionModeCallback;
    public Context context;
    private final List<PDFPage> listPdfImagePages;
    private final SparseBooleanArray selectedPages = new SparseBooleanArray();


    public static class OrganizePagesViewHolder extends ViewHolder {
        LinearLayout layOrganizePage;
        public TextView tvPageNumber;
        public RelativeLayout rLayMain;
        ImageView imgPdfImage;

        private OrganizePagesViewHolder(View view) {
            super(view);
            this.rLayMain = view.findViewById(R.id.rLayMain);
            this.tvPageNumber = view.findViewById(R.id.tvPageNumber);
            this.imgPdfImage = view.findViewById(R.id.imgPdfImage);
            this.layOrganizePage = view.findViewById(R.id.layOrgnizePage);
        }
    }

    public ShareAsPictureAdapter(Context context, List<PDFPage> list) {
        this.listPdfImagePages = list;
        this.context = context;
        this.actionModeCallback = new ActionModeCallback();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("number of thumbs ");
        stringBuilder.append(list.size());
    }

    @NonNull
    public OrganizePagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new OrganizePagesViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_organize_pages_grid, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull final OrganizePagesViewHolder organizePagesViewHolder, int i) {
        PDFPage pDFPage = this.listPdfImagePages.get(i);
        try {
            Glide.with(context).load(pDFPage.getThumbnailUri()).into(organizePagesViewHolder.imgPdfImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        organizePagesViewHolder.tvPageNumber.setText(String.valueOf(pDFPage.getPageNumber()));
        changePDFSelectedBGColor(organizePagesViewHolder, i);
        organizePagesViewHolder.rLayMain.setOnClickListener(view -> {
            int adapterPosition = organizePagesViewHolder.getAdapterPosition();
            if (ShareAsPictureAdapter.this.actionMode == null) {
                ShareAsPictureAdapter shareAsPictureAdapter = ShareAsPictureAdapter.this;
                shareAsPictureAdapter.actionMode = ((AppCompatActivity) shareAsPictureAdapter.context).startSupportActionMode(ShareAsPictureAdapter.this.actionModeCallback);
            }
            ShareAsPictureAdapter.this.getSelectedImagePdf(adapterPosition);
            StringBuilder sb = new StringBuilder();
            sb.append("Clicked position ");
            sb.append(adapterPosition);
        });
    }

    public void getSelectedImagePdf(int i) {
        if (this.selectedPages.get(i, false)) {
            this.selectedPages.delete(i);
        } else {
            this.selectedPages.put(i, true);
        }
        notifyItemChanged(i);
        int size = this.selectedPages.size();
        if (size == 0) {
            this.actionMode.finish();
            return;
        }
        ActionMode actionMode2 = this.actionMode;
        String stringBuilder = size +
                " " +
                "Selected";
        actionMode2.setTitle(stringBuilder);
        this.actionMode.invalidate();
    }

    private void changePDFSelectedBGColor(OrganizePagesViewHolder organizePagesViewHolder, int i) {
        if (isSelected(i)) {
            organizePagesViewHolder.layOrganizePage.setVisibility(View.VISIBLE);
        } else {
            organizePagesViewHolder.layOrganizePage.setVisibility(View.GONE);
        }
    }

    private boolean isSelected(int i) {
        return getSelectedPdfImagePages().contains(i);
    }

    public int getItemCount() {
        return this.listPdfImagePages.size();
    }

    public void clearSelectedPDF() {
        List<Integer> selectedImgPages = getSelectedPdfImagePages();
        this.selectedPages.clear();
        for (Integer intValue : selectedImgPages) {
            notifyItemChanged(intValue);
        }
    }

    public List<Integer> getSelectedPdfImagePages() {
        int size = this.selectedPages.size();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < size; i++) {
            arrayList.add(this.selectedPages.keyAt(i));
        }
        return arrayList;
    }

    private void deletePdfItem(int i) {
        this.listPdfImagePages.remove(i);
        notifyItemRemoved(i);
    }

    public List<PDFPage> getFinalOrganizedPages() {
        return this.listPdfImagePages;
    }


    public void deletePhotoPdfItem(List<Integer> list) {
        Collections.sort(list, (num, num2) -> num2 - num);
        while (!list.isEmpty()) {
            if (list.size() == 1) {
                deletePdfItem(list.get(0));
                list.remove(0);
            } else {
                int i = 1;
                while (list.size() > i && list.get(i).equals(list.get(i - 1) - 1)) {
                    i++;
                }
                if (i == 1) {
                    deletePdfItem(list.get(0));
                } else {
                    removeSelectedPdfRange(list.get(i - 1), i);
                }
                if (i > 0) {
                    list.subList(0, i).clear();
                }
            }
        }
    }

    private void removeSelectedPdfRange(int i, int i2) {
        for (int i3 = 0; i3 < i2; i3++) {
            this.listPdfImagePages.remove(i);
        }
        notifyItemRangeRemoved(i, i2);
    }

    private class ActionModeCallback implements Callback {
        int colorFrom, colorTo, flags;
        View view;

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        private ActionModeCallback() {
            this.view = ((Activity) ShareAsPictureAdapter.this.context).getWindow().getDecorView();
            this.flags = this.view.getSystemUiVisibility();
            this.colorFrom = ShareAsPictureAdapter.this.context.getResources().getColor(R.color.colorPrimaryDark);
            this.colorTo = ShareAsPictureAdapter.this.context.getResources().getColor(R.color.colorDarkerGray);
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.activity_organize_pages_action_mode, menu);
            int i = VERSION.SDK_INT;
            if (i >= 21) {
                if (i >= 23) {
                    this.flags &= -8193;
                    this.view.setSystemUiVisibility(this.flags);
                }
                ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), this.colorFrom, this.colorTo);
                ofObject.setDuration(300);
                ofObject.addUpdateListener(valueAnimator -> {
                });
                ofObject.start();
            }
            return true;
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.action_delete) {
                ShareAsPictureAdapter shareAsPictureAdapter = ShareAsPictureAdapter.this;
                shareAsPictureAdapter.deletePhotoPdfItem(shareAsPictureAdapter.getSelectedPdfImagePages());
                actionMode.finish();
            }
            return true;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            ShareAsPictureAdapter.this.clearSelectedPDF();
            int i = VERSION.SDK_INT;
            if (i >= 21) {
                if (i >= 23) {
                    this.flags |= 8192;
                    this.view.setSystemUiVisibility(this.flags);
                }
                ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), this.colorTo, this.colorFrom);
                ofObject.setDuration(300);
                ofObject.addUpdateListener(valueAnimator -> {
                });
                ofObject.start();
            }
            ShareAsPictureAdapter.this.actionMode = null;
        }
    }

}
