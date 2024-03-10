package com.kyata.pdfreader.view;

public interface IFilter {
    void filterReadingPDF();

    void filterCompletedPDF();

    void filterNewPDF();

    void sortSize();

    void sortDate();

    void sortName(boolean alpha);

    void noFilter(boolean notifi);
}
