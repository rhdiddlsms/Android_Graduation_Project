package gmchan.example.registeration;


import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CourseListAdapter extends BaseAdapter {
    private Context context;
    private List<Course> courseList;
    private Fragment parent;
    private String userID=MainActivity.userID;
    private Schedule schedule= new Schedule();
    private List<Integer> courseIDList;

    public static int totalCredit=0;



    public CourseListAdapter(Context context, List<Course> courseList, Fragment parent) {
        this.context = context;
        this.courseList = courseList;
        this.parent=parent;
        schedule=new Schedule();
        courseIDList=new ArrayList<Integer>();
        new BackgroundTask().execute();
        totalCredit=0;
    }



    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int i) {
        return courseList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View v=View.inflate(context, R.layout.course,null);
        TextView courseGrade=(TextView)v.findViewById(R.id.courseGrade);
        TextView courseTitle=(TextView)v.findViewById(R.id.courseTitle);
        TextView courseCredit=(TextView)v.findViewById(R.id.courseCredit);
        TextView courseDivide=(TextView)v.findViewById(R.id.courseDivide);
        TextView coursePersonnel=(TextView)v.findViewById(R.id.coursePersonnel);
        TextView courseProfessor=(TextView)v.findViewById(R.id.courseProfessor);
        TextView courseTime=(TextView)v.findViewById(R.id.courseTime);

        //학년의 문자열이 "" 일 때
        if(courseList.get(i).getCourseGrade().equals("제한 없음")||courseList.get(i).getCourseGrade().equals(""))
        {
            courseGrade.setText("모든 학년"); //사용자에게 보여지는 부분
        }
        else
        {
            courseGrade.setText(courseList.get(i).getCourseGrade()); // courseList 에서 원소를 불러온다음에 + 학년을 붙혀줌.
        }
        courseTitle.setText(courseList.get(i).getCourseTitle());
        courseCredit.setText(courseList.get(i).getCourseCredit()+"학점");
        courseDivide.setText(courseList.get(i).getCourseDivide()+"분반");

        if(courseList.get(i).getCoursePersonnel()==0)
        {
            coursePersonnel.setText("인원 제한 없음");
        }
        else
        {
            coursePersonnel.setText("제한 인원 : "+courseList.get(i).getCoursePersonnel()+"명");
        }

        courseProfessor.setText(courseList.get(i).getCourseProfessor() + "교수님");
        courseTime.setText(courseList.get(i).getCourseTime() + ""); //시간대 출력.


        v.setTag(courseList.get(i).getCourseID());

        //강의추가
        Button addButton=(Button)v.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean validate = false;
                validate = schedule.validate(courseList.get(i).getCourseTime());
                if (!already(courseIDList, courseList.get(i).getCourseID())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                    AlertDialog dialog = builder.setMessage("이미 추가한 강의입니다.")
                            .setPositiveButton("다시 시도", null)
                            .create();
                    dialog.show();
                }
                else if (totalCredit+courseList.get(i).getCourseCredit()>24)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                    AlertDialog dialog = builder.setMessage("24학점을 초과 할 수 없습니다.")
                            .setPositiveButton("다시 시도", null)
                            .create();
                    dialog.show();
                }
                else if (validate == false)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                    AlertDialog dialog = builder.setMessage("시간표가 중복됩니다.")
                            .setPositiveButton("다시 시도", null)
                            .create();
                    dialog.show();
                }
                else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(parent.getActivity());
                                    AlertDialog dialog = builder.setMessage("강의가 추가되었습니다.")
                                            .setPositiveButton("확인", null)
                                            .create();
                                    dialog.show();

                                    //강의가 추가 될 때마다 정보를 넣어준다.
                                    courseIDList.add(courseList.get(i).getCourseID());
                                    schedule.addSchedule(courseList.get(i).getCourseTime());
                                    totalCredit+=courseList.get(i).getCourseCredit();

                                } else {
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(parent.getActivity());
                                    AlertDialog dialog = builder.setMessage("강의 추가에 실패했습니다.")
                                            .setNegativeButton("확인", null)
                                            .create();
                                    dialog.show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };


                    AddRequest addRequest = new AddRequest(userID, courseList.get(i).getCourseID() + "", responseListener);
                    RequestQueue queue = Volley.newRequestQueue(parent.getActivity());
                    queue.add(addRequest);
                }
            }
        });

        return v;
    }

    // 더 추가될 부분

    //PHP서버에 접속해서 JSON타입으로 데이터를 가져옴
    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            try {
            target = "https://rhdiddlsms.cafe24.com/ScheduleList.php?userID=" + URLEncoder.encode(userID, "UTF-8");
            }catch (Exception e){
                e.printStackTrace();
            }
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
                String courseProfessor, courseTime;
                int courseID;
                totalCredit=0;
                while(count<jsonArray.length()){
                    JSONObject object=jsonArray.getJSONObject(count);
                    courseID=object.getInt("courseID");
                    courseProfessor=object.getString("courseProfessor");
                    courseTime=object.getString("courseTime");
                    totalCredit+=object.getInt("courseCredit");
                    courseIDList.add(courseID);
                    schedule.addSchedule(courseTime);
                    count++;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public boolean already(List<Integer>courseIDList, int item)
    {
        for(int i=0; i<courseIDList.size(); i++)
        {
            if(courseIDList.get(i)==item){
                return false;
            }
        }

        return true;
    }


}