package com.example.guoweb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnClickInMyAdapterListener{

    private static final String MY_PREFS_NAME = "MyPrefsFile";
    private static final String TEXT_ONE = "text_one_key";
    ProgressBar progressBar;
    WebView webView;
    EditText etUrl;
    String myCurrentUrl;
    ListViewCustomAdaptorBM listAdapterBM;
    List<History> bkmkset = new ArrayList<>();
    List<History> histList1 = new ArrayList<>();
    private List<History> histList;
    private List<History> bookmkSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // if the bundle isn't null, retrieve saved information
        if (savedInstanceState != null) {
            // get message out using the key
            String message = savedInstanceState.getString("msg");
            // make a toast to show the message
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            // update the textview text with saved message
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(message);
        }

        histList = loadSharedPreferencesHistList();
        bookmkSet = loadSharedPreferencesbookmkSet();
        pagesetup();
        webView.loadUrl("https://www.google.com");
    }

    private void pagesetup() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        etUrl = findViewById(R.id.edtxturl1);
        webView = findViewById(R.id.webview1);
        progressBar = findViewById(R.id.progressBar);

        // Create a WebSettings object
        WebSettings webSettings = webView.getSettings();
        // Set Zoom Controls
        webSettings.setBuiltInZoomControls(true);

        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                myCurrentUrl = url;
                histList.add(new History(webView.getTitle().toString(), myCurrentUrl));
        }

    });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress((newProgress));
                if (newProgress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                if (newProgress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                } else {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
            }
        });


        ImageButton gobtn = (ImageButton) findViewById(R.id.go_btn1);
        gobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from EditText
                myCurrentUrl = etUrl.getText().toString().trim();
                // If entered url doesn't start "http://" or "https://" we need to append it
                // before the url.
                if (!myCurrentUrl.startsWith("http://") || !myCurrentUrl.startsWith("https://")) {
                    myCurrentUrl = "https://" + myCurrentUrl;
                }
                // To load the webView with the url we need to call loadUrl() method
                // of the WebView class, passing url as parameter.
                webView.loadUrl(myCurrentUrl);
            }
        });

        //move backward
        ImageButton bckbtn = (ImageButton) findViewById(R.id.back_btn1);
        bckbtn.setOnClickListener((View v) -> {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        });
         //move forward
        ImageButton fwdbtn = (ImageButton) findViewById(R.id.fwd_btn1);
        fwdbtn.setOnClickListener((View v) -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });

        //share link
        ImageButton shareBtn= (ImageButton) findViewById(R.id.share_btn1);

        shareBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                intent.setType("text/plain");
                startActivity(intent);
            }
        });


        //show homepage
        ImageButton homebtn = (ImageButton) findViewById(R.id.home_btn1);
        homebtn.setOnClickListener((View v) -> {
            webView.loadUrl("https://www.google.com");
        });

        //refresh the page
        ImageButton refreshbtn = (ImageButton) findViewById(R.id.refresh_btn1);
        refreshbtn.setOnClickListener((View v) -> {
            webView.reload();
        });


        //add bookmark
        ImageButton addbkmk = (ImageButton) findViewById(R.id.addbkmk);
        addbkmk.setOnClickListener((View v) -> {
            Log.d("bk","book mark");
            String title = webView.getTitle();
            String current_url = webView.getUrl();
            boolean exists = false;
            for(History bkmks : bookmkSet){
                //Log.d("bk","book mark 2 "+title +" , "+bkmks.getTitle());
                if(bkmks.getTitle().equalsIgnoreCase(title)){
                    Log.d("bk","book mark 2 "+title +" , "+bkmks.getTitle());
                    Toast.makeText(this,title+" already exists in the List",Toast.LENGTH_LONG);
                    exists = true;
                    return;
                }
            }
            if(!exists) {
                Log.d("bk","book mark 3 "+ title);
                bookmkSet.add(new History(current_url, new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z").format(new Date(System.currentTimeMillis())), title));
            } });


        //history button click
        ImageButton histbtn = (ImageButton) findViewById(R.id.hist1);
        histbtn.setOnClickListener((View v) -> {
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            String current_url;
            if (histList.size() > 0) {
                current_url = (histList.get(histList.size() -1)).getUrl();
            } else {
                current_url = "https://google.com";
            }
            editor.putString("current_url", current_url);
            editor.apply();

            setContentView(R.layout.history_list);
            setupScreen();

            Button clrhist = findViewById(R.id.hisclr);
            clrhist.setOnClickListener((View v1) -> {
                MainActivity.this.clearHistory();
            });

            Button backbtn = findViewById(R.id.backbtn);
            backbtn.setOnClickListener((View v2) -> MainActivity.this.historyBack());

        });


        //chack bookmark
        ImageButton bokmkbtn = findViewById(R.id.bookmk1);
        bokmkbtn.setOnClickListener((View v) -> {
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            String current_url = (histList.get(histList.size()-1)).getUrl();
            editor.putString("current_url", current_url);
            editor.apply();
            setContentView(R.layout.bookmark_list);
            setupScreen2();

            Button hmbtn = findViewById(R.id.homebtn);
            hmbtn.setOnClickListener((View v2) -> {
                MainActivity.this.showHome();
            });

            Button backbtn = findViewById(R.id.backbtn);
            backbtn.setOnClickListener((View v2) -> MainActivity.this.historyBack());
        });



    }


    private void historyBack() {
        pagesetup();
        showPage(myCurrentUrl);
    }
    private void showHome() {
        pagesetup();
        showPage("https://google.com");
    }

    private void clearHistory() {
        histList.clear();
        setupScreen();
    }
    private void showPage(String s) {
        Log.d("his", "step21");

        WebViewClient wvc = new WebViewClient();
        webView.setWebViewClient(wvc);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(s);
        int time = (int) (System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());

        Log.d("history", "step21 " + formatter.format(date));
        History history = new History(webView.getUrl(), formatter.format(date));
        histList.add(history);
        etUrl  = findViewById(R.id.edtxturl1);
        etUrl .setText(webView.getUrl());
    }




    public void setupScreen() {

        ListView list = (ListView) findViewById(R.id.history_Listview);
        setTitle(R.string.test_url);

        ListViewCustomAdaptor listAdapter = new ListViewCustomAdaptor(com.example.guoweb.MainActivity.this, histList);


        list.setAdapter(listAdapter);
        Log.d("his", "step3");
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               // pagesetup();
                showPage((histList.get(position)).getUrl());
                Log.d("his", "step5");
                Log.d("his", getString(R.string.item_clicked) + " " + position + " " + (histList1.get(position)).getUrl());

            }
        });
    }

    public List<History> loadSharedPreferencesHistList() {

        SharedPreferences histPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
//        String json = null;
        if (histPrefs.contains("MyHistList")) {
            String json = histPrefs.getString("MyHistList", "default");
            if (!json.isEmpty()) {
                Type type = new TypeToken<ArrayList<History>>() {
                }.getType();
                histList1 = gson.fromJson(json, type);
            }
        }

        return histList1;
    }



    private List<History> loadSharedPreferencesbookmkSet() {
        SharedPreferences histPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
//        String json = null;
        if (histPrefs.contains("MyBookMarks")) {
            String json = histPrefs.getString("MyBookMarks", "default");
            if (!json.isEmpty()) {
                Type type = new TypeToken<ArrayList<History>>() {
                }.getType();
                bkmkset = gson.fromJson(json, type);
            }
        }
        return bkmkset;
    }

    @Override
    public void onremovebookmarkclicked(int i) {
        //  Log.d("his", "inMA to remove "+i);
        bookmkSet.remove(i);
        setupScreen2();
    }

    public void setupScreen2() {

        ListView list = (ListView) findViewById(R.id.bookmark_Listview);
        setTitle(R.string.test_url);

        listAdapterBM = new ListViewCustomAdaptorBM(MainActivity.this, bookmkSet, (OnClickInMyAdapterListener) this);

        list.setAdapter(listAdapterBM);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                pagesetup();
                showPage((bookmkSet.get(position)).getUrl());
                Log.d("his", "step5");
                Log.d("his", getString(R.string.item_clicked) + " " + position + " " + (bookmkSet.get(position)).getUrl());
            }
        });

    }
    @Override
    protected void onStart() {
        Log.d("ONSTART","Starting MainActivity");
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(histList);
        prefsEditor.putString("MyHistList", json);
        prefsEditor.apply();
        String json1 = gson.toJson(bookmkSet);
        prefsEditor.putString("MyBookMarks", json1);
        prefsEditor.commit();

        Log.d("his", "onstop");

    }

    @Override
    protected void onDestroy() {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(histList);
        prefsEditor.putString("MyHistList", json);
        prefsEditor.apply();
        String json1 = gson.toJson(bookmkSet);
        prefsEditor.putString("MyBookMarks", json1);
        prefsEditor.commit();
        Log.d("his", "ondestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("ONPAUSE","Pausing MainActivity");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("ONRESUME","Resuming MainActivity");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        TextView textView = (TextView) findViewById(R.id.textView);
        // retrieve the string value inside the textview
        String currentText = textView.getText().toString();

        // put the key and value into the Bundle object
        outState.putString("msg", currentText);
        super.onSaveInstanceState(outState);
    }


}
