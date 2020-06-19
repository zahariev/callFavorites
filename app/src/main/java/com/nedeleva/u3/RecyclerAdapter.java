package com.nedeleva.u3;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Map<CallEntry, Integer> content;
    private List<CallEntry> items;
    private OnCallClick callListener;




    public RecyclerAdapter(Map<CallEntry, Integer> userContent) {
        content = userContent;

        final Map<CallEntry, Integer> sorted = sortByValue(content);
        items = new ArrayList<>();
        for (CallEntry entry : sorted.keySet()) {
            items.add(entry);
        }
    }

    public static Map<CallEntry, Integer> sortByValue(Map<CallEntry, Integer> hm) {
        List<Map.Entry<CallEntry, Integer>> list =
                new LinkedList<>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<CallEntry, Integer>>() {
            public int compare(Map.Entry<CallEntry, Integer> o1,
                               Map.Entry<CallEntry, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<CallEntry, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<CallEntry, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if(items.get(position).getImageUri()!=null){
            holder.image.setImageURI( items.get(position).getImageUri());
        }

        holder.name.setText(items.get(position).getName());
        holder.number.setText(items.get(position).getNumber());

        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                callListener.callNumber("123");
                callListener.callNumber(items.get(position).getNumber());
                Log.d("click",items.get(position).getNumber());
            }
        });
    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setCallListener(OnCallClick callListener) {
        this.callListener = callListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;
        TextView number;
        ImageButton callButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);

            callButton = itemView.findViewById(R.id.callButton);
        }
    }
}
