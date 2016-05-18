package com.beeselmane.simplehome;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DirectorySerialization
{
    public static DirectoryPackage directoryPackageFromFile(File file, String name) {
        return null;
    }

    public static void directoryPackageToFile(DirectoryPackage directoryPackage, File file) {
        return;
    }

    public static void addMissingAppsFromList(DirectoryPackage directoryPackage, List<AppPackage> list) {
        return;
    }

    public static DirectoryPackage directoryPackageFromAppList(String name, List<AppPackage> apps) {
        List<List<AppPackage>> directoryLists = new ArrayList<>();
        List<PackageEntry> finalEntries = new ArrayList<>();

        Collections.sort(apps, new Comparator<AppPackage>() {
            @Override
            public int compare(AppPackage lhs, AppPackage rhs) {
                return lhs.getUserLabel().compareToIgnoreCase(rhs.getUserLabel());
            }
        });

        String package_lists[][] = {
            {
                "com.cyanogenmod.filemanager",
                "com.cyanogenmod.eleven",
                "com.android.gallery3d",
                "com.android.terminal",
                "org.cyanogenmod.audiofx",
                "org.cyanogenmod.theme.chooser"
            }, {
                "com.android.contacts",
                "com.android.dialer",
                "com.android.browser",
                "com.android.calendar",
                "com.android.deskclock",
                "com.android.email",
                "com.android.soundrecorder",
                "com.android.messaging",
                "com.google.android.apps.photos",
                "com.android.providers.downloads.ui"
            }, {
                "com.android.vending",
                "com.google.android.apps.docs.editors.docs",
                "com.google.android.apps.docs",
                "com.google.android.gm",
                "com.google.android.launcher",
                "com.google.android.talk",
                "com.google.android.youtube",
                "com.google.android.googlequicksearchbox",
                "com.google.android.apps.inbox"
            }, {
                "de.robv.android.xposed.installer",
                "com.marz.snapprefs"
            }, {
                "com.android.calculator2",
                "com.android.chrome",
                "com.google.android.GoogleCamera",
                "org.cyanogenmod.screencast",
                "com.google.android.talk",
                "com.snapchat.android"
            }
        };

        for (int i = 0; i < 5; i++)
            directoryLists.add(new ArrayList<AppPackage>());

        for (int k = 0; k < apps.size(); k++)
        {
            int toRemove = -1;

            for (int i = 0; i < package_lists.length; i++)
            {
                for (int j = 0; j < package_lists[i].length; j++)
                {
                    if (package_lists[i][j].equals(apps.get(k).getPackageName()))
                    {
                        directoryLists.get(i).add(apps.get(k));
                        toRemove = k;
                    }
                }
            }

            if (toRemove != -1)
            {
                apps.remove(toRemove);
                k--;
            }
        }

        Collections.reverse(directoryLists.get(3));

        DirectoryPackage packages[] = {
            new DirectoryPackage("CM Apps", directoryLists.get(0)),
            new DirectoryPackage("Default Android Apps", directoryLists.get(1)),
            new DirectoryPackage("Google Apps", directoryLists.get(2)),
            new DirectoryPackage("Xposed Apps", directoryLists.get(3)),
            new DirectoryPackage("Main Apps", directoryLists.get(4))
        };

        for (int i = 0; i < 5; i++)
            finalEntries.add(packages[i]);

        finalEntries.addAll(apps);
        return new DirectoryPackage(name, finalEntries);
    }

    private DirectorySerialization() {}
}
