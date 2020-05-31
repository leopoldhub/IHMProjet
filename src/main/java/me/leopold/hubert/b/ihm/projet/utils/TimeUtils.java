package me.leopold.hubert.b.ihm.projet.utils;

public class TimeUtils {

	public static String msToString(long time) {
		long seconds = time/1000;
		int hours = (int) ((seconds-(seconds%3600))/3600);
		seconds = seconds-hours*3600;
		int min = (int) ((seconds-(seconds%60))/60);
		seconds = seconds-min*60;
		StringBuilder sb = new StringBuilder();
		sb.append("");
		if(hours > 0) {
			sb.append(hours+"h");
		}
		if(min > 0) {
			sb.append(min+"m");
		}
		if(seconds > 0) {
			sb.append(seconds+"s");
		}
		return sb.toString();
	}
	
	public static long stringToMs(String time) {
		int hours = 0;
		int min = 0;
		int sec = 0;
		String[] spl = time.split("[A-Za-z]+");
		if(spl.length > 3) {
			return 0;
		}
		for(int i = 1; i <= spl.length; i++) {
			if(isNumeric(spl[spl.length-i])) {
				if(i == 1) {
					sec = Integer.parseInt(spl[spl.length-i]);
				}else if(i == 2) {
					min = Integer.parseInt(spl[spl.length-i]);
				}else if(i == 3) {
					hours = Integer.parseInt(spl[spl.length-i]);
				}
			}
		}
		long fsec = hours*3600+min*60+sec;
		long ftime = fsec*1000;
		return ftime;
	}
	
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	
}
