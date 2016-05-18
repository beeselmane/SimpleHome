package com.beeselmane.simplehome;

import java.util.List;

public class DirectoryPackage extends PackageEntry {
    private List<? extends PackageEntry> entries = null;

    public DirectoryPackage(String userLabel, List<? extends PackageEntry> entries) {
        super(userLabel);

        this.entries = entries;
    }

    public List<PackageEntry> getEntries() {
        return (List<PackageEntry>)this.entries;
    }

    public void addEntry(PackageEntry entry) {
        ((List<PackageEntry>)this.entries).add(entry);
    }

    public void removeEntry(PackageEntry entry) {
        if (this.hasEntry(entry)) this.entries.remove(entry);
    }

    public boolean hasEntry(PackageEntry entry) {
        return this.entries.contains(entry);
    }

    public int findEntry(PackageEntry entry) {
        if (this.hasEntry(entry)) return this.entries.indexOf(entry);
        else return -1;
    }

    public PackageEntry findEntryWithUserLabel(String label, boolean ignoreCase) {
        for (PackageEntry entry : this.entries)
        {
            if (ignoreCase && entry.getUserLabel().equalsIgnoreCase(label)) return entry;
            else if (!ignoreCase && entry.getUserLabel().equals(label)) return entry;
        }

        return null;
    }

    public PackageEntry getEntry(int i) {
        return this.entries.get(i);
    }

    public int getNumberOfEntries() {
        return this.entries.size();
    }
}
