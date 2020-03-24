package fire.fire.cv;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadInfo_task extends AsyncTask<String, Void, String> {


    public static String INFO_BY_COUNTRY = "/data/country/";
    public static String INFO_GET_ALL_COUNTRIES = "/data/meta/countries";
    public static String INFO_BY_DATE = "/data/date/";
    public static String INFO_LATEST_GLOBAL_DATA = "/data";
    public static String INFO_BY_COUNTRY_AND_DATE = "/data/custom?country=";

    private Context ctx;
    private String BASE_URL = "https://covid-api.quintessential.gr";
    private MyListener listener ;
    private JSONArray JArray;
    private String URL ,country, date;
    private String downloadType ,param;

    public DownloadInfo_task(Context ctx ,String downloadType,String param){
        this.ctx = ctx;
        this.listener = (MyListener) ctx;
        this.downloadType = downloadType;
        this.param = param;

    }

    public DownloadInfo_task(Context ctx ,String downloadType,String country, String date){
        this.ctx = ctx;
        this.listener = (MyListener) ctx;
        this.downloadType = downloadType;
        this.country = country;
        this.date = date;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (Utils.isNetworkAvailable2(ctx)) {

            if (downloadType.equals(INFO_BY_COUNTRY))
                URL  = BASE_URL + INFO_BY_COUNTRY + param;
            else if (downloadType.equals(INFO_BY_DATE))
                URL  = BASE_URL + INFO_BY_DATE + param;
            else if (downloadType.equals(INFO_LATEST_GLOBAL_DATA))
                URL  = BASE_URL + INFO_LATEST_GLOBAL_DATA ;
            else if (downloadType.equals(INFO_GET_ALL_COUNTRIES))
                 URL  = BASE_URL + INFO_GET_ALL_COUNTRIES ;
            else if (downloadType.equals(INFO_BY_COUNTRY_AND_DATE))
                URL  = BASE_URL + INFO_BY_COUNTRY_AND_DATE + country + "&date=" + date ;
            //country=countryName&date=dateString
            else
                cancel(true);

          //  Log.e("url2", URL);

        }
        else {
            cancel(true);
            Toast.makeText(ctx, R.string.check_internet_access, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected String doInBackground(String... params) {


        final OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(URL.replace(" " , "%20"))
                .get()
                .build();
        String data = null;
        try {

            Response response = client.newCall(request).execute();
            data = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();

        }


        return data;
    }

    @Override
    protected void onPostExecute(String result) {

        try {
            if (result != null) {


                if (downloadType.equals(INFO_GET_ALL_COUNTRIES)) {

                    JSONArray temp = new JSONArray(result);
                    String[] array = temp.join(",").replace(", "," ").replace("\"","").split(",");
                    listener.hereIsYourCountries(array);
                } else {

                    JSONArray jsonArray = new JSONArray(result);
                    listener.hereIsYourData(jsonArray);
                }

            } else {
                listener.hereIsYourData(null);
                Toast.makeText(ctx, R.string.no_data, Toast.LENGTH_SHORT).show();

            }

        }
        catch (Exception e){

        }
    }
}
