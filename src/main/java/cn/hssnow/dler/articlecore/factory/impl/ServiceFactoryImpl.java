package cn.hssnow.dler.articlecore.factory.impl;

import cn.hssnow.dler.articlecore.factory.ServiceFactory;
import cn.hssnow.dler.articlecore.service.BaseService;
import cn.hssnow.dler.articlecore.service.support.Host;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;

public class ServiceFactoryImpl implements ServiceFactory<BaseService> {
	private static final List<Class<? extends BaseService>> SERVICES = new ArrayList<>();

	static {
		Reflections reflections = new Reflections("cn.hssnow.dler.articlecore.service.impl");
		SERVICES.addAll(reflections.getSubTypesOf(BaseService.class));
	}
	
	private BaseService newInstance(String url) {
		if (url == null || "".equals(url.trim())) return null;
		for (Class<? extends BaseService> c : SERVICES) {
			if (url.contains(c.getAnnotation(Host.class).host())) {
				try {
					return c.newInstance();
				} catch (Exception e) {
					return null;
				}
			}
		}
		return null;
	}
	
	@Override
	public BaseService judge(String path, String filename, String url) {
		BaseService service = newInstance(url);
		
		service.build(path, filename, url);
		
		return service;
	}

}
