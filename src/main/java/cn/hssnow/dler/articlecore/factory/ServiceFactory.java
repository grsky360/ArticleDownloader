package cn.hssnow.dler.articlecore.factory;

public interface ServiceFactory<T> {
	T judge(String path, String filename, String url);
}
