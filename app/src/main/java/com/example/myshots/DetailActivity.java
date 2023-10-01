package com.example.myshots;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public
class DetailActivity extends AppCompatActivity {

    private PhotoView descImg;
    private TextView header, desc, stamp;

    @Override
    protected
    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setTitle("Details of Event");

        descImg = findViewById(R.id.descImg);
        header = findViewById(R.id.descHeader);
        desc = findViewById(R.id.descDesc);
        stamp = findViewById(R.id.timestamp);

        try {
            Glide.with(this).load(getIntent().getStringExtra("image")).placeholder(R.drawable.ic_image).into(descImg);
        }catch (Exception e){
            e.printStackTrace();
        }

//        String timestamp = String.valueOf(getIntent().getStringExtra("timing"));
//        Date date=new Date(Long.parseLong(timestamp)*1000);
//        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//        sfd.format(date);

        header.setText(getIntent().getStringExtra("nam"));
        desc.setText(getIntent().getStringExtra("not"));
//        stamp.setText(sfd.format(date));

        desc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public
            boolean onLongClick(View view) {
                String nm = header.getText().toString();
                String nt = desc.getText().toString();
                AlertDialog.Builder alert = new AlertDialog.Builder(DetailActivity.this);
                alert.setTitle(nm);
                alert.setMessage(nt);
                alert.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public
                    void onClick(DialogInterface dialogInterface, int i) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.setType("text/plain");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Event Name: " +nm +" \n\n" + "About: " +nt);
                        startActivity(sendIntent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public
                    void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

                return true;
            }

        });
    }

    @Override
    public
    boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public
    boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.shareEvent) {
            Toasty.info(getApplicationContext(), "Sharing details...", Toasty.LENGTH_SHORT, true).show();
            String nm = header.getText().toString();
            String nt = desc.getText().toString();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Event Name: " +nm +" \n\n" + "ABOUT: " +nt);
            startActivity(sendIntent);
            return true;
        }
        if (id == R.id.uploadNew) {
            Intent intent = new Intent(DetailActivity.this, UploadActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public
    void onBackPressed() {
        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}