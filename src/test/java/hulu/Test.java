package hulu;

import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;

public class Test {
	public static void main(String[] args) {
		Log log = LogFactory.get();
		log.debug("aaa");
		log.info("aaa");
		log.warn("aaa");
		log.error("aaa");
		
//		for(int i = 0; i < 1000; i++){
//			final int a = i;
//			ThreadUtil.execute(new Runnable(){
//				
//				@Override
//				public void run() {
//					LogFactory.get();
//				}
//			});
//			
//		}
	}
}
