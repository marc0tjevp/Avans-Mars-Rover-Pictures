package nl.marcovp.avans.nasaroverphotos.controller;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import nl.marcovp.avans.nasaroverphotos.R;

public class InfoActivity extends AppCompatActivity {

    Button devButton;
    Button coffeeButton;
    Button logoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        devButton = findViewById(R.id.button_dev);
        coffeeButton = findViewById(R.id.button_coffee);
        logoButton = findViewById(R.id.button_logo);

        devButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/marc0tjevp");
                Intent devIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(devIntent);
            }
        });

        coffeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://paypal.me/marcovp");
                Intent intentCoffee = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intentCoffee);
            }
        });

        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.flaticon.com/authors/eucalyp");
                Intent intentLogo = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intentLogo);
            }
        });

    }
}
