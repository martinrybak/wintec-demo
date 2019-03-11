package cn.wintec.wtandroiddemo.multiinone;

import java.util.LinkedList;
import java.util.List;

import cn.wintec.wtandroiddemo.multiinone.MainApplication;

import android.app.Activity;
import android.app.Application;

public class MultiInOneApplication extends MainApplication
{
	private List<Activity> activitylist =  new LinkedList<Activity>();
	private static MultiInOneApplication instance;
	public MultiInOneApplication()
	{	
		super();
	}
	//单例模式中获取唯一的RM2091Applcation 示例
	
	public static MultiInOneApplication getInstance() {
//		if(null== instance)
//			instance = new RM2091Application();
		return instance;
	}
	public void addActivity(Activity activity) {
		// TODO Auto-generated method stub
		activitylist.add(activity);	
	}
	public void exit() {
		// TODO Auto-generated method stub
		for(Activity activity : activitylist)
		{
			
			activity.finish();
		}
		System.exit(0);
	}
	@Override
	public void onCreate() {
		instance = this;
		super.onCreate();
	}
	
}