package ilio.adler.service.data;

import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.TypeReference;
import ilio.adler.service.enums.ArticleTypeEnum;
import lombok.Data;

import java.io.FileReader;
import java.util.Collections;
import java.util.List;

@Data
public class Task {
	private String path;
	private String name;
	private String url;

	public ArticleTypeEnum getArticleType() {
		for (ArticleTypeEnum articleType : ArticleTypeEnum.values()) {
			if (url.contains(articleType.getHost())) {
				return articleType;
			}
		}
		return null;
	}
}
