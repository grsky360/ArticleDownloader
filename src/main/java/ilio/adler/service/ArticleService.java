package ilio.adler.service;

import ilio.adler.service.data.Task;
import ilio.adler.service.enums.ArticleTypeEnum;

/**
 * @author zqy
 * @version 1.0
 * @date 2020/1/19 17:10
 */
public interface ArticleService {

    ArticleTypeEnum getArticleType();

    boolean submit(Task task);
}
