package com.bzahariev.callFavorites;

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
        /*
         * incoming and outgoing calls sometimes have different numbers (with or without ext +359 or 00359)
         * searching with (name && number) get duplicate entry for some records.
         * We don't want that :)
         */

        return Objects.equals(name, callEntry.name) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
