package com.tistory.dayglo.smarting_android;

import android.content.DialogInterface;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "zzanzu";

    ExpandableRelativeLayout doorbellLayout;
    ImageView visitorImage;

    private static final String temperatureUrl = "http://smarts.asuscomm.com:1005/temperature";
    private static final String openDoorUrl = "http://smarts.asuscomm.com:1005/led";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_main);

        doorbellLayout = (ExpandableRelativeLayout) findViewById(R.id.doorbell_layout);

        visitorImage = (ImageView) findViewById(R.id.visitor_photo);

        requestData(temperatureUrl, callbackAfterGettingTemperature);
        requestData(openDoorUrl, callbackAfterOpeningDoor);
    }

    public void onClickDoorbell(View view) {
        doorbellLayout.toggle();
    }

    public void onClickTemperature(View view) {
        requestData(temperatureUrl, callbackAfterGettingTemperature);
    }


    Call openDoor(String httpUrl, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(httpUrl)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private void requestData(String httpUrl, Callback callBack) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(httpUrl).newBuilder();
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callBack);
    }

    // temperature 콜백
    public Callback callbackAfterGettingTemperature = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onFailure: callback fail");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                final String responseData = response.body().string();
                Log.d("temperature", responseData);

                // Run view-related code back on the main thread
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tempTv = findViewById(R.id.temperature_textview);
                        TextView timeTv = findViewById(R.id.temperature_time_textview);

                        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
                        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", java.util.Locale.getDefault());

                        try {
                            Date originalDate = originalFormat.parse(getJsonData(responseData, "time"));
                            String newDate = newFormat.format(originalDate);

                            tempTv.setText(getJsonData(responseData, "tem"));
                            timeTv.setText(newDate);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    };

    // door opening 콜백
    public Callback callbackAfterOpeningDoor = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d(TAG, "onResponse: door opening button pressed");
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                final String responseData = response.body().string();

                // Run view-related code back on the main thread
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.visitor_textivew);
                        tv.setText(getJsonData(responseData, "data"));
                    }
                });
            }
        }
    };

    public void onClickVisitorImage(View view) {
        final WebViewDialog webViewDialog = new WebViewDialog(this);

        webViewDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

            }
        });

        webViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        webViewDialog.show();
    }

    // door opening button
    public void onClickOpen(View view) throws IOException {
        openDoor(openDoorUrl, callbackAfterOpeningDoor);
    }

    // JSON parsing
    public String getJsonData(String jsonData, String jsonItem) {
        String resultData = null;

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            resultData = jsonObject.getString(jsonItem);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return resultData;
    }
}
