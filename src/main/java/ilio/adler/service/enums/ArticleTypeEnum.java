package ilio.adler.service.enums;

import ilio.adler.service.support.Host;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author zqy
 * @version 1.0
 * @date 2020/1/19 17:14
 */
@RequiredArgsConstructor
public enum ArticleTypeEnum {
    Yamibo("bbs.yamibo.com", "page"),
    Tieba("tieba.baidu.com", "pn");

    @Getter private final String host;
    @Getter private final String pageFlag;
}
