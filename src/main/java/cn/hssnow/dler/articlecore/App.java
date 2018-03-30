package cn.hssnow.dler.articlecore;

import cn.hssnow.dler.articlecore.factory.impl.ServiceFactoryImpl;
import cn.hssnow.dler.articlecore.service.BaseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class App implements CommandLineRunner {
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    @Override
    public void run(String... args) throws IOException {
		final String demo2 = "https://bbs.yamibo.com/forum.php?mod=viewthread&tid=182132&page=1&authorid=11241";

		BaseService service = new ServiceFactoryImpl().judge("./", "a", demo2);
		
		
    }
    
}
