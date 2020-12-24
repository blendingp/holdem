package egovframework.example.sample.web;

import java.util.ArrayList;

import egovframework.example.sample.web.model.AuthSelf;
import egovframework.example.sample.web.model.MemberInfo;
import egovframework.example.sample.web.model.MembersInfo;
import egovframework.example.sample.web.model.ProfileModel;
import egovframework.example.sample.web.model.TaskModel;

public class UserInfo {

    public long balance = 0;
	public long point = 0;
	public long safe_point = 0;
	public long safe_balance = 0;
    public long cash = 0;
    public long bank = 0;
    public long totalpayment = 0;
    public Attendance attendance;
    public MembersInfo membersinfo;
    public ProfileModel totalprofile;
    public ProfileModel todayprofile;
    public ArrayList<TaskModel> tasklist;
    public ArrayList<String> avatalist;
    public ArrayList<Item> consumableItem;
    public MemberInfo memberinfo;
    public AuthSelf auth;
    public boolean checkattendance;
    
}
