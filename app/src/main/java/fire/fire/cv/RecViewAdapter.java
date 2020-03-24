package fire.fire.cv;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import fire.fire.cv.DiffUtil.Information_Callback;

public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.MyViewHolder> implements SearchView.OnQueryTextListener {


    public ArrayList<Information> result;
    public Activity act;
    GoogleMapOptions options;
    private RecyclerView mRecyclerView;

    public RecViewAdapter( ArrayList<Information> result,Activity act ){
        this.result = result;
        this.act = act;

    }


    @Override
    public boolean onQueryTextSubmit(String query) { return false; }

    @Override
    public boolean onQueryTextChange(String newText) { return false; }

    public void filterList(ArrayList<Information> filteredList) {

        result = filteredList;
        notifyDataSetChanged();
        // updateLista(result);
    }



    @NonNull
    @Override
    public RecViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_main_rv_layout, parent, false);

        return new MyViewHolder(v);
    }



    public void updateLista(ArrayList <Information>  lista) {
        Information_Callback diffCallback = new Information_Callback(this.result, lista);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        diffResult.dispatchUpdatesTo(this);
        this.result.clear();
        this.result.addAll(lista);

    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }





    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull List<Object> payloads) {


        if (payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads);
        }
        else {
            Bundle o = (Bundle) payloads.get(0);


            for (String key : o.keySet()) {
                if(key.equals("country") || key.equals("state") ){
              //      Log.e("infochanged" , "country "+position+" : country Changed");

                    String province_state = result.get(position).getProvince_state();
                    String country_region = result.get(position).getCountry_region();

                    holder.country_stateTV.setText(country_region + (!province_state.equals("") ? "   ,  State: "  + province_state: ""));
                }


                if(key.equals("last_update") ){
               //     Log.e("infochanged" , "last_update "+position+" : last_update Changed");
                    holder.lastUpdateTV.setText("Last update:  " + result.get(position).getLast_update());
                }


                if(key.equals("confirmed") ){
                 //   Log.e("infochanged" , "confirmed "+position+" : confirmed Changed");
                    holder.confirmedTV.setText("Confirmed: " + result.get(position).getConfirmed());
                }

                if(key.equals("deaths") ){
                //    Log.e("infochanged" , "deaths "+position+" : deaths Changed");
                    holder.deathTV.setText("Deaths: " + result.get(position).getDeaths());
                }


                if(key.equals("recovered") ){
                 //   Log.e("infochanged" , "recovered "+position+" : recovered Changed");
                    holder.recoveredTV.setText("Recovered: " + result.get(position).getRecovered());
                }

                if(key.equals("latitude") || key.equals("longitude")){
                //    Log.e("infochanged" , "latitude "+position+" : latitude Changed");

                    //   setMapLocation(holder.map,  result.get(position).getLatitude(),result.get(position).getLongitude());

                }






            }
        }
    }




    @Override
    public void onBindViewHolder(@NonNull RecViewAdapter.MyViewHolder holder, int position) {

        String province_state,country_region, last_update, confirmed, deaths, recovered, latitude, longitude;
        boolean isExpanded = result.get(position).isExpanded();
        options = new GoogleMapOptions().liteMode(true);

        province_state = result.get(position).getProvince_state();
        country_region = result.get(position).getCountry_region();
        last_update = result.get(position).getLast_update();
        confirmed = result.get(position).getConfirmed();
        deaths = result.get(position).getDeaths();
        recovered = result.get(position).getRecovered();
        latitude = result.get(position).getLatitude();
        longitude = result.get(position).getLongitude();

        holder.country_stateTV.setText(country_region + (!province_state.equals("") ? "   ,  State: "  + province_state: ""));
        holder.lastUpdateTV.setText("Last update:  " + last_update);
        holder.confirmedTV.setText("Confirmed: " + confirmed);
        holder.deathTV.setText("Deaths: " + deaths);
        holder.recoveredTV.setText("Recovered: " + recovered);
        holder.llExpandArea.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.mapView.setTag(position);
        if (holder.mapView != null) {
            holder.mapView.onCreate(null);
            holder.mapView.onResume();
            holder.mapView.getMapAsync(googleMap -> {
                MapsInitializer.initialize(act);
                holder.map = googleMap;
                if (!latitude.equals("") && !longitude.equals("")) {

                    double lat = Double.parseDouble(latitude);
                    double lon = Double.parseDouble(longitude);
                    LatLng coordinates = new LatLng(lat, lon);
                    // Add a marker for this item and set the camera
                    googleMap.addMarker(new MarkerOptions().position(coordinates));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 5));
                }
            });
        }




    }




    @Override
    public int getItemCount() {
        return result.size();
    }



    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        // Cleanup MapView here?
        if (holder.map != null)
        {
            holder.map.clear();
            holder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView country_stateTV, lastUpdateTV ,confirmedTV,deathTV,recoveredTV;
        public ConstraintLayout llExpandArea;
        public MapView mapView;
        public GoogleMap map;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            country_stateTV = itemView.findViewById(R.id.countryTV);
            lastUpdateTV = itemView.findViewById(R.id.state_lastUpdateTV);
            llExpandArea = itemView.findViewById(R.id.llExpandArea);
            confirmedTV = itemView.findViewById(R.id.confirmedTV);
            deathTV = itemView.findViewById(R.id.deathTV);
            recoveredTV = itemView.findViewById(R.id.recoveredTV);
            mapView = itemView.findViewById(R.id.map);
            itemView.setOnClickListener(this);


        }



        @Override
        public void onClick(View v) {

            int position = mRecyclerView.getChildLayoutPosition(itemView);
            boolean isExpanded = result.get(position).isExpanded();

            if (isExpanded)
                result.get(position).setExpanded(false);
            else
                result.get(position).setExpanded(true);


            notifyItemChanged(position);

        }



    }












}