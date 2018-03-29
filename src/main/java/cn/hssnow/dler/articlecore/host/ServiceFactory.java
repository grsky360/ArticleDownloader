package cn.hssnow.dler.articlecore.host;

public interface ServiceFactory<T> {
	T judge(String path, String filename, String url);
}
