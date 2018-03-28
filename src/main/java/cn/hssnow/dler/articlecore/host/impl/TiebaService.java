package cn.hssnow.dler.articlecore.host.impl;

import cn.hssnow.dler.articlecore.host.BaseService;
import cn.hssnow.dler.articlecore.host.support.Host;

@Host(name = "tieba", host = "tieba.baidu.com", page = "pn")
public class TiebaService extends BaseService {

	@Override
	protected void handlePageAndTitle() {
		
	}
	
}
