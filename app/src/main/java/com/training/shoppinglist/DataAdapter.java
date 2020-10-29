package com.training.shoppinglist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class DataAdapter extends FirestoreRecyclerAdapter<Data, DataAdapter.DataHolder> {

    public DataAdapter(@NonNull FirestoreRecyclerOptions<Data> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DataHolder holder, int position, @NonNull Data model) {
        holder.textViewType.setText(model.getType());
        holder.textViewAmount.setText(model.getAmount());
        holder.textViewNote.setText(model.getNote());
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent,false);
        return new DataHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class DataHolder extends RecyclerView.ViewHolder {

        TextView textViewType;
        TextView textViewAmount;
        TextView textViewNote;

        public DataHolder(@NonNull View itemView) {
            super(itemView);
            textViewType = itemView.findViewById(R.id.type);
            textViewAmount = itemView.findViewById(R.id.amount);
            textViewNote = itemView.findViewById(R.id.note);
        }

    }

}
