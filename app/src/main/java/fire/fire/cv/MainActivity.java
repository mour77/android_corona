package fire.fire.cv;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import fire.fire.cv.DiffUtil.CustomSearchDialog;
import fire.fire.cv.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements MyListener {

    private ActivityMainBinding bd;
    private ArrayList<Information> lista ,listForSort;
    private AlertDialog alertDialog;
    private RecViewAdapter adapterRV;
    private boolean  isFirstTimeSort = true;
    private String [] sortList = new String[]{"By Confirmed", "by Country", "by Recovered"};
    private String [] countries;
    private static int SORT_BY_COUNTRY = 1  ,SORT_BY_CONFIRMED = 0 , SORT_BY_RECOVERED = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bd = ActivityMainBinding.inflate(getLayoutInflater());
        View view = bd.getRoot();
        setContentView(view);

        alertDialog = Utils.setAlertDialog(this);

        listForSort = new ArrayList<>();
        Utils.setRecyclerView(bd.rv, this);
        setSpinnerSortedList();
        searchETListener();

        swipeRefreshLayoutListener();


        customSearchListener();

        downloadData(DownloadInfo_task.INFO_LATEST_GLOBAL_DATA,null);
        downloadData(DownloadInfo_task.INFO_GET_ALL_COUNTRIES,null);


    }

    private void customSearchListener() {
        bd.dialogBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomSearchDialog df = new CustomSearchDialog();
                Bundle putextra = new Bundle();
                putextra.putStringArray("countriesList", countries);
                df.setArguments(putextra);
                df.show(getSupportFragmentManager() , "Dialog");
            }
        });
    }

    private void swipeRefreshLayoutListener() {


        bd.swipeRefreshLayout.setOnRefreshListener(this::swipeRefreshLayoutListener);
        bd.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        bd.swipeRefreshLayout.post(() -> downloadData(DownloadInfo_task.INFO_LATEST_GLOBAL_DATA,null));

    }




    public void downloadData(String typeData, String country,String date){
        alertDialog.show();
        DownloadInfo_task task = new DownloadInfo_task(this,typeData,country,date);
        task.execute();
    }

    public void downloadData(String typeData, String param){
        alertDialog.show();
        DownloadInfo_task task = new DownloadInfo_task(this,typeData,param);
        task.execute();
    }

    private void setSpinnerSortedList(){



        ArrayAdapter adapterSortSP = new ArrayAdapter(this, R.layout.spinner_layout3, sortList);

        adapterSortSP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bd.sortSP.setAdapter(adapterSortSP);

        bd.sortSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (isFirstTimeSort)
                    isFirstTimeSort = false;
                else {
                    int itemSelected = bd.sortSP.getSelectedItemPosition();
                    bd.searchET.clearFocus();
                   // adapterRV.updateLista(sortByList(listForSort, itemSelected));
                     adapterRV.updateLista(sortByList((ArrayList<Information>) adapterRV.result.clone(), itemSelected));

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


    }

    private void searchETListener(){
        bd.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                filter(editable.toString());
            }
        });
    }


    private void filter(String text){

        ArrayList<Information> filteredList =  new ArrayList();

        for (Information item : listForSort){
            if (item.getCountry_region().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
            else  if (item.getProvince_state().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }


        if (adapterRV != null)
             adapterRV.updateLista(filteredList);


    }



    @Override
    public void hereIsYourData(JSONArray jarray) {

        if (jarray != null) {
            alertDialog.show();
            lista = new ArrayList<>();
            for (int i = 0; i < jarray.length(); i++) {
                try {
                    JSONObject s = jarray.getJSONObject(i);
                    Information info = new Information();
                    info.setProvince_state(s.optString("Province/State"));
                    info.setCountry_region(s.optString("Country/Region"));
                    info.setLast_update(Utils.changeFormatDate(s.optString("Last Update")));
                    info.setConfirmed(s.optString("Confirmed"));
                    info.setDeaths(s.optString("Deaths"));
                    info.setRecovered(s.optString("Recovered"));
                    info.setLatitude(s.optString("Latitude"));
                    info.setLongitude(s.optString("Longitude"));


                    if (!info.getCountry_region().equals(""))
                        lista.add(info);

                    listForSort = (ArrayList<Information>) lista.clone();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (adapterRV == null) {
                adapterRV = new RecViewAdapter(lista, MainActivity.this);
                bd.rv.setAdapter(adapterRV);
            }

            else
                adapterRV.updateLista(lista);


            if (lista.size() == 0)
                Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();


            alertDialog.dismiss();

        }

        bd.swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void hereIsYourCountries(String[] array) {

        Arrays.sort(array);
        String tempArray [] = new String[array.length + 1];
        tempArray[0] = "Choose country (optional)";

        for (int i=0; i<array.length; i++){
            tempArray[i+1] = array[i];
        }

        countries = tempArray;



    }






    private ArrayList<Information> sortByList(ArrayList<Information> list, int sortBy){



            Collections.sort(list, new Comparator<Information>() {
                @Override
                public int compare(Information item1, Information item2) {
                    if (sortBy == SORT_BY_COUNTRY)
                        return item1.getCountry_region().compareTo(item2.getCountry_region());
                    else if (sortBy == SORT_BY_CONFIRMED) {
                        Integer x = item1.getConfirmed().equals("") ? 0 : Integer.parseInt(item1.getConfirmed());
                        Integer z = item2.getConfirmed().equals("") ? 0 : Integer.parseInt(item2.getConfirmed());
                        return x.compareTo(z);
                    }
                    else if (sortBy == SORT_BY_RECOVERED){

                        Integer x = item1.getRecovered().equals("") ? 0 : Integer.parseInt(item1.getRecovered());
                        Integer z = item2.getRecovered().equals("") ? 0 : Integer.parseInt(item2.getRecovered());

                        return x.compareTo(z);

                    }
                    return -1;
                }
            });

            return list;
        }
}
