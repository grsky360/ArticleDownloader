package cn.hssnow.dler.articlecore.host.impl;

import cn.hssnow.dler.articlecore.host.BaseService;
import cn.hssnow.dler.articlecore.host.support.Host;

@Host(name = "yamibo", host = "bbs.yamibo.com", page = "page")
public class YamiboService extends BaseService {

	@Override
	protected void handlePageAndTitle() {
		
	}
	
}
