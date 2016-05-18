package com.beeselmane.simplehome;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;

public class SettingsActivity extends SettingsBaseActivity {
    public static final class LauncherPreferenceFragment extends PreferenceFragment {
        private static final int IMAGE_PICKER_REQUEST_CODE = 1;
        private SettingsActivity settingsActivity = null;
        private LauncherPreferenceFragment self = this;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.addPreferencesFromResource(R.xml.preferences);

            Preference openSystemSettings = this.findPreference(SHApplication.getAppContext().getString(R.string.key_open_system));
            Preference setWallpaperButton = this.findPreference(this.getString(R.string.key_set_wallpaper));

            openSystemSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    self.settingsActivity.openSystemSettings();
                    return true;
                }
            });

            setWallpaperButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    self.setWallpaper();
                    return true;
                }
            });
        }

        private void setWallpaper() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            this.startActivityForResult(Intent.createChooser(intent, this.getString(R.string.settings_select_wallpaper)), LauncherPreferenceFragment.IMAGE_PICKER_REQUEST_CODE);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == LauncherPreferenceFragment.IMAGE_PICKER_REQUEST_CODE)
            {
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(SHApplication.getAppContext());
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    WindowManager windowManager = (WindowManager)SHApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
                    windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                    Uri dataURI = data.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(SHApplication.getAppContext().getContentResolver(), dataURI);
                        wallpaperManager.setBitmap(bitmap);
                        wallpaperManager.suggestDesiredDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels);
                        Toast.makeText(SHApplication.getAppContext(), SHApplication.getAppContext().getString(R.string.settings_wallpaper_success), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        Toast.makeText(SHApplication.getAppContext(), SHApplication.getAppContext().getString(R.string.settings_wallpaper_failure), Toast.LENGTH_SHORT).show();
                        ex.printStackTrace(System.err);
                    }
                } else {
                    Toast.makeText(SHApplication.getAppContext(), SHApplication.getAppContext().getString(R.string.settings_wallpaper_cancel), Toast.LENGTH_SHORT).show();
                }
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LauncherPreferenceFragment preferenceFragment = new LauncherPreferenceFragment();
        preferenceFragment.settingsActivity = this;
        this.getFragmentManager().beginTransaction().replace(android.R.id.content, preferenceFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
            } break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        DirectoryViewActivity.overrideTransitionsInActivity(this, true);
    }

    private void openSystemSettings()
    {
        Intent intent = this.getPackageManager().getLaunchIntentForPackage("com.android.settings");
        this.startActivity(intent);
        DirectoryViewActivity.overrideTransitionsInActivity(this, false);
    }
}
