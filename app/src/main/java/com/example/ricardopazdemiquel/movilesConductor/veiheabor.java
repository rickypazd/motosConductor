package com.example.ricardopazdemiquel.movilesConductor;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import utiles.BehaviorCuston;

public class veiheabor extends AppCompatActivity {
    private BottomSheetBehavior bottomSheetBehavior;
    private CoordinatorLayout main_content;
    private ScrollView scrollView;
    private boolean enter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veiheabor);
        main_content=findViewById(R.id.main_content);
        scrollView=findViewById(R.id.lista_veih);



        View view =findViewById(R.id.bottom_sheet);
        bottomSheetBehavior=BottomSheetBehavior.from(view);
        bottomSheetBehavior.setHideable(false);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                     if (bottomSheetBehavior instanceof BehaviorCuston) {
                    ((BehaviorCuston) bottomSheetBehavior).setLocked(true);
                        }
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    if (bottomSheetBehavior instanceof BehaviorCuston) {
                        ((BehaviorCuston) bottomSheetBehavior).setLocked(false);
                    }
                }

                return false;
            }
        });


    }
}
