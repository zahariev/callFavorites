package com.nedeleva.u3;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RecyclerActivity extends AppCompatActivity implements OnCallClick {

    private RecyclerView recyclerView;
    private int REQUEST_CALL_LOG = 1001;
    private int REQUEST_CALL_PHONE = 1002;
    private int REQUEST_READ_CONTACTS = 1003;

    private List<CallEntry> callEntries;
    private Map<CallEntry, Integer> numberOfCalls;
    private TextView callStats;
    private boolean canCallPhone = false;
    private boolean canReadContacts = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        callStats = findViewById(R.id.callStats);

        recyclerView = findViewById(R.id.recycler);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.READ_CALL_LOG,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.SEND_SMS
        };

        if (!hasPermissions( PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

            callEntries = new ArrayList<>();
            numberOfCalls = new HashMap<>();

            Cursor callLog = getAllCalls(getContentResolver());
            getCallsInfo(callLog);

            Toast.makeText(this,
                    String.valueOf(callEntries.size()), Toast.LENGTH_SHORT).show();

            populateRecyclerView();


    }

    public boolean hasPermissions( String... permissions) {
        Context context = RecyclerActivity.this;
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void populateRecyclerView() {
        RecyclerAdapter adapter = new RecyclerAdapter(numberOfCalls);
        adapter.setCallListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

//    private void populateStats() {
//        final StringBuilder builder = new StringBuilder();
//        for (CallEntry e : numberOfCalls.keySet()) {
//            builder.append(e.getName());
//            builder.append("\n");
//            builder.append(e.getNumber());
//            builder.append("\n");
//            builder.append("called: ");
//            builder.append(numberOfCalls.get(e));
//            builder.append("\n");
//            builder.append("----------------");
//            builder.append("\n");
//        }
//        callStats.setText(builder.toString());
//    }

    private void getCallsInfo(Cursor cursor) {
        while (cursor.moveToNext()) {
            CallEntry entry = new CallEntry();
            final String number = cursor
                    .getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));

            final String name = cursor
                    .getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));


            final String uri =
                    (cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI)));


            if(uri!=null) {
                entry.setImageUri(Uri.parse(uri));
            }
            else if(canReadContacts){

                entry.setImageUri(getPhotoUri(getContactIDFromNumber(number)));
            }

            entry.setNumber(number);



            if (name == null) {
                entry.setName("Unknown");
            } else {
                entry.setName(name);
            }
//            callEntries.add(entry);

            if (numberOfCalls.containsKey(entry)) {
                numberOfCalls.put(entry, numberOfCalls.get(entry) + 1);
            } else {
                numberOfCalls.put(entry, 1);
            }
        }
//        populateEntries();
    }

        public Uri getPhotoUri(int id) {
           String ID = String.valueOf(id);

        try {
            Cursor cur = getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.NUMBER + "=" + ID + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);

            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(ID));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    private void populateEntries() {
        for (CallEntry entry : callEntries) {
            if (numberOfCalls.containsKey(entry)) {
                numberOfCalls.put(entry, numberOfCalls.get(entry) + 1);
            } else {
                numberOfCalls.put(entry, 1);
            }
        }
    }

    public int getContactIDFromNumber(String contactNumber)
    {
        contactNumber = Uri.encode(contactNumber);
        int phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,contactNumber),new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        while(contactLookupCursor.moveToNext()){
            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();

        return phoneContactID;
    }

    private Cursor getAllCalls(ContentResolver contentResolver) {

        Calendar calendar = Calendar.getInstance();
        String strOrder1 = android.provider.CallLog.Calls.DATE + " DESC limit 100";
//        String whereValue = new String[]{String.valueOf(calendar.getTimeInMillis()),String.valueOf(calendar.)};

        Uri callUri = Uri.parse("content://call_log/calls");
        return getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null,
                strOrder1);

//        String order = android.provider.CallLog.Calls.DATE + " DESC";
//        Uri callLogUri = Uri.parse("content://call_log/calls");
//        return contentResolver.query(callLogUri, null,
//                null, null, order);
    }

    @Override
    public void callNumber(String number) {
        if (canCallPhone) {
            Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            startActivity(call);
        }
    }




}
