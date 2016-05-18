package com.beeselmane.simplehome;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class SettingsBaseActivity extends PreferenceActivity {
    private AppCompatDelegate delegate = null;

    private AppCompatDelegate getDelegate()
    {
        if (this.delegate == null) this.delegate = AppCompatDelegate.create(this, null);
        return this.delegate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.getDelegate().installViewFactory();
        this.getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        this.getDelegate().onPostCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public MenuInflater getMenuInflater() {
        return this.getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(int layoutResID) {
        this.getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        this.getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        this.getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        this.getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        this.getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color)
    {
        super.onTitleChanged(title, color);
        this.getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        this.getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getDelegate().onDestroy();
    }

    @Override
    public void invalidateOptionsMenu() {
        this.getDelegate().invalidateOptionsMenu();
    }
}
