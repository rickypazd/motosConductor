package utiles;

public class Constants {
	public interface ACTION {
		public static String MAIN_ACTION = "com.truiton.foregroundservice.action.main";
		public static String PREV_ACTION = "com.truiton.foregroundservice.action.prev";
		public static String PLAY_ACTION = "com.truiton.foregroundservice.action.play";
		public static String NEXT_ACTION = "com.truiton.foregroundservice.action.next";
		public static String STARTFOREGROUND_ACTION = "com.truiton.foregroundservice.action.startforeground";
		public static String STOPFOREGROUND_ACTION = "com.truiton.foregroundservice.action.stopforeground";
	}

	public interface NOTIFICATION_ID {
		public static int FOREGROUND_SERVICE = 101;
	}
	public static Boolean isOnlineNet() {
		try {
			Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");
			int val           = p.waitFor();
			boolean reachable = (val == 0);
			return reachable;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
