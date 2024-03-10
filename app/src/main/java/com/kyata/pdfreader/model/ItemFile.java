package com.kyata.pdfreader.model;

import com.kyata.pdfreader.App;

import java.io.Serializable;
import java.util.List;

public class ItemFile implements Serializable {
    private String path;
    private boolean isFavorite;
    private int numPage;

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isFavorite() {
        return isFavorite;
    }


    public ItemFile(String path, int numPage) {
        try {
            this.path = path;
            this.numPage = numPage;

            List<FavoriteFile> list = App.getInstance().getDatabase().favoritetDao().getList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPath().equals(path)) {
                    isFavorite = true;
                    return;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ItemFile(String path) {
        this.path = path;
    }

    public int getNumPage() {
        return numPage + 1;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
