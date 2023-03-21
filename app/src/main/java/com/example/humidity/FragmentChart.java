package com.example.humidity;

import static android.graphics.Color.parseColor;

import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentChart extends Fragment {

    //선 그래프
    private LineChart lineChart;
    private final String TAG = this.getClass().getSimpleName();
    private MyDialog myDialog;
    private ArrayList<Entry> entry_chart1;
    ArrayList<String> xVals = new ArrayList<String>(); // X 축 이름 값
    ArrayList<Entry> humidity_Entry = new ArrayList<>(); //Y 축 값
    ArrayList<Entry> relativeHumidity_Entry= new ArrayList<>();
    ArrayList<Entry> temperature_Entry= new ArrayList<>();

    private LineData chartData; // 데이터를 가지고 그래프를 그리는 데 필요한 모든 정보를 담고 있는 객체입
    private LineDataSet lineDataSet1; // 관련된 데이터를을 담고 있는 객체 ex) 상대습도
    private long mNow;
    private View view;
    private ImageButton calendar,relative_humidity,wet,temp;
    private TextView date,textView;
    private ImageView sparkle1,sparkle2,sparkle3;
    DatePickerDialog datePicker;
    private Integer setData=0;

    public static FragmentChart newInstance() {

        FragmentChart fragmentChart = new FragmentChart();
        return fragmentChart;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chart, container, false);
        chartData = new LineData(); // 차트에 담길 데이터
        lineChart = (LineChart) view.findViewById(R.id.chart);
        calendar =  view.findViewById(R.id.calendar);
        relative_humidity =  view.findViewById(R.id.relative_humidity);
        wet =  view.findViewById(R.id.wet);
        temp =  view.findViewById(R.id.temp);
        sparkle1 =  view.findViewById(R.id.sparkle1);
        sparkle2 =  view.findViewById(R.id.sparkle2);
        sparkle3 =  view.findViewById(R.id.sparkle3);

        date =  view.findViewById(R.id.date);
        textView =  view.findViewById(R.id.textView);


        myDialog =new MyDialog(getContext());
        xVals.clear(); // x축 배열 모든 값 제거
        relativeHumidity_Entry.clear(); // y 축 배열 모든 값 제거
        humidity_Entry.clear(); // y 축 배열 모든 값 제거
        temperature_Entry.clear(); // y 축 배열 모든 값 제거


        mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = mFormat.format(mDate);
        Log.e(TAG, "현재 날짜" + nowDate);
        date.setText(nowDate); // 현재 날짜 세팅
        setRelativeHumidity(nowDate,0);
        lineChart.animateXY(1000,2000); // 랜더링 애니메이션 설정 - 인자 x,y 값 그리는 랜더링 속도 설정

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xVals.clear(); // x축 배열 모든 값 제거
                relativeHumidity_Entry.clear(); // y 축 배열 모든 값 제거
                humidity_Entry.clear(); // y 축 배열 모든 값 제거
                temperature_Entry.clear(); // y 축 배열 모든 값 제거

                Calendar calendar = Calendar.getInstance();
                //getInstance 는 현재 날짜를 선택값의 기본값으로 사용
                int pYear = calendar.get(Calendar.YEAR);
                int pMonth = calendar.get(Calendar.MONTH);
                int pDay = calendar.get(Calendar.DAY_OF_MONTH);
                //사용작 달력해서 선택한 날짜를 변수에 저장 해주는 듯

                datePicker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            // 사용자가 날짜 선택을 끝냈음을 인식하는 리스너 함수
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                // 사용자가 날짜 선택을 끝내고 완료버튼을 눌렀을 때 실행되는 함수
                                // 사용자가 선택한 날짜 연, 달, 일이 이 함수의 인자로 넘어 간다.
                                month = month +1;
                                String pickDate = year + "-" + month + "-" + day;
                                setRelativeHumidity(pickDate,setData);
                                date.setText(pickDate);
                                //선택한 날짜를 et에 세팅해주는 과정
                            }
                        }, pYear,pMonth,pDay);
                //다이얼로그의 인자 값으로 context, 날짜 선택을 끝냈음을 인식한 리스너 함수 구현체,
                //달력 다이얼로그를 띄울 때 표시될 날짜들을 인자로 넘겨준다.


                datePicker.show();
            }
        });

        relative_humidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLineChart(0);
                sparkle1.setVisibility(View.VISIBLE);
                sparkle2.setVisibility(View.INVISIBLE);
                sparkle3.setVisibility(View.INVISIBLE);
                textView.setText("relative-humidity");
                textView.setTextColor(parseColor("#9BC3FF"));
                setData=0;

            }
        });

        wet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLineChart(1);
                sparkle1.setVisibility(View.INVISIBLE);
                sparkle2.setVisibility(View.VISIBLE);
                sparkle3.setVisibility(View.INVISIBLE);
                textView.setText("humidity");
                textView.setTextColor(parseColor("#648CFF"));
                setData=1;

            }
        });

        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLineChart(2);
                sparkle1.setVisibility(View.INVISIBLE);
                sparkle2.setVisibility(View.INVISIBLE);
                sparkle3.setVisibility(View.VISIBLE);
                textView.setText("temperature");
                textView.setTextColor(parseColor("#FF1493"));
                setData=2;

            }
        });
        return view;
    }


    public void setRelativeHumidity(String specific_date,Integer setWhat) {
        Call<DTOHDT> call = NetWorkHelper.getInstance().getApiService().
                setRelativeHumidity(specific_date);
        call.enqueue(new Callback<DTOHDT>() {
            @Override
            public void onResponse(Call<DTOHDT> call, Response<DTOHDT> response) {

                if (response.isSuccessful() && response.body() != null) {
//                    Log.e(TAG, "서버에서 받아온 값 형태 " + response + response.body());
                    DTOHDT DTOGroupResponse = response.body();
//                    Log.e(TAG, "응답 성공 후 dto 파싱해준 값1" + DTOGroupResponse);

                    if (DTOGroupResponse.getStatus().equals("true")) {
                        List<DTOHDT.DHTDATA> items = DTOGroupResponse.getData();
//                        Log.e(TAG, "응답 성공 후 dto 파싱해준 값2" + items);

                        setDataList(items,setWhat);

                    } else {
                        myDialog.CheckDialog("오류가 발생하였습니다 다시 시도해주세요");
                    }
                } else {
                    Log.e(TAG, "실패" + response.body());
                }

            }

            @Override
            public void onFailure(Call<DTOHDT> call, Throwable t) {
                Log.e(TAG, "응답에러 = " + t.getMessage());
            }
        });

    }



    private void setDataList(List<DTOHDT.DHTDATA> items,Integer setWhat) {
        Log.e(TAG, "setDataList 역할 :  서버에서 받아온 상대습도 및 시간 데이터를 리스트에 저장해줌" );
//        Log.e(TAG, "setDataList 받은 인자값" + items);
        for (int i = 0; i < items.size(); i++) {
            String time = items.get(i).getDetail_date();
            int relative_humidity = items.get(i).getRelative_humidity();
            int humidity = items.get(i).getHumidity();
            int temperature = items.get(i).getTemperature();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 서버에서 가져온 String 값과 같은 형태로 설정해줌
            Date dt = new Date();
            try {
                dt = simpleDateFormat.parse(time); // String 형태를 date 로 파싱해주는 함수 인자 : String 타입의 날짜 return Date
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String timeFormat = sdf.format(dt); //Date 형태를 String 형식으로 변환
            xVals.add(timeFormat);
            Log.e(TAG, "setDataList : timeFormat 값 : " +i + timeFormat);
            relativeHumidity_Entry.add(new Entry(i,relative_humidity));
            Log.e(TAG, "setDataList : relative_humidity 값 : " + relative_humidity);
            humidity_Entry.add(new Entry(i,humidity));
            Log.e(TAG, "setDataList : humidity 값 : " + humidity);
            temperature_Entry.add(new Entry(i,temperature));
            Log.e(TAG, "setDataList : temperature 값 : " + temperature);
            Log.d(TAG, "----------------------------");

        }

        Log.e(TAG, "setDataList : xVals 배열 값 : "+ xVals);
        Log.e(TAG, "setDataList : xVals 크기 : "+ xVals.size());
        Log.e(TAG, "setDataList : humidity_Entry 배열 값  : " + humidity_Entry);
        Log.e(TAG, "setDataList : humidity_Entry 크기 : "+ humidity_Entry.size());
        Log.e(TAG, "setDataList : temperature_Entry 배열 값 : " + temperature_Entry);
        Log.e(TAG, "setDataList : temperature_Entry 크기 : " + temperature_Entry.size());


        setLineChart(setWhat);
    }


    public void setLineChart(Integer setWhat){

        if(setWhat==0){ //상대습도
            Log.e(TAG, "setLineChart 상대습도" );
            chartData.clearValues();
            lineDataSet1 = new LineDataSet(relativeHumidity_Entry, "상대 습도"); // 데이터가 담긴 Arraylist 를 LineDataSet 으로 변환한다.
            lineDataSet1.setColor(parseColor("#9BC3FF")); // 해당 LineDataSet의 색 설정 :: 각 Line 과 관련된 세팅은 여기서 설정한다.
            lineDataSet1.setDrawFilled(true);
            lineDataSet1.setFillColor(parseColor("#9BC3FF"));
        }

        else if(setWhat==1){
            Log.e(TAG, "setLineChart 습도" );
            chartData.clearValues();
            lineDataSet1 = new LineDataSet(humidity_Entry, " 습도"); // 데이터가 담긴 Arraylist 를 LineDataSet 으로 변환한다.
            lineDataSet1.setColor(parseColor("#648CFF")); // 해당 LineDataSet의 색 설정 :: 각 Line 과 관련된 세팅은 여기서 설정한다.
            lineDataSet1.setDrawFilled(true);
            lineDataSet1.setFillColor(parseColor("#5ABEFF"));
        }

        else if (setWhat==2){
            Log.e(TAG, "setLineChart 온도" );
            chartData.clearValues();
            lineDataSet1 = new LineDataSet(temperature_Entry, "온도"); // 이터가 담긴 Arraylist 를 LineDataSet 으로 변환한다.
            lineDataSet1.setColor(parseColor("#FF1493")); // 해당 LineDataSet의 색 설정 :: 각 Line 과 관련된 세팅은 여기서 설정한다.
            lineDataSet1.setDrawFilled(true);
            lineDataSet1.setFillColor(parseColor("#FFA6C5"));
        }

            chartData.addDataSet(lineDataSet1);
            lineChart.fitScreen();//reset 어쩌고.. 데이터에 새로운 값을 넣은 뒤에 꼭 해줘야함
            lineChart.setData(chartData);



        lineChart.setScaleEnabled(false); // 확대, 축소를 가능하게 하려면 true로 설정
        lineChart.setPinchZoom(false); //zoom 기능
        lineChart.setScrollContainer(true);; //스크롤 가능하게
        lineChart.setVisibleXRangeMaximum(5); //가로 스크롤 생김 + 스크롤 넘어가기전 표출되는 데이터 값
        lineChart.setTouchEnabled(true); // 차트 터치 disable

        XAxis xAxis = lineChart.getXAxis(); // XAxis : x축
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // x축 아래쪽
        xAxis.setTextSize(13f); // x축에 표출되는 텍스트의 크기
        xAxis.setDrawGridLines(false); //x축의 그리드 라인을 없앰
        xAxis.setLabelCount(5); //x축의 데이터를 최대 몇 개 까지 나타낼지에 대한 설정


        //y 축 왼쪽 설정
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setGridColor(parseColor("#9BC3FF"));
        YAxis rightAxis = lineChart.getAxisRight();;
        rightAxis.setEnabled(false); // y축에 오른쪽 제한선 없앰
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();


    }





}