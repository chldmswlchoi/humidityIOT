package com.example.humidity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;


public class FragmentHome extends Fragment {

    private View view;
    private final String TAG = this.getClass().getSimpleName();
    private TextView humidity, isConnected, temperature;
    private ImageButton onButton;
    private Socket socket;
    private MyDialog myDialog;
    private Handler dialogHandler, setDhtHandler,setOnOffButtonHandler;
    private ProgressDialog customProgressDialog;
    private String humidityFromServer, temperatureFramServer;
    private PrintWriter writer;
    private BufferedReader reader;
    private Integer sendWhat,OnOff;
    private SendSocket sendSocket;

    public static FragmentHome newInstance() {
        FragmentHome fragmentHome = new FragmentHome();
        return fragmentHome;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d(TAG, "홈 액티비티 진입");

        humidity = view.findViewById(R.id.humidity);
        isConnected = view.findViewById(R.id.isConnected);
        temperature = view.findViewById(R.id.temperature);
        onButton = view.findViewById(R.id.onButton);
        isConnected = view.findViewById(R.id.isConnected);
        myDialog = new MyDialog(getContext());

        dialogHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {

                if (msg.what == 0) {
                    myDialog.CheckDialog("온습도 센서 전원을 켜주세요");
                }
            }
        };

        setDhtHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0) { //서버에서 받아온 온습도 값 텍스트 뷰에 세팅
                    humidity.setText(humidityFromServer);
                    temperature.setText(temperatureFramServer + "℃");

                }
            }
        };

        setOnOffButtonHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0) { //서버에서 받아온 온습도 값 텍스트 뷰에 세팅 //off상태
                    onButton.setBackgroundResource(R.drawable.on);
                    isConnected.setText("disconnected");
                } else if (msg.what == 1) { //서버에서 받아온 온습도 값 텍스트 뷰에 세팅 //on상태
                    onButton.setBackgroundResource(R.drawable.off);
                    isConnected.setText("connected");

                }
            }
        };

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSocket = new SendSocket();
                if (OnOff == 0) { //off->on
                    sendWhat = 0;
                    onButton.setBackgroundResource(R.drawable.off);
                    Toast.makeText(getContext(), "ON", Toast.LENGTH_SHORT).show();
                    OnOff = 1; // OnOff 상태 알려주는 변수 값 1로 변경
                    isConnected.setText("connected");

                } else { //on->off
                    sendWhat = 2;
                    onButton.setBackgroundResource(R.drawable.on);
                    Toast.makeText(getContext(), "OFF", Toast.LENGTH_SHORT).show();
                    OnOff = 0;// OnOff 상태 알려주는 변수 값 0으로 변경
                    isConnected.setText("disconnected");

                }
                sendSocket.start();
            }
        });

        SocketStart socketStart = new SocketStart();
        socketStart.start();
        Log.e(TAG, "스레드 실행 끝?");

        //로딩창 객체 생성
        customProgressDialog = new ProgressDialog(getContext());
        customProgressDialog.setCancelable(false); // 로딩창 주변 클릭 시 종료 막기
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customProgressDialog.show();
//        setHomeData();

        return view;
    }



    class SocketStart extends Thread {
        public SocketStart (){}
        public void run(){
            try {
                Log.e(TAG,"스레드 실행");
//                socket = new Socket("192.168.0.12",12345); // 집
                socket = new Socket();
//                socket.connect(new InetSocketAddress("192.168.0.145",12345),10000); // 2학원
//                socket.connect(new InetSocketAddress("192.168.0.3",12345),10000); // 3학원
//                socket.connect(new InetSocketAddress("192.168.0.3",12345),10000); // 집
                socket.connect(new InetSocketAddress("192.168.0.10",12345),10000); // 2학원
//                socket.connect(new InetSocketAddress("192.168.0.4",12345),10000); // 2학원


                Log.d(TAG, "서버와 소켓 연결 완료");
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String isOnOff = reader.readLine();
                Log.d(TAG,isOnOff);
                if(isOnOff.equals("False"))
                {
                    setOnOffButtonHandler.sendEmptyMessage(0);
                     OnOff =0;
                }
                else {
                    setOnOffButtonHandler.sendEmptyMessage(1);
                    OnOff =1;

                }

                writer = new PrintWriter(socket.getOutputStream(),true);
                writer.print("Hello");
                writer.flush();
                String str = reader.readLine(); //개행문자열이 포함된 메시지가 와야만 다음 문장이 실행된다.
                Log.d(TAG,str);
                String[] splitText = str.split("/");
                humidityFromServer = splitText[0];
                temperatureFramServer =splitText[1];
                setDhtHandler.sendEmptyMessage(0);
                customProgressDialog.dismiss();

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,"에러");
                Log.e(TAG,e.toString());
                Log.e(TAG, String.valueOf(e.getCause()));
                Log.e(TAG, e.getMessage());
                customProgressDialog.dismiss(); // 로딩 다이얼로그 종료
                dialogHandler.sendEmptyMessage(0); // 라즈베리파이 전원을 키라는 안내문구 띄우기

            }
        }
    }

    class SendSocket extends Thread{
        public void run(){
            try{
                if(sendWhat == 0){
                    writer.print("ON");
                    writer.flush();
                    Log.d(TAG,"라즈베리 서버에 ON 메시지 보냄");
                }

                else if (sendWhat ==1){
                    writer.print("quit");
                    socket.close();
                    Log.d(TAG,"라즈베리 서버에 ON 메시지 보냄");

                }
                else if (sendWhat ==2){
                    writer.print("OFF");
                    writer.flush();
                    Log.d(TAG,"라즈베리 서버에 OFF 메시지 보냄");
                }

            }catch (Exception e){

            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");

        super.onDestroy();


    }


}