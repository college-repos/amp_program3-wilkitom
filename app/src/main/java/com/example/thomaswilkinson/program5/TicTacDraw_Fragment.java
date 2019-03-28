package com.example.thomaswilkinson.program5;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class TicTacDraw_Fragment extends Fragment {
    DrawView dv;
    Button button1, button2;
    public TicTacDraw_Fragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View myView = inflater.inflate(R.layout.tictacdraw_fragment, container, false);
        dv = myView.findViewById(R.id.dv1);
        button1 = (Button) myView.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);

                dv.startAdvertise();
            }
        });
        button2 = (Button) myView.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);

                dv.startDiscovery();
            }
        });
        return myView;
    }

}
