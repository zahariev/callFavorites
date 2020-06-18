package com.nedeleva.u3;

import android.net.Uri;

import java.util.Objects;

public class CallEntry {
    private String name;
    private String number;
    private Uri uri;


    public CallEntry() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setImageUri(Uri uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public Uri getImageUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallEntry callEntry = (CallEntry) o;
        return Objects.equals(name, callEntry.name) &&
                Objects.equals(number, callEntry.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, number);
    }
}
