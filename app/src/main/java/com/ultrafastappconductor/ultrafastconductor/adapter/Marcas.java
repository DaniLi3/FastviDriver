package com.ultrafastappconductor.ultrafastconductor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.ultrafastappconductor.ultrafastconductor.Models.Vehiculos;
import com.ultrafastappconductor.ultrafastconductor.Providers.VehiculoProvider;
import com.ultrafastappconductor.ultrafastconductor.R;

public class Marcas extends FirebaseRecyclerAdapter<Vehiculos, Marcas.ViewHolder> {
    com.ultrafastappconductor.ultrafastconductor.Providers.VehiculoProvider vehiculoProvider;
    private Context mcontex;



    public Marcas(@NonNull FirebaseRecyclerOptions<Vehiculos> options, Context context) {
        super(options);
        vehiculoProvider=new VehiculoProvider();
        mcontex=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Vehiculos model) {

        holder.destinoo.setText(model.getMarca());
        /*holder.origenn.setText(model.getDestination());
        bookinProvider.getDriver(model.getIdClient()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String origen =snapshot.child("origin").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_vehiculo_booking,parent,false);
        return new ViewHolder( view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView origenn;
        private TextView destinoo;



        public ViewHolder(View view){
            super(view);
           // origenn = view.findViewById(R.id.txtmarcas);
            destinoo = view.findViewById(R.id.txtmarcas);

        }

    }
}
