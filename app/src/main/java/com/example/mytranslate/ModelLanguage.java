package com.example.mytranslate;

public class ModelLanguage {
    String langaugeCode;
    String languageTitle;

    public ModelLanguage(String langaugeCode, String languageTitle) {
        this.langaugeCode = langaugeCode;
        this.languageTitle = languageTitle;
    }

    public String getLangaugeCode() {
        return langaugeCode;
    }

    public void setLangaugeCode(String langaugeCode) {
        this.langaugeCode = langaugeCode;
    }

    public String getLanguageTitle() {
        return languageTitle;
    }

    public void setLanguageTitle(String languageTitle) {
        this.languageTitle = languageTitle;
    }
}
