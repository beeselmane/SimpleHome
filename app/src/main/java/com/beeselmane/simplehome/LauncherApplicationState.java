package com.beeselmane.simplehome;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class LauncherApplicationState {
    private static DirectoryPackage applicationState = null;
    private static boolean isInitialized = false;

    public static final class PackageStateChangedActivity extends Activity {
        //
    }

    private static void initialize()
    {
        PackageManager packageManager = SHApplication.getAppContext().getPackageManager();
        List<AppPackage> applications = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

        for (ResolveInfo info : activities)
            applications.add(new AppPackage(info, packageManager));

        LauncherApplicationState.applicationState = DirectorySerialization.directoryPackageFromAppList(SHApplication.getAppContext().getString(R.string.root_directory_name), applications);
        DirectorySerialization.addMissingAppsFromList(LauncherApplicationState.applicationState, applications);
        LauncherApplicationState.isInitialized = true;
    }

    public static boolean isPackageInstalled(String packageName) {
        return false;
    }

    public static DirectoryPackage directoryPackageForString(String pathString) {
        if (!LauncherApplicationState.isInitialized) LauncherApplicationState.initialize();
        DirectoryPackage result = applicationState;
        Scanner scanner = new Scanner(pathString);
        scanner.useDelimiter("[/]");

        while (scanner.hasNext())
            result = (DirectoryPackage) result.getEntry(scanner.nextInt());

        return result;
    }
}
