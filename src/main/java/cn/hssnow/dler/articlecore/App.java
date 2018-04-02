package cn.hssnow.dler.articlecore;

import cn.hssnow.dler.articlecore.factory.impl.ServiceFactoryImpl;
import cn.hssnow.dler.articlecore.service.BaseService;

public class App {
    
    public static void main(String[] args) {
		final String demo1 = "https://tieba.baidu.com/p/4649121637?see_lz=1";
		final String demo2 = "https://bbs.yamibo.com/forum.php?mod=viewthread&tid=182132&page=1&authorid=11241";

		BaseService service = new ServiceFactoryImpl().judge(
				"./",
				"a",
				demo2);

		System.out.println(service.start());
    }
    
}
