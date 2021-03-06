package kr.co.isaveyou.isaveyou.main;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import android.widget.FrameLayout;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.co.isaveyou.isaveyou.R;

//스트리밍을 위한 Fragment
public class MonitoringFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "MonitoringFragment";
    WebView wv_monitoring;

    Button btn_stop, btn_start;

    String streamingServer_access, streamingServer_url;
    HttpURLConnection conn;
    ConstraintLayout streaming_ConstraintLayout;


    String result;
    View.OnClickListener handler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_start:
                    try {
                        Toast.makeText(getContext(),"로딩 중, 잠시만 기다려주세요.",Toast.LENGTH_SHORT).show();
                        wv_monitoring.loadUrl(streamingServer_url);

                        Log.v(TAG,"영상 재생 시작");
                        Log.v(TAG,"재생 버튼 누름");

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_stop:
                    Toast.makeText(getContext(),"뒤로가기를 누르시면 영상이 종료됩니다.",Toast.LENGTH_LONG).show();
                    StopTask stopTask = new StopTask();
                    stopTask.execute();
                    Log.v(TAG,"영상 재생 멈춤");
                    Log.v(TAG,"멈춤 버튼 누름");

                    MonitoringFragment.this.getFragmentManager().beginTransaction().detach(   MonitoringFragment.this  ) ;

                    ((MainActivity)getActivity()).Invisible_Streaming();


                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        Log.v(TAG,bundle+"");
        if(bundle != null) {
            streamingServer_access = bundle.getString("streamingServer_access");
            streamingServer_url = bundle.getString("streamingServer_url");
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoring,null);

        streaming_ConstraintLayout = (ConstraintLayout)view.findViewById(R.id.streaming_ConstraintLayout);
        wv_monitoring = (WebView)view.findViewById(R.id.wv_monitoring);
        btn_start = view.findViewById(R.id.btn_start);
        btn_stop = view.findViewById(R.id.btn_stop);

        btn_start.bringToFront();
        btn_stop.bringToFront();

        wv_monitoring.getSettings().setJavaScriptEnabled(true);
        wv_monitoring.setWebChromeClient(new WebChromeClient());
        wv_monitoring.setWebViewClient(new WebViewClient());
        wv_monitoring.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //필터
                if(event.getAction()!=KeyEvent.ACTION_DOWN){
                    return true;
                }
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    if(wv_monitoring.canGoBack()){
                        wv_monitoring.goBack();
                        Log.v(TAG,"뒤로 가기 눌림");
                    } else {
                        Log.v(TAG,"뒤로 갈 수 없음");
                        ((MainActivity)getActivity()).onBackPressed();
                    }
                    return true;
                }
                return false;
            }
        });
        btn_stop.setOnClickListener(handler);
        btn_start.setOnClickListener(handler);

        Log.v(TAG,"영상 준비 작업 시작");
        Log.v(TAG, getArguments()+"");

        Log.v(TAG, "bundle 값 : streamingSever_access :"+ streamingServer_access + ", streamingServer_url :" + streamingServer_url);

        StartTask starttask = new StartTask();
        starttask.execute();


        return view;
    }
    public void loadUrl(String url){
        wv_monitoring.loadUrl(url);
    }



    class StopTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                //서버 접속

                URL url = new URL("http://192.168.0.13:5001/stream/0/");
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-type","application/xml");
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                if(conn.getResponseCode()!=HttpURLConnection.HTTP_OK){

                } else {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    StringBuffer buffer = new StringBuffer();
                    while ((line = reader.readLine())!=null){
                        buffer.append(line + "\n");
                    }
                    result = buffer.toString();
                    Log.v(TAG, "buffer result : " + line );
                    reader.close();
                }
                Log.v(TAG, "url : " + url );


            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.v(TAG, "전달받음");

            super.onPostExecute(s);
        }
    }

    class StartTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            try {
                //스트리밍 서버 허가를 주는 주소에 접속하여 허가 얻음

                URL url = new URL(streamingServer_access);
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-type","application/xml");
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                Log.v(TAG,"streamingServer_access : " + streamingServer_access);


                if(conn.getResponseCode()!=HttpURLConnection.HTTP_OK){

                } else {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    StringBuffer buffer = new StringBuffer();
                    while ((line = reader.readLine())!=null){
                        buffer.append(line + "\n");
                    }
                    result = buffer.toString();
                    Log.v(TAG, "buffer result : " + line );
                    reader.close();
                }
                Log.v(TAG, "url : " + url );


            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.v(TAG, "서버access 완료");
            Log.v(TAG, "result : " + result);

            super.onPostExecute(s);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }
    public void destroyStremingFragment(){
        streaming_ConstraintLayout.setVisibility(View.INVISIBLE);
    }
}
