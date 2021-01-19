package gmchan.example.registeration;
//강의 추가에 대해서 요청하는것.
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddRequest extends StringRequest {

    final static private String URL = "https://rhdiddlsms.cafe24.com/CourseAdd.php";
    private Map<String, String> parameters;

    public AddRequest(String userID, String courseID, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters =new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("courseID", courseID);

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}

