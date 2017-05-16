package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RE {
 public static void main(String args[]){
	 String  line    = "0000166c <_ZN7_JNIEnv12NewStringUTFEPKc-0x24>:";
	 Pattern pattern = Pattern.compile("([0-9a-z]+)\\s<(\\S+?)>:");
	 Matcher matcher = pattern.matcher(line);
	 if(matcher.matches()){
		 for(int i=0;i<=matcher.groupCount();i++){
			 System.out.println("group "+i+":"+matcher.group(i));
        }
     }
 }
}
