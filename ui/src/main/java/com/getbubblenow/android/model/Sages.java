package com.getbubblenow.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Sages {

    @SerializedName("sages")
    @Expose
    List<String> sages;

    public void setSages(final List<String> sages) {
        this.sages = sages;
    }

    public List<String> getSages() {
        return sages;
    }
}
