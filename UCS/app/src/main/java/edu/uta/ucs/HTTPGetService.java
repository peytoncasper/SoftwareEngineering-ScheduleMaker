package edu.uta.ucs;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class HTTPGetService extends Service {

    public static final int URL_REQUEST = 0;

    //public static final String URL_REQUEST = "edu.uta.ucs.URL_REQUEST";
    public static final String SPOOF_SERVER_RESPONSE = "edu.uta.ucs.SPOOF_SERVER_RESPONSE";
    public static final String SERVER_RESPONSE = "edu.uta.ucs.SERVER_RESPONSE";
    public static final String SPOOFED_RESPONSE = "edu.uta.ucs.SPOOFED_RESPONSE";
    public static final String SPOOF_SERVER = "SPOOF";
    public static final String SOURCE_INTENT = "SOURCE_INTENT";
    public static final String SPOOFED_CLASSLIST_RESPONSE ="{\"Results\":[{\"CourseId\":\"ENGL-1301\",\"CourseName\":\"ENGL 1301 - RHETORIC AND COMPOSITION I\",\"CourseResults\":[{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80594\",\"Section\":\"001\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80595\",\"Section\":\"002\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80596\",\"Section\":\"003\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80597\",\"Section\":\"004\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80598\",\"Section\":\"005\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80599\",\"Section\":\"006\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80600\",\"Section\":\"007\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80601\",\"Section\":\"008\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80602\",\"Section\":\"009\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80603\",\"Section\":\"010\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80604\",\"Section\":\"011\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80605\",\"Section\":\"012\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80606\",\"Section\":\"013\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80607\",\"Section\":\"014\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80608\",\"Section\":\"015\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80609\",\"Section\":\"016\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80610\",\"Section\":\"017\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80611\",\"Section\":\"018\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80612\",\"Section\":\"019\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80613\",\"Section\":\"020\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80614\",\"Section\":\"021\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80615\",\"Section\":\"022\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80616\",\"Section\":\"023\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80617\",\"Section\":\"024\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80618\",\"Section\":\"025\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80619\",\"Section\":\"026\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80620\",\"Section\":\"027\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80621\",\"Section\":\"028\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80622\",\"Section\":\"029\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80623\",\"Section\":\"030\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"86701\",\"Section\":\"031\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80624\",\"Section\":\"032\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80625\",\"Section\":\"033\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"86702\",\"Section\":\"034\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80626\",\"Section\":\"035\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"86703\",\"Section\":\"036\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80627\",\"Section\":\"038\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80628\",\"Section\":\"039\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"4:00PM-5:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80629\",\"Section\":\"040\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"5:30PM-6:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80630\",\"Section\":\"041\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80631\",\"Section\":\"042\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80632\",\"Section\":\"043\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80633\",\"Section\":\"044\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80634\",\"Section\":\"045\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:30AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80635\",\"Section\":\"046\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:30AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80636\",\"Section\":\"047\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:30AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80637\",\"Section\":\"048\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:30AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80638\",\"Section\":\"049\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80639\",\"Section\":\"050\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80640\",\"Section\":\"051\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80641\",\"Section\":\"052\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80642\",\"Section\":\"053\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80643\",\"Section\":\"054\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80644\",\"Section\":\"055\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80645\",\"Section\":\"056\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80646\",\"Section\":\"057\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80647\",\"Section\":\"058\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80648\",\"Section\":\"059\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80649\",\"Section\":\"060\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80650\",\"Section\":\"061\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:30PM-4:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80651\",\"Section\":\"062\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"7:00PM-8:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80652\",\"Section\":\"066\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80653\",\"Section\":\"067\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80654\",\"Section\":\"068\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80655\",\"Section\":\"069\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80656\",\"Section\":\"071\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80657\",\"Section\":\"072\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80658\",\"Section\":\"073\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"5:30PM-6:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85880\",\"Section\":\"075\",\"Room\":\"OFF WEB\",\"Instructor\":\"Staff\",\"MeetingTime\":\"TBA\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"83333\",\"Section\":\"077\",\"Room\":\"OFF WEB\",\"Instructor\":\"Staff\",\"MeetingTime\":\"TBA\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84535\",\"Section\":\"079\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84537\",\"Section\":\"081\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86406\",\"Section\":\"082\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86407\",\"Section\":\"083\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86408\",\"Section\":\"084\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86409\",\"Section\":\"085\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86513\",\"Section\":\"086\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86515\",\"Section\":\"087\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:00PM-4:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86543\",\"Section\":\"089\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:00PM-4:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86556\",\"Section\":\"090\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87922\",\"Section\":\"091\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87923\",\"Section\":\"092\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87924\",\"Section\":\"093\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87920\",\"Section\":\"094\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87921\",\"Section\":\"095\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87925\",\"Section\":\"096\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87926\",\"Section\":\"097\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"83165\",\"Section\":\"700\",\"Room\":\"OFF WEB\",\"Instructor\":\"Pamela K Rollins\",\"MeetingTime\":\"TBA\",\"Status\":\"Open\"}]},{\"CourseId\":\"MATH-1426\",\"CourseName\":\"MATH 1426 - CALCULUS I\",\"CourseResults\":[{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84744\",\"Section\":\"100\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84745\",\"Section\":\"101\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84746\",\"Section\":\"102\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84747\",\"Section\":\"200\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84748\",\"Section\":\"201\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84749\",\"Section\":\"202\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"87226\",\"Section\":\"271\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"87230\",\"Section\":\"273\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84750\",\"Section\":\"300\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"4:00PM-5:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84751\",\"Section\":\"301\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84752\",\"Section\":\"302\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"5:30PM-6:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85931\",\"Section\":\"400\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85932\",\"Section\":\"401\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85933\",\"Section\":\"402\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:30PM-4:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85804\",\"Section\":\"500\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"7:00PM-8:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85805\",\"Section\":\"501\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"6:00PM-6:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"84753\",\"Section\":\"700\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"84754\",\"Section\":\"701\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85029\",\"Section\":\"710\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85030\",\"Section\":\"711\",\"Room\":\"TBATBA\",\"Instructor\":\"StaffStaff\",\"MeetingTime\":\"11:00AM-11:50AMWe\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85031\",\"Section\":\"720\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85033\",\"Section\":\"721\",\"Room\":\"TBATBA\",\"Instructor\":\"StaffStaff\",\"MeetingTime\":\"11:00AM-11:50AMWe\",\"Status\":\"Open\"}]},{\"CourseId\":\"PHYS-1441\",\"CourseName\":\"PHYS 1441 - GENERAL COLLEGE PHYSICS I\",\"CourseResults\":[{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"81301\",\"Section\":\"002\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81148\",\"Section\":\"003\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81149\",\"Section\":\"004\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81150\",\"Section\":\"005\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"5:30PM-6:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"81151\",\"Section\":\"006\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"81152\",\"Section\":\"007\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"7:00PM-9:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81153\",\"Section\":\"008\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81154\",\"Section\":\"009\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81155\",\"Section\":\"010\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-4:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"W\",\"F\"],\"CourseNumber\":\"81156\",\"Section\":\"011\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"W\",\"F\"],\"CourseNumber\":\"81157\",\"Section\":\"012\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"W\",\"F\"],\"CourseNumber\":\"81158\",\"Section\":\"013\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"7:00PM-9:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81159\",\"Section\":\"014\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81160\",\"Section\":\"015\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81161\",\"Section\":\"016\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-4:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\",\"F\"],\"CourseNumber\":\"81162\",\"Section\":\"017\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\",\"F\"],\"CourseNumber\":\"81163\",\"Section\":\"018\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-4:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"81164\",\"Section\":\"019\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-3:50PM\",\"Status\":\"Open\"}]},{\"CourseId\":\"CSE-1105\",\"CourseName\":\"CSE 1105 - INTRODUCTION TO COMPUTER SCIENCE AND ENGINEERING\",\"CourseResults\":[{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"84195\",\"Section\":\"001\",\"Room\":\"TBA\",\"Instructor\":\"Eric W Becker\",\"MeetingTime\":\"3:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84196\",\"Section\":\"002\",\"Room\":\"TBA\",\"Instructor\":\"Eric W Becker\",\"MeetingTime\":\"3:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"84197\",\"Section\":\"003\",\"Room\":\"TBA\",\"Instructor\":\"Eric W Becker\",\"MeetingTime\":\"10:30AM-11:20AM\",\"Status\":\"Open\"}]}],\"TimeTaken\":29.622961699999998,\"Success\":true}";
    public static final String SPOOFED_LOGIN_RESPONSE = "{\"Success\":true,\"Email\":\"a@a.a\"}";

    private final IBinder mbinder = new LocalBinder();
    Messenger messenger;
    String url;

    /*
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.

    public HTTPGetService() {
        super("HTTPGetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String response;

        String url = intent.getStringExtra(URL_REQUEST);    // Get url

        Log.d("HTTPGetService URL", url);

        if (url.equalsIgnoreCase(SPOOF_SERVER)) {
            response = intent.getStringExtra(SPOOFED_RESPONSE);
        }
        else
            response = fetchJSON(url);

        SharedPreferences.Editor editor = getSharedPreferences("SharedPrefs", MODE_PRIVATE).edit();
        editor.putString("Server Message",response);
        editor.apply();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(intent.getStringExtra(SOURCE_INTENT));
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(SERVER_RESPONSE, response);
        sendBroadcast(broadcastIntent);

        //Log.d("Broadcasting Response: ",response);
    } */

    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(this, "HTTPGetService has been created", Toast.LENGTH_LONG).show();
        Log.d("Service Test", "HTTPGetService has been created");
        messenger = new Messenger(new MessageHandler());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "HTTPGetService has been stopped", Toast.LENGTH_LONG).show();
        Log.d("Service Test", "HTTPGetService has been stopped");
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link android.os.IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link android.content.Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class MessageHandler extends android.os.Handler{
        public void handleMessage(Message message){
            switch(message.what){
                case URL_REQUEST:
                    Toast.makeText(getBaseContext(), "Hello from the service", Toast.LENGTH_LONG).show();
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }
    /**
     * Making service call
     * @url - url to make request
     * */
    public String fetchJSON(String url) {

        Log.d("HTTPGetService URL:", url);
        String response = "";
        try {
            // http client
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
            HttpConnectionParams.setSoTimeout(httpParams, 45000);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            Log.d("test:", "fetchJSON HTTP parameters set");

            HttpGet httpGet = new HttpGet(url);
            Log.d("test:", "HTTPGet setup");
            httpResponse = httpClient.execute(httpGet);
            Log.d("test:", "HTTPGet executed - response received");

            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("Service Test", "HTTP Request Failed - UnsupportedEncodingException");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.d("Service Test", "HTTP Request Failed - ClientProtocolException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Service Test", "HTTP Request Failed - IOException");
            response = "Server Request Timed-out";
        }

        Log.d("Server reply:", response);
        return response;
    }

    public class LocalBinder extends Binder {

        public HTTPGetService getService() {
            return HTTPGetService.this;
        }
    }
}
