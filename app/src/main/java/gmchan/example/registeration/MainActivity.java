package gmchan.example.registeration;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView noticeListView;
    private NoticeListAdapter adapter;
    private List<Notice> noticeList;
    public static String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 현재 스마트폰 화면을 특정한 방향으로 고정하고싶을때 사용(세로)

        userID=getIntent().getStringExtra("userID");

        noticeListView = (ListView) findViewById(R.id.noticeListView);
        noticeList = new ArrayList<Notice>(); // 초기화
        // 공지할것들 추가하기.
        adapter = new NoticeListAdapter(getApplicationContext(), noticeList); //어댑터에 리스트내역을 매칭시킴.
        noticeListView.setAdapter(adapter); //리스트뷰에 어댑터 매칭.


        final Button courseButton = (Button) findViewById(R.id.courseButton);
        final Button statisticButton = (Button) findViewById(R.id.statisticButton);
        final Button scheduleButton = (Button) findViewById(R.id.scheduleButton);
        final Button logoutButton=(Button)findViewById(R.id.logoutButton);
        final LinearLayout notice = (LinearLayout) findViewById(R.id.notice);

        courseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notice.setVisibility(View.GONE);//공지사항부분이 보이지 않도록, fragment로 바꿔줄수 있게
                courseButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                statisticButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                scheduleButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new CourseFragment());
                fragmentTransaction.commit();
            }
        });

        statisticButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notice.setVisibility(View.GONE);//공지사항부분이 보이지 않도록, fragment로 바꿔줄수 있게
                courseButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                statisticButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                scheduleButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new StatisticFragment());
                fragmentTransaction.commit();
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notice.setVisibility(View.GONE);//공지사항부분이 보이지 않도록, fragment로 바꿔줄수 있게
                courseButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                statisticButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                scheduleButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new ScheduleFragment());
                fragmentTransaction.commit();
            }
        });

        // 로그아웃 버튼 넣기
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                        .show();
            }
        });


        new BackgroundTask().execute(); // 정상적으로 리스트가 들어갈 수 있게 해줌.
    }

    //PHP서버에 접속해서 JSON타입으로 데이터를 가져옴
    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            target = "https://rhdiddlsms.cafe24.com/NoticeList.php";
        }

        // 실제 데이터를 가져오는 부분
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target); //해당서버에 접속 할 수 있도록 설정.
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream(); //넘어오는 결과값을 그대로 저장.
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp; //결과값을 여기에 저장
                StringBuilder stringBuilder = new StringBuilder();

                //버퍼 생성 후 한줄씩 가져옴
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim(); // 결과값이 여기에 리턴되면 이 값이 onPOstExcute파라미터로 넘어감
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values){
            super.onProgressUpdate();
        }

        //여기서는 가져온 데이터를 Notice객체에 넣은뒤 리스트뷰 출력을 위한 List객체에 넣어주는 부분
        @Override
        public void onPostExecute(String result) //공지사항 리스트 추가
        {
            try{
                JSONObject jsonObject=new JSONObject(result);
                JSONArray jsonArray=jsonObject.getJSONArray("response");
                int count=0;
                String noticeContent, noticeName, noticeDate;
                //json타입의 값을 하나씩 빼서 Notice 객체에 저장후 리스트에 추가하는 부분
                while(count<jsonArray.length()){
                    JSONObject object=jsonArray.getJSONObject(count);
                    noticeContent=object.getString("noticeContent");
                    noticeName=object.getString("noticeName");
                    noticeDate=object.getString("noticeDate");
                    Notice notice=new Notice(noticeContent,noticeName, noticeDate);
                    noticeList.add(notice);
                    adapter.notifyDataSetChanged();
                    count++;
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //백버튼을 두번 누르면 앱이 종료되게 함.
    // 대부분 애플리케이션에서 생성되어 있음.
    private long pressedTime;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //백버튼이 눌리고 1.5초안에 또 눌리면 종료가됨
        if ( pressedTime == 0 ) {
            Toast.makeText(MainActivity.this, " 한 번 더 누르면 종료됩니다." , Toast.LENGTH_LONG).show();
            pressedTime = System.currentTimeMillis();
        }
        else {
            int seconds = (int) (System.currentTimeMillis() - pressedTime);

            if ( seconds > 1500 ) {
                Toast.makeText(MainActivity.this, " 한 번 더 누르면 종료됩니다." , Toast.LENGTH_LONG).show();
                pressedTime = 0 ;
            }
            else {
                super.onBackPressed();//이걸 따라가다보면 결국 finish()를 실행 시키는 부분이 나옴
//              finish(); // app 종료 시키기
            }
        }
    }
}

