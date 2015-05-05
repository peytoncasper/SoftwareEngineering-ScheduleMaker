package edu.uta.ucs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    public static final String ACTION_RESP ="edu.uta.ucs.intent.action.MAIN_ACTIVITY";
    private static final String SPOOF_CLASSLIST = "{\"Results\":[{\"CourseId\":\"ENGL-1301\",\"CourseName\":\"ENGL 1301 - RHETORIC AND COMPOSITION I\",\"CourseResults\":[{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80594\",\"Section\":\"001\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80595\",\"Section\":\"002\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80596\",\"Section\":\"003\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80597\",\"Section\":\"004\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80598\",\"Section\":\"005\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80599\",\"Section\":\"006\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80600\",\"Section\":\"007\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80601\",\"Section\":\"008\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80602\",\"Section\":\"009\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80603\",\"Section\":\"010\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80604\",\"Section\":\"011\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80605\",\"Section\":\"012\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80606\",\"Section\":\"013\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80607\",\"Section\":\"014\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80608\",\"Section\":\"015\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80609\",\"Section\":\"016\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80610\",\"Section\":\"017\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80611\",\"Section\":\"018\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80612\",\"Section\":\"019\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80613\",\"Section\":\"020\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80614\",\"Section\":\"021\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80615\",\"Section\":\"022\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80616\",\"Section\":\"023\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80617\",\"Section\":\"024\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80618\",\"Section\":\"025\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80619\",\"Section\":\"026\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80620\",\"Section\":\"027\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80621\",\"Section\":\"028\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80622\",\"Section\":\"029\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80623\",\"Section\":\"030\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"86701\",\"Section\":\"031\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80624\",\"Section\":\"032\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80625\",\"Section\":\"033\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"86702\",\"Section\":\"034\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80626\",\"Section\":\"035\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"86703\",\"Section\":\"036\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80627\",\"Section\":\"038\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80628\",\"Section\":\"039\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"4:00PM-5:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80629\",\"Section\":\"040\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"5:30PM-6:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80630\",\"Section\":\"041\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80631\",\"Section\":\"042\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80632\",\"Section\":\"043\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80633\",\"Section\":\"044\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80634\",\"Section\":\"045\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:30AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80635\",\"Section\":\"046\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:30AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80636\",\"Section\":\"047\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:30AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80637\",\"Section\":\"048\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:30AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80638\",\"Section\":\"049\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80639\",\"Section\":\"050\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80640\",\"Section\":\"051\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80641\",\"Section\":\"052\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80642\",\"Section\":\"053\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80643\",\"Section\":\"054\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80644\",\"Section\":\"055\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80645\",\"Section\":\"056\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80646\",\"Section\":\"057\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80647\",\"Section\":\"058\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80648\",\"Section\":\"059\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80649\",\"Section\":\"060\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80650\",\"Section\":\"061\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:30PM-4:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80651\",\"Section\":\"062\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"7:00PM-8:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80652\",\"Section\":\"066\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80653\",\"Section\":\"067\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80654\",\"Section\":\"068\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80655\",\"Section\":\"069\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80656\",\"Section\":\"071\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80657\",\"Section\":\"072\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80658\",\"Section\":\"073\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"5:30PM-6:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85880\",\"Section\":\"075\",\"Room\":\"OFF WEB\",\"Instructor\":\"Staff\",\"MeetingTime\":\"TBA\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"83333\",\"Section\":\"077\",\"Room\":\"OFF WEB\",\"Instructor\":\"Staff\",\"MeetingTime\":\"TBA\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84535\",\"Section\":\"079\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84537\",\"Section\":\"081\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86406\",\"Section\":\"082\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86407\",\"Section\":\"083\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86408\",\"Section\":\"084\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86409\",\"Section\":\"085\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86513\",\"Section\":\"086\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86515\",\"Section\":\"087\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:00PM-4:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86543\",\"Section\":\"089\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:00PM-4:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86556\",\"Section\":\"090\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87922\",\"Section\":\"091\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87923\",\"Section\":\"092\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87924\",\"Section\":\"093\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87920\",\"Section\":\"094\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87921\",\"Section\":\"095\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87925\",\"Section\":\"096\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87926\",\"Section\":\"097\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"83165\",\"Section\":\"700\",\"Room\":\"OFF WEB\",\"Instructor\":\"Pamela K Rollins\",\"MeetingTime\":\"TBA\",\"Status\":\"Open\"}]},{\"CourseId\":\"MATH-1426\",\"CourseName\":\"MATH 1426 - CALCULUS I\",\"CourseResults\":[{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84744\",\"Section\":\"100\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84745\",\"Section\":\"101\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84746\",\"Section\":\"102\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84747\",\"Section\":\"200\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84748\",\"Section\":\"201\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84749\",\"Section\":\"202\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"87226\",\"Section\":\"271\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"87230\",\"Section\":\"273\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84750\",\"Section\":\"300\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"4:00PM-5:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84751\",\"Section\":\"301\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84752\",\"Section\":\"302\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"5:30PM-6:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85931\",\"Section\":\"400\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85932\",\"Section\":\"401\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85933\",\"Section\":\"402\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:30PM-4:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85804\",\"Section\":\"500\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"7:00PM-8:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85805\",\"Section\":\"501\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"6:00PM-6:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"84753\",\"Section\":\"700\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"84754\",\"Section\":\"701\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85029\",\"Section\":\"710\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85030\",\"Section\":\"711\",\"Room\":\"TBATBA\",\"Instructor\":\"StaffStaff\",\"MeetingTime\":\"11:00AM-11:50AMWe\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85031\",\"Section\":\"720\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85033\",\"Section\":\"721\",\"Room\":\"TBATBA\",\"Instructor\":\"StaffStaff\",\"MeetingTime\":\"11:00AM-11:50AMWe\",\"Status\":\"Open\"}]},{\"CourseId\":\"PHYS-1441\",\"CourseName\":\"PHYS 1441 - GENERAL COLLEGE PHYSICS I\",\"CourseResults\":[{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"81301\",\"Section\":\"002\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81148\",\"Section\":\"003\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81149\",\"Section\":\"004\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81150\",\"Section\":\"005\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"5:30PM-6:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"81151\",\"Section\":\"006\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"81152\",\"Section\":\"007\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"7:00PM-9:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81153\",\"Section\":\"008\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81154\",\"Section\":\"009\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81155\",\"Section\":\"010\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-4:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"W\",\"F\"],\"CourseNumber\":\"81156\",\"Section\":\"011\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"W\",\"F\"],\"CourseNumber\":\"81157\",\"Section\":\"012\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"W\",\"F\"],\"CourseNumber\":\"81158\",\"Section\":\"013\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"7:00PM-9:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81159\",\"Section\":\"014\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81160\",\"Section\":\"015\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81161\",\"Section\":\"016\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-4:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\",\"F\"],\"CourseNumber\":\"81162\",\"Section\":\"017\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\",\"F\"],\"CourseNumber\":\"81163\",\"Section\":\"018\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-4:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"81164\",\"Section\":\"019\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-3:50PM\",\"Status\":\"Open\"}]},{\"CourseId\":\"CSE-1105\",\"CourseName\":\"CSE 1105 - INTRODUCTION TO COMPUTER SCIENCE AND ENGINEERING\",\"CourseResults\":[{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"84195\",\"Section\":\"001\",\"Room\":\"TBA\",\"Instructor\":\"Eric W Becker\",\"MeetingTime\":\"3:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84196\",\"Section\":\"002\",\"Room\":\"TBA\",\"Instructor\":\"Eric W Becker\",\"MeetingTime\":\"3:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"84197\",\"Section\":\"003\",\"Room\":\"TBA\",\"Instructor\":\"Eric W Becker\",\"MeetingTime\":\"10:30AM-11:20AM\",\"Status\":\"Open\"}]}],\"TimeTaken\":29.622961699999998,\"Success\":true}";

    String[] desiredCourseList = {};//{"ENGL-1301","MATH-1426","PHYS-1443","CSE-1105"};
    String baseURL = "http://ucs.azurewebsites.net/UTA/ClassStatus?classes=";
    TextView responseDisplay;

    private TextView mainText;
    private EditText courseInput;
    private Switch spoofServerSwitch;
    private Switch useDefaultCourseList;

    private ListView sectionListView;
    private MySectionArrayAdapter adapter;

    ArrayList<Section> sectionArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_main);

        courseInput =((EditText) findViewById(R.id.editText));

        spoofServerSwitch = (Switch) findViewById(R.id.spoofServerSwitch);
        spoofServerSwitch.setChecked(true);

        useDefaultCourseList = (Switch) findViewById(R.id.useDefaultCourseListSwitch);
        useDefaultCourseList.setChecked(false);
        useDefaultCourseList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    courseInput.setText("ENGL-1301,MATH-1426,PHYS-1443,CSE-1105");
                }
                else courseInput.setText("");
            }
        });

        responseDisplay = (TextView)findViewById(R.id.textView);
        responseDisplay.setText("Press FETCH JSON to attempt a data fetch");

        sectionListView = (ListView) findViewById(R.id.listView);

        sectionArrayList = new ArrayList<>();

        LocalBroadcastManager.getInstance(this).registerReceiver(new ResponseReceiver(), new IntentFilter(ACTION_RESP));

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new MySectionArrayAdapter(MainActivity.this, R.layout.section_list_display, sectionArrayList);
        adapter.setNotifyOnChange(true);
        sectionListView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Schedule.loadSchedulesFromFile(MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void generateSchedule(View view){
        Log.d("MainActivity","Opening Generate Schedule");
        Intent startSelectCoursesActivity = new Intent(MainActivity.this, SelectCourses.class);
        MainActivity.this.startActivity(startSelectCoursesActivity);
    }

    public void requestJSON(View view){

        responseDisplay.setText("Please wait, attempting to fetch data...");

        StringBuilder urlBuilder = new StringBuilder(baseURL);
        String classTextField = courseInput.getText().toString();
        urlBuilder.append( ( classTextField.length() > 0 ? classTextField:"") + "," );
        Log.d("URL BUILDING", urlBuilder.toString());
        String url = urlBuilder.length() > 0 ? urlBuilder.substring( 0, urlBuilder.length() - 1 ): "";

        boolean switchStatus = spoofServerSwitch.isChecked();

        Intent intent = new Intent(this, HTTPGetService.class);
        if(spoofServerSwitch.isChecked()) {

            intent.putExtra(HTTPGetService.URL_REQUEST, HTTPGetService.SPOOF_SERVER);
            intent.putExtra(HTTPGetService.SPOOFED_RESPONSE, SPOOF_CLASSLIST);
        }
        else
            intent.putExtra(HTTPGetService.URL_REQUEST, url);

        intent.putExtra(HTTPGetService.SOURCE_INTENT, ACTION_RESP);

        startService(intent);
    }

    public void stopMethod(){
    }

    private class ResponseReceiver extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra(HTTPGetService.SERVER_RESPONSE);
            Log.d("Received: ",response);
            responseDisplay.setText("About to Show text!");
            responseDisplay.setText(response);
            ArrayList<Course> courseList = new ArrayList<Course>();
            int numberOfSectionsTotal = 0;

            try {
                JSONObject rawResult = new JSONObject(response);
                JSONArray jsonCourses = rawResult.getJSONArray("Results");
                SharedPreferences.Editor editor = getSharedPreferences("SharedPrefs", MODE_PRIVATE).edit();
                editor.putString("fetchedCourseListJSON",rawResult.getString("Results"));
                editor.apply();
                float timeTaken = Float.parseFloat(rawResult.getString("TimeTaken"));
                Log.d("New Request Time Taken:", Float.toString(timeTaken));
                courseList = Course.buildCourseList(jsonCourses);

                responseDisplay.setText(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (Course course : courseList){
                sectionArrayList.addAll(course.getSectionList());
            }

            Log.d("New Section", "ArrayList Built");
            Log.d("New Section", "ListView Built");
            sectionArrayList = new ArrayList<Section>(numberOfSectionsTotal);
            adapter.notifyDataSetChanged();

        }
    }
}
