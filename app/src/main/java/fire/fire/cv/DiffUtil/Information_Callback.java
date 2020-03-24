package fire.fire.cv.DiffUtil;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

import fire.fire.cv.Information;

public class Information_Callback extends DiffUtil.Callback {

    private   ArrayList<Information>  oldLista = new ArrayList<>();
    private   ArrayList <Information>  newLista  = new ArrayList<>();



    public Information_Callback(ArrayList<Information> oldList, ArrayList<Information> newList) {
        this.oldLista = oldList;
        this.newLista = newList;


    }



    @Override
    public int getOldListSize() {
        return oldLista.size();
    }

    @Override
    public int getNewListSize() {
        return newLista.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
      //  Log.e("eleooooooooos" , "String.valueOf(oldItemPosition) ");

        return true; }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

      //  Log.e("theseis" , String.valueOf(oldItemPosition) + " , " +  String.valueOf(newItemPosition));

        int result = newLista.get(newItemPosition).compareTo(oldLista.get(oldItemPosition));
        if (result==0){
            return true;
        }
        return false;
    }



    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        Information newItem = newLista.get(newItemPosition);
        Information oldItem = oldLista.get(oldItemPosition);

        Bundle diff = new Bundle();
        if(!newItem.getCountry_region().equals(oldItem.getCountry_region()))
            diff.putString("country", newItem.getCountry_region());

        if(!newItem.getProvince_state().equals(oldItem.getProvince_state()))
            diff.putString("state", newItem.getProvince_state());

        if(!newItem.getLast_update().equals(oldItem.getLast_update()))
            diff.putString("last_update", newItem.getLast_update());

        if(!newItem.getConfirmed().equals(oldItem.getConfirmed()))
            diff.putString("confirmed", newItem.getConfirmed());

        if(!newItem.getDeaths().equals(oldItem.getDeaths()))
            diff.putString("deaths", newItem.getDeaths());

        if(!newItem.getRecovered().equals(oldItem.getRecovered()))
            diff.putString("recovered", newItem.getRecovered());

        if(!newItem.getLatitude().equals(oldItem.getLatitude()))
            diff.putString("latitude", newItem.getLatitude());


        if(!newItem.getLongitude().equals(oldItem.getLongitude()))
            diff.putString("longitude", newItem.getLongitude());

        if (diff.size()==0)
            return null;


        return diff;
    }

}
