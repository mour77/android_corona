package fire.fire.cv.DiffUtil;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import fire.fire.cv.DownloadInfo_task;
import fire.fire.cv.MainActivity;
import fire.fire.cv.R;
import fire.fire.cv.databinding.CustomSearchDialogBinding;


public class CustomSearchDialog extends DialogFragment{


    private CustomSearchDialogBinding bd;
    private MainActivity act;
    private String [] countries;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        bd = CustomSearchDialogBinding.inflate(inflater, container, false);
        View view = bd.getRoot();

        Bundle bundle = getArguments();
        if (bundle != null)
             countries = bundle.getStringArray("countriesList");

        act = (MainActivity) getActivity();
        setDateTV();
        setSpinnerCountries();
        buttonsListeners();
        return view;

    }




    private void setSpinnerCountries(){

       ArrayAdapter adapterSP = new ArrayAdapter(act, R.layout.spinner_layout2, countries);

        adapterSP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bd.countriesSP.setAdapter(adapterSP);



    }


    private void buttonsListeners(){

        bd.okBT.setOnClickListener(view -> {

            String country = bd.countriesSP.getSelectedItem().toString();
            String date = bd.dateTV.getText().toString();

            if (!country.equals(countries[0]) && !date.equals(""))
                act.downloadData(DownloadInfo_task.INFO_BY_COUNTRY_AND_DATE,country,date);
            //SEARCH BY COUNTRY
            else if (country.equals(countries[0]) && !date.equals(""))
                act.downloadData(DownloadInfo_task.INFO_BY_DATE,date);
            //SEARCH BY DATE
            else if (!country.equals(countries[0]) && date.equals(""))
                act.downloadData(DownloadInfo_task.INFO_BY_COUNTRY,country);


            dismiss();
        });

        bd.cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }





    private void setDateTV(){

        final Calendar myCalendar = Calendar.getInstance();


        final DatePickerDialog.OnDateSetListener date12 = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "MM-dd-yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            bd.dateTV.setText(sdf.format(myCalendar.getTime()));

        };

        bd.dateTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(act, date12, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }





    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }


    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
