//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.monke.monkeybook.model.impl;

import com.monke.monkeybook.bean.BookContentBean;
import com.monke.monkeybook.bean.BookShelfBean;
import com.monke.monkeybook.bean.BookSourceBean;
import com.monke.monkeybook.bean.ChapterListBean;
import com.monke.monkeybook.bean.SearchBookBean;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

public interface IWebBookModel {
    /**
     * 网络请求并解析书籍信息
     */
    Observable<BookShelfBean> getBookInfo(BookShelfBean bookShelfBean);

    /**
     * 网络解析图书目录
     */
    Observable<BookShelfBean> getChapterList(BookShelfBean bookShelfBean);

    /**
     * 章节缓存
     */
    Observable<BookContentBean> getBookContent(Scheduler scheduler, ChapterListBean chapter);

    /**
     * 其他站点资源整合搜索
     */
    Observable<List<SearchBookBean>> searchOtherBook(String content, int page, String tag);

    /**
     * 发现
     */
    Observable<List<SearchBookBean>> findBook(String url, int page, String tag);
}
