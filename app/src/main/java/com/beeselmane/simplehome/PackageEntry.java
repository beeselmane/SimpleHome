package com.beeselmane.simplehome;

import android.graphics.drawable.Drawable;

public class PackageEntry {
    private Drawable userIcon = null;
    private String userLabel = "";

    public PackageEntry(String userLabel, Drawable userIcon)
    {
        this.userLabel = userLabel;
        this.userIcon = userIcon;
    }

    public PackageEntry(String userLabel)
    {
        this.userIcon = SHApplication.getAppContext().getDrawable(R.drawable.ic_circle);
        this.userLabel = userLabel;
    }

    public String getUserLabel() {
        return this.userLabel;
    }

    public Drawable getUserIcon() {
        return this.userIcon;
    }

    public void setUserIcon(Drawable userIcon) {
        this.userIcon = userIcon;
    }
}