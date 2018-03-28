package cn.hssnow.dler.articlecore.host;

public interface ServiceFactory<T> {
	T judge(String url);
}
