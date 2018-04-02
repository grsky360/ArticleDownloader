package cn.hssnow.dler.articlecore.factory.impl;

import cn.hssnow.dler.articlecore.factory.ServiceFactory;
import cn.hssnow.dler.articlecore.service.BaseService;
import cn.hssnow.dler.articlecore.service.support.Host;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ServiceFactoryImpl implements ServiceFactory<BaseService> {
	private static final List<Class<? extends BaseService>> SERVICES = new ArrayList<>();

	static {
		String path = ServiceFactoryImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		List<String> classes = new ArrayList<>();
		System.out.println(path);
		if (path.charAt(path.length() - 1) == '/') {
			String[] files = new File(path + "cn/hssnow/dler/articlecore/service/impl/").list();
			for (String file : files != null ? files : new String[0]) {
				if (file.endsWith(".class")) {
					classes.add(file.replace(".class", ""));
				}
			}
		} else {
			try {
				JarFile jar = new JarFile(new File(path));
				Enumeration<JarEntry> entities = jar.entries();
				while (entities.hasMoreElements()) {
					JarEntry file = entities.nextElement();
					String name = file.getName();
					if (!name.startsWith("cn/hssnow/dler/articlecore/service/impl/") || !name.endsWith(".class")) {
						continue;
					}

					classes.add(name.substring(name.lastIndexOf('/') + 1, name.lastIndexOf('.')));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		classes.forEach(c -> {
			try {
				Class clazz = Class.forName("cn.hssnow.dler.articlecore.service.impl." + c).asSubclass(BaseService.class);
				SERVICES.add(clazz);
			} catch (Exception ignored) {
			}
		});
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

	public static void main(String[] args) {
		SERVICES.forEach(c -> {
			System.out.println(c.getName());
		});
	}


}
