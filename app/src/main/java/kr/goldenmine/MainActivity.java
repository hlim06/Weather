package kr.goldenmine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView date;
    private TextView time;
    private Spinner spot;
    private TextView search;
//    private TextView result;
    private RecyclerView weatherList;
    private MyAdapter weatherAdapter;

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date.setText(getStringFromDate(year, month, dayOfMonth));
    }

    //    class MyOnDateSetListener implements DatePickerDialog.OnDateSetListener {
//        @Override
//        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//            date.setText(getStringFromDate(year, month, dayOfMonth));
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        spot = findViewById(R.id.spinner);
        search = findViewById(R.id.search);
//        result = findViewById(R.id.result);
        weatherList = findViewById(R.id.weather_list_view);

        weatherList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        weatherAdapter = new MyAdapter(getApplicationContext(), new ArrayList<>(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        weatherList.setAdapter(weatherAdapter);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) - 2);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 0~11
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

//        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                date.setText(getStringFromDate(year, month, dayOfMonth));
//            }
//        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time.setText(getStringFromTime(hourOfDay, minute));
            }
        }, hour, minute, true);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Servicekey = "oZ5gFy91vhqpbygHwmJdDcHRnW1mJHgo1r6HxjvhqDOM6dLOHM9pAgXWoPb/p/LY55L56Fxj6Hj8pB8j/o8NBQ==";
                int pageNo = 1;
                int numRows = 1000;
                String dataType = "XML";
                String BaseDate = date.getText().toString();
                String BaseTime = time.getText().toString();
                int nx = 60;
                int ny = 127;

                RetrofitFactory.getWeatherInstance().getWeather(Servicekey, pageNo, numRows, dataType, BaseDate, BaseTime, nx, ny).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            String body = response.body();
//                            result.setText(body);

                            Document doc = Jsoup.parse(body);

                            // 배열을 상속받은 새로운 클래스 (비유하자면)
                            Elements elements = doc.getElementsByTag("item");

//                            String text = "";

                            weatherAdapter.itemList.clear();

                            for (Element element : elements) {
                                String category = element.getElementsByTag("category").get(0).text();
                                String date = element.getElementsByTag("fcstDate").get(0).text();
                                String time = element.getElementsByTag("fcstTime").get(0).text();
                                String value = element.getElementsByTag("fcstValue").get(0).text();

                                int hour = Integer.parseInt(time.substring(0, 2));
                                int minute = Integer.parseInt(time.substring(2,4));

                                if(category.equals("T1H")) {
                                    weatherAdapter.itemList.add(new WeatherInfo(hour, minute,  Integer.parseInt(value)));
                                }
//                                if (category.equals("T1H")) {
//                                    text += "날짜: " + date + ", 시간: " + time + ", 온도: " + value + "\n";
//                                }
                            }

                            Toast.makeText(getApplicationContext(), "갯수: " + weatherAdapter.itemList.size(), Toast.LENGTH_SHORT).show();

                            weatherAdapter.notifyDataSetChanged();

//                            result.setText(text);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        date.setText(getStringFromDate(year, month, day));
        time.setText(getStringFromTime(hour, minute));
    }

    // ListView -> 개선 -> RecyclerView
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
        public class MyHolder extends RecyclerView.ViewHolder {
            public ImageView image;
            public TextView time;
            public TextView temperature;

            public MyHolder(View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.weather_image);
                time = itemView.findViewById(R.id.weather_tab_time);
                temperature = itemView.findViewById(R.id.weather_tab_temperature);
            }
        }

        private ArrayList<WeatherInfo> itemList;
        private Context context;
        private View.OnClickListener onClickItem;

        public MyAdapter(Context context, ArrayList<WeatherInfo> itemList, View.OnClickListener onClickItem) {
            this.context = context;
            this.itemList = itemList;
            this.onClickItem = onClickItem;
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // context 와 parent.getContext() 는 같다.
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.weather_tab, parent, false);

            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            WeatherInfo item = itemList.get(position);

//            Toast.makeText(getApplicationContext(), "시간: " + item.getTime(), Toast.LENGTH_SHORT).show();
            boolean isDay = 6 <= item.getHour() && item.getHour() < 18;
            Drawable drawable = getResources().getDrawable(isDay ? R.drawable.clear : R.drawable.midnight);
            holder.image.setImageDrawable(drawable);
            holder.time.setText(item.getHour()+ "시");
            holder.temperature.setText(item.getTemperature() + "'C");
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }

    public static String getStringZero(int value) {
        if (value < 10) {
            return "0" + String.valueOf(value);
        } else {
            return String.valueOf(value);
        }
    }

    public static String getStringFromDate(int year, int month, int day) {
        // 연도, 월, 일 -> 20220728
        // 20220728

        String result = String.valueOf(year);
        result += getStringZero(month);
        result += getStringZero(day);

        return result;
    }

    public static String getStringFromTime(int hour, int minute) {
        String result = getStringZero(hour) + getStringZero(minute);

        return result;
    }
}

