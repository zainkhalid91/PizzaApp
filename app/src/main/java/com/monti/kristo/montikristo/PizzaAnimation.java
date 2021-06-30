package com.monti.kristo.montikristo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class PizzaAnimation extends AppCompatActivity {
    Button continueButton;
    TextView textView;

    @Override
    protected void onStart() {
        overridePendingTransition(0, 0);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_animation);
        //Crashlytics
        Fabric.with(this, new Crashlytics());


        continueButton = findViewById(R.id.continuebtn);
        textView = findViewById(R.id.textView);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        textView.startAnimation(myanim);

        Animation btnanim = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow);
        continueButton.startAnimation(btnanim);
        continueButton.animate().setStartDelay(500);


      /*  LottieAnimationView animationView = findViewById(R.id.lottieanimation_pizza);

        LottieDrawable drawable = new LottieDrawable();
        LottieComposition.Factory.fromAssetFileName(getApplicationContext(), "pizza.json",(composition ->
        {
            drawable.setComposition(composition);
            drawable.playAnimation();
            drawable.setScale(6);
            animationView.setImageDrawable(drawable);
        }));*/

        continueButton = findViewById(R.id.continuebtn);
        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MyCartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
