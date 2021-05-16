package be.vsol.tools;

import be.vsol.util.Int;

import java.time.LocalDate;

public class Sig {

    public enum Publisher { SIGI_DEV, SIGI_HOME, JURIEN_DEV }

    private final String appTitle;
    private final int major, minor, update;
    private final Publisher publisher;
    private final LocalDate date;

    public Sig(String appTitle, int major, int minor, int update, Publisher publisher, LocalDate date) {
        this.appTitle = appTitle;
        this.major = major;
        this.minor = minor;
        this.update = update;
        this.publisher = publisher;
        this.date = date;
    }

    public String getVersion() {
        return Int.format(major, 1) + "." + Int.format(minor, 2) + "." + Int.format(update, 3);
    }

    @Override public String toString() {
        return appTitle + " v " + getVersion();
    }

    public String getAppTitle() {
        return appTitle;
    }

    public String getFolder() { return "C:/" + appTitle.replace(" ", "_"); }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getUpdate() {
        return update;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public LocalDate getDate() { return date; }

}
