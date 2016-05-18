package com.beeselmane.simplehome;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DirectoryViewActivity extends Activity implements SearchView.OnQueryTextListener {
    public static final String INTENT_DIRECTORY_PATH_EXTRA = "directory_path";

    private static final int REVERSE_TRANSITION_ENTER = R.anim.slide_on_right;
    private static final int REVERSE_TRANSITION_EXIT = R.anim.slide_off_right;

    private static final int TRANSITION_ENTER = R.anim.slide_on_left;
    private static final int TRANSITION_EXIT = R.anim.slide_off_left;

    private DirectoryViewActivity self = this;
    private String thisPath = null;

    private DirectoryPackage representedObject = null;
    private List<PackageEntry> currentEntries = null;
    private MenuItem searchMenuItem = null;
    private ListView listView = null;

    private SharedPreferences preferences = null;
    private boolean ignoreCase = true;
    private boolean darkMode = false;

    public static void overrideTransitionsInActivity(Activity activity, boolean reverse) {
        int enter, exit;

        if (reverse) {
            enter = DirectoryViewActivity.REVERSE_TRANSITION_ENTER;
            exit = DirectoryViewActivity.REVERSE_TRANSITION_EXIT;
        } else {
            enter = DirectoryViewActivity.TRANSITION_ENTER;
            exit = DirectoryViewActivity.TRANSITION_EXIT;
        }

        activity.overridePendingTransition(enter, exit);
    }

    private void overrideTransitions(boolean reverse) {
        DirectoryViewActivity.overrideTransitionsInActivity(this, reverse);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.onNewIntent(this.getIntent());

        this.setContentView(R.layout.directory_view);
        this.setTitle(this.representedObject.getUserLabel());

        this.preferences = PreferenceManager.getDefaultSharedPreferences(SHApplication.getAppContext());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (this.searchMenuItem != null && this.searchMenuItem.isActionViewExpanded())
            this.searchMenuItem.collapseActionView();

        boolean darkMode = this.preferences.getBoolean(this.getString(R.string.key_dark_mode), false);
        this.ignoreCase = this.preferences.getBoolean(this.getString(R.string.key_ignore_case), true);

        if (this.listView == null || (darkMode != this.darkMode))
        {
            View backgroundView = this.findViewById(R.id.background_view);
            this.darkMode = darkMode;

            this.listView = (ListView)this.findViewById(R.id.entry_list);
            this.setupListView();

            if (this.darkMode) {
                int bgColor = ContextCompat.getColor(this, R.color.dark_mode_cell_color);
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

                //this.listView.setDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.dark_mode_divider_color)));
                backgroundView.setBackgroundColor(bgColor);
            } else {
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                this.getWindow().setNavigationBarColor(Color.TRANSPARENT);
                this.listView.setDivider(this.defaultListViewDivider());
                backgroundView.setBackgroundColor(Color.TRANSPARENT);
            }

            this.getWindow().setStatusBarColor(0xFF282C38);
            //this.getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        this.toggleKeyboard(true);
        this.showAllItems();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.overrideTransitions(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getAction() == null || intent.getAction().equals(Intent.ACTION_MAIN)) {
            String directoryPath = "/";

            if (intent.getAction() == null)
                directoryPath = intent.getStringExtra(DirectoryViewActivity.INTENT_DIRECTORY_PATH_EXTRA);

            this.thisPath = directoryPath;
            this.representedObject = LauncherApplicationState.directoryPackageForString(directoryPath);
        } else {
            throw new LauncherException("DirectoryViewActivity is only meant to handle certain intents!", null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!this.thisPath.equals("/"))
        {
            ActionBar actionBar = this.getActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.getMenuInflater().inflate(R.menu.mainmenu, menu);
        this.searchMenuItem = menu.findItem(R.id.action_search);

        Intent intent = this.getPackageManager().getLaunchIntentForPackage(this.getString(R.string.store_package));
        if (intent == null) menu.removeItem(R.id.action_store);

        SearchManager searchManager = (SearchManager)this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)this.searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home: {
                this.finish();
            } break;
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                this.overrideTransitions(false);
            } break;
            case R.id.action_store: {
                Intent intent = this.getPackageManager().getLaunchIntentForPackage(this.getString(R.string.store_package));
                this.startActivity(intent);
                this.overrideTransitions(false);
            } break;
            case R.id.action_search: {
                this.showAllItems();
            } break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query.length() == 0)
        {
            this.showAllItems();
            return true;
        }

        if (this.ignoreCase) query = query.toLowerCase();
        List<PackageEntry> entries = this.representedObject.getEntries();
        this.currentEntries = new ArrayList<>();

        for (PackageEntry entry : entries)
        {
            String label = entry.getUserLabel();
            if (this.ignoreCase) label = label.toLowerCase();

            if (label.contains(query)) this.currentEntries.add(entry);
        }

        this.updateListView();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        this.toggleKeyboard(false);
        return true;
    }

    private void toggleKeyboard(boolean forceHide) {
        InputMethodManager keyboardManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (!forceHide) keyboardManager.toggleSoftInput(0, 0);
        else keyboardManager.hideSoftInputFromInputMethod(this.listView.getWindowToken(), 0);
    }

    private void showAllItems() {
        this.currentEntries = this.representedObject.getEntries();
        this.updateListView();
    }

    private void updateListView() {
        listView.setAdapter(new ArrayAdapter<PackageEntry>(this, R.layout.list_item, this.currentEntries) {
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null) view = self.getLayoutInflater().inflate(R.layout.list_item, null);
                PackageEntry entry = self.currentEntries.get(position);

                ImageView imageView = (ImageView)view.findViewById(R.id.item_entry_icon);
                TextView labelView = (TextView)view.findViewById(R.id.item_entry_label);
                TextView subtextView = (TextView)view.findViewById(R.id.item_entry_subtext);

                imageView.setImageDrawable(entry.getUserIcon());
                imageView.setContentDescription(entry.getUserLabel());
                labelView.setText(entry.getUserLabel());

                if (entry instanceof AppPackage) {
                    subtextView.setText(((AppPackage)entry).getPackageName());
                } else if (entry instanceof DirectoryPackage) {
                    int entryCount = ((DirectoryPackage)entry).getNumberOfEntries();
                    String formatString = self.getString(R.string.directory_subtext_format_string);
                    subtextView.setText(String.format(formatString, entryCount));
                } else {
                    throw new LauncherException("PackageEntry is of invalid type!", null);
                }

                return view;
            }
        });
    }

    private void setupListView() {
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PackageEntry entry = self.currentEntries.get(position);
                self.toggleKeyboard(true);

                if (entry instanceof AppPackage) {
                    Intent launchIntent = self.getPackageManager().getLaunchIntentForPackage(((AppPackage) entry).getPackageName());
                    self.startActivity(launchIntent);
                    self.overrideTransitions(false);
                } else if (entry instanceof DirectoryPackage) {
                    Intent directoryIntent = new Intent(self, DirectoryViewActivity.class);
                    directoryIntent.putExtra(INTENT_DIRECTORY_PATH_EXTRA, String.format("%s%d/", self.thisPath, position));
                    self.startActivity(directoryIntent);
                    self.overrideTransitions(false);
                } else {
                    throw new LauncherException("PackageEntry is of invalid type!", null);
                }

                System.out.println("View Clicked");
            }
        });
    }

    private Drawable defaultListViewDivider() {
        TypedArray array = this.getTheme().obtainStyledAttributes(R.style.LauncherTheme, new int[]{android.R.attr.listDivider});
        int attributedResourceID = array.getResourceId(0, 0);
        Drawable divider = this.getDrawable(attributedResourceID);
        array.recycle();

        int lpx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, this.getResources().getDisplayMetrics());
        int rpx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, this.getResources().getDisplayMetrics());

        return new InsetDrawable(divider, lpx, 0, rpx, 0);
    }
}
