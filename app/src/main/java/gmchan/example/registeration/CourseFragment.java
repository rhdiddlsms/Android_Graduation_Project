package gmchan.example.registeration;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseFragment extends Fragment {//Fragment 특정한 화면 안에 있는 세부적인 화면 구성
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener; //추가할 내용

    public CourseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseFragment newInstance(String param1, String param2) {
        CourseFragment fragment = new CourseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ArrayAdapter yearAdapter;
    private Spinner yearSpinner;
    private ArrayAdapter termAdapter;
    private Spinner termSpinner;
    private ArrayAdapter areaAdapter;
    private Spinner areaSpinner;
    private ArrayAdapter majorAdapter;
    private Spinner majorSpinner;

    private String courseUniversity="";
//    private String courseYear="";
//    private String courseterm="";
//    private String courseArea="";

    private ListView courseListView;
    private CourseListAdapter adapter;
    private List<Course> courseList;

    @Override
    public void onActivityCreated(Bundle b){
        super.onActivityCreated(b);

        final RadioGroup courseUniversityGroup=(RadioGroup)getView().findViewById(R.id.courseUniversityGroup);
        yearSpinner=(Spinner)getView().findViewById(R.id.yearSpinner);
        termSpinner=(Spinner)getView().findViewById(R.id.termSpinner);
        areaSpinner=(Spinner)getView().findViewById(R.id.areaSpinner);
        majorSpinner=(Spinner)getView().findViewById(R.id.majorSpinner);

        courseUniversityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                RadioButton courseButton=(RadioButton)getView().findViewById(i);
                courseUniversity=courseButton.getText().toString();

                yearAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.year, android.R.layout.simple_spinner_dropdown_item);
                yearSpinner.setAdapter(yearAdapter);
                yearSpinner.setSelection(0); // array 배열안에 있는 spinner을 초기값으로 설정.

                termAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.term, android.R.layout.simple_spinner_dropdown_item);
                termSpinner.setAdapter(termAdapter);
                termSpinner.setSelection(0);

                if(courseUniversity.equals("전문학사"))
                {
                    areaAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.universityArea, android.R.layout.simple_spinner_dropdown_item);
                    areaSpinner.setAdapter(areaAdapter);
                    majorAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.universityRefinementMajor, android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                }
                else if(courseUniversity.equals("학사"))
                {
                    areaAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.graduateArea, android.R.layout.simple_spinner_dropdown_item);
                    areaSpinner.setAdapter(areaAdapter);
                    majorAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.graduateMajor, android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                }

            }
        });

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                if(areaSpinner.getSelectedItem().equals("교양"))
                {
                    majorAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.universityRefinementMajor, android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                }
                if(areaSpinner.getSelectedItem().equals("전공"))
                {
                    majorAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.universityMajor, android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                }
                if(areaSpinner.getSelectedItem().equals("전공심화"))
                {
                    majorAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.graduateMajor, android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        courseListView=(ListView)getView().findViewById(R.id.courseListView);
        courseList=new ArrayList<Course>();
        adapter=new CourseListAdapter(getContext().getApplicationContext(), courseList, this);
        courseListView.setAdapter(adapter);

        Button searchButton=(Button)getView().findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new BackgroundTask().execute();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


        class BackgroundTask extends AsyncTask<Void, Void, String> {
            String target;

            @Override
            protected void onPreExecute() {
                //super.onPreExecute();
                try {
                    target = "https://rhdiddlsms.cafe24.com/CourseList.php?courseUniversity="
                            + URLEncoder.encode(courseUniversity, "UTF-8")
                            + "&courseYear=" + URLEncoder.encode(yearSpinner.getSelectedItem().toString().substring(0, 4), "UTF-8")
                            + "&courseTerm=" + URLEncoder.encode(termSpinner.getSelectedItem().toString(), "UTF-8")
                            + "&courseArea=" + URLEncoder.encode(areaSpinner.getSelectedItem().toString(), "UTF-8")
                            + "&courseMajor=" + URLEncoder.encode(majorSpinner.getSelectedItem().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
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
                    courseList.clear();  //함수를 없앤다.
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray jsonArray=jsonObject.getJSONArray("response");
                    int count=0;
                    int courseID;//강의 고유 번호
                    String courseUniversity;//학부 혹은 대학원
                    int courseYear;//해당 년도
                    String courseTerm;//해당학기
                    String courseArea;//강의 영역
                    String courseMajor;//해당학과
                    String courseGrade;//해당학년
                    String courseTitle;//강의제목
                    int courseCredit;//강의 학점
                    int courseDivide;//강의 분반
                    int coursePersonnel;//강의 제한 인원
                    String courseProfessor;//강의 교수
                    String courseTime;//강의 시간대
                    String courseRoom;//강의실

                    while(count<jsonArray.length())
                    {
                        JSONObject object=jsonArray.getJSONObject(count); //현재 배열의 원소값 가져오기.
                        courseID = object.getInt("courseID");
                        courseUniversity = object.getString("courseUniversity");
                        courseYear = object.getInt("courseYear");
                        courseTerm = object.getString("courseTerm");
                        courseArea = object.getString("courseArea");
                        courseMajor = object.getString("courseMajor");
                        courseGrade = object.getString("courseGrade");
                        courseTitle = object.getString("courseTitle");
                        courseCredit = object.getInt("courseCredit");
                        courseDivide = object.getInt("courseDivide");
                        coursePersonnel = object.getInt("coursePersonnel");
                        courseProfessor = object.getString("courseProfessor");
                        courseTime = object.getString("courseTime");
                        courseRoom = object.getString("courseRoom");
                        Course course=new Course(courseID, courseUniversity, courseYear, courseTerm, courseArea, courseMajor, courseGrade, courseTitle,courseCredit, courseDivide,  coursePersonnel, courseProfessor, courseTime, courseRoom);
                        courseList.add(course);
                        count++;
                    }

                    if(count==0)
                    {
                        AlertDialog dialog;
                        AlertDialog.Builder builder=new AlertDialog.Builder(CourseFragment.this.getActivity());
                        dialog = builder.setMessage("조회된 강의가 없습니다.\n날짜를 확인하세요.")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                    }
                    adapter.notifyDataSetChanged();  // 값이 변경되었다.

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }



    }


