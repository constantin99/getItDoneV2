package com.example.getitdonev2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.GregorianCalendar;

public class HomeFragment extends Fragment  {
    private View view1;
    private GregorianCalendar calendar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view1 =  inflater.inflate(R.layout.fragment_home, container, false);

        CalendarView v = view1.findViewById(R.id.calendarView);
        v.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar = new GregorianCalendar(year, month, dayOfMonth);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if(auth.getCurrentUser() == null){
                    Toast.makeText(getActivity(), "Nu sunteti logat", Toast.LENGTH_SHORT).show();
                    return;
                }
                //setting the date as string
                String stringDate =(String) DateFormat.format("yyyy-MM-dd HH:mm:ss", calendar.getTime());
                //passData(stringDate);
                Intent intent = new Intent(getActivity(),AddTask.class);
                intent.putExtra("date", stringDate);
                startActivity(intent);

            }
        });
        return view1;

    }

}
