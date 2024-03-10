package com.kyata.pdfreader.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.kyata.pdfreader.App;
import com.kyata.pdfreader.R;
import com.kyata.pdfreader.model.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LanguageUtils {

    private static Language sCurrentLanguage = null;

    public static Language getCurrentLanguage() {
        if (sCurrentLanguage == null) {
            sCurrentLanguage = initCurrentLanguage();
        }
        return sCurrentLanguage;
    }


    private static Language initCurrentLanguage() {
        Language currentLanguage =
                SharedPrefs.getInstance().get(SharedPrefs.LANGUAGE, Language.class);
        if (currentLanguage != null) {
            return currentLanguage;
        }
        currentLanguage = new Language(0,
                "English",
                "en");
        SharedPrefs.getInstance().put(SharedPrefs.LANGUAGE, currentLanguage);
        return currentLanguage;
    }

    /**
     * return language list from string.xml
     */
    public static List<Language> getLanguageData() {
        List<Language> languageList = new ArrayList<>();
        List<String> languages =
                Arrays.asList(App.getInstance().getResources().getStringArray(R.array.language));
        for (int i = 0; i < languages.size(); i++) {
            String[] str = languages.get(i).split(",");
            languageList.add(new Language(i, str[1], str[0]));
        }
        return languageList;
    }

    /**
     * load current locale and change language
     */
    public static void loadLocale(Context context) {
        changeLanguage(context, initCurrentLanguage());
    }


    public static void changeLanguage(Context context, Language language) {
        if (language == null) {
            return;
        }
        try {
            SharedPrefs.getInstance().put(SharedPrefs.LANGUAGE, language);
            sCurrentLanguage = language;

            Locale locale = new Locale(language.getCode());
            Locale.setDefault(locale);

            Resources resources = context.getResources();

            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;

            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
