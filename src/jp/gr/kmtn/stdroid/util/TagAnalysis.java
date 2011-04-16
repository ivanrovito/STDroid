package jp.gr.kmtn.stdroid.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagAnalysis
{

    //-----------------------------------------------------------------------------
    /**
     *  analysis the status to get index of HashTag
     */
    //-----------------------------------------------------------------------------
    public static ArrayList<String> getIndex(String status, String interval)
    {

        ArrayList<String> result = new ArrayList<String>();
        //		
        //		int from = 0;
        //		int to = 0;
        //		char[] message = status.toCharArray();
        //		int length = message.length;
        //		for(int i = 0; i < length; i++){
        //			
        //			if(message[i] == interval){
        //				from = i + 1;
        ////				System.out.println(from);
        //				result.add(String.valueOf(from));
        //				for(int j = i; j < length; j++){
        //					
        //					if(message[j] == ' '){
        //						to = j;
        ////						System.out.println(to);
        //						result.add(String.valueOf(to));
        //						break;
        //					}
        //					else if(message[j] == '\n'){
        //						to = j;
        ////						System.out.println(to);
        //						result.add(String.valueOf(to));
        //						break;
        //					}
        //					if(j == length - 1){
        ////						System.out.println(length);
        //						result.add(String.valueOf(length));
        //					}
        //
        //					if(interval == '@'){
        //						i = length;
        //					}		
        //
        //				}
        //				
        //			}
        //			
        //		}

        String s1 = status;//^,^.^ ^:^@^#^\uFE30-\uFFA0^\uFF00-\uFFFF^\u4E00-\u9FA5
        Pattern p = Pattern.compile(interval
                + "[^,^.^ ^:^@^#^[\u2E80-\u9FFF]+$^\uFE30-\uFFA0^\uFF00-\uFFFF^\u4E00-\u9FA5]*");
        Matcher m = p.matcher(s1);
        while (m.find())
        {
            System.out.print(m.start() + " " + m.end() + " " + " " + m.group()
                    + "\n");
            result.add(String.valueOf(m.start()));
            result.add(String.valueOf(m.end()));
            //			if(interval.equals("@")){
            //				break;
            //			}
        }

        return result;

    }

}
