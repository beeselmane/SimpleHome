package com.beeselmane.simplehome;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class AppPackage extends PackageEntry {
    private ApplicationInfo applicationInfo = null;
    private boolean canUninstall = false;
    private boolean isSystem = false;
    private boolean canOpen = false;
    private String packageName = "";

    public AppPackage(ResolveInfo info, PackageManager packageManager) {
        super(info.loadLabel(packageManager).toString(), info.loadIcon(packageManager));
        this.packageName = info.activityInfo.packageName;

        if (this.getUserIcon() == null)
            this.setUserIcon(SHApplication.getAppContext().getDrawable(android.R.drawable.sym_def_app_icon));

        boolean isUpdatedSystemApp = (info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
        this.isSystem = (info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        this.canUninstall = !isSystem || isUpdatedSystemApp;

        this.applicationInfo = info.activityInfo.applicationInfo;
        this.canOpen = true;
    }

    public ApplicationInfo applicationInfo() {
        return this.applicationInfo;
    }

    public boolean canUninstall() {
        return this.canUninstall;
    }

    public boolean isSystem() {
        return this.isSystem;
    }

    public boolean canOpen() {
        return this.canOpen;
    }

    public String getPackageName() {
        return this.packageName;
    }

    @Override
    public String toString() {
        return String.format("%s <%s>", this.getUserLabel(), this.getPackageName());
    }
}
