package com.getbubblenow.android.model;

public class AutoUpdatePolicy {
    private boolean driverUpdates;
    private boolean appUpdates;
    private boolean dataUpdates;
    private boolean newStuff;

    public void setDriverUpdates(final boolean driverUpdates) {
        this.driverUpdates = driverUpdates;
    }

    public void setAppUpdates(final boolean appUpdates) {
        this.appUpdates = appUpdates;
    }

    public void setDataUpdates(final boolean dataUpdates) {
        this.dataUpdates = dataUpdates;
    }

    public void setNewStuff(final boolean newStuff) {
        this.newStuff = newStuff;
    }

    public boolean isDriverUpdates() {
        return driverUpdates;
    }

    public boolean isAppUpdates() {
        return appUpdates;
    }

    public boolean isDataUpdates() {
        return dataUpdates;
    }

    public boolean isNewStuff() {
        return newStuff;
    }
}
