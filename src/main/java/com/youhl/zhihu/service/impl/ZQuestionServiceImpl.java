package com.youhl.zhihu.service.impl;

import static com.youhl.zhihu.utils.Utils.coalesce;
import static com.youhl.zhihu.utils.Utils.content;
import static com.youhl.zhihu.utils.Utils.contents;
import static com.youhl.zhihu.utils.Utils.nextText;
import static com.youhl.zhihu.utils.Utils.parseInt;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youhl.zhihu.dao.GenericDao;
import com.youhl.zhihu.dao.ZQuestionDao;
import com.youhl.zhihu.entity.ZQuestion;
import com.youhl.zhihu.service.ZQuestionService;
import com.youhl.zhihu.utils.Utils;

@Service("zQuestionService")
public class ZQuestionServiceImpl extends BaseServiceImpl<ZQuestion, Long> implements
    ZQuestionService {
  @Autowired
  private ZQuestionDao zQuestionDao;

  @Override
  public GenericDao<ZQuestion, Long> getMainDao() {
    return zQuestionDao;
  }

  @Override
  public void parse(String url, Document doc) throws Exception {
    Long questionId = Long.parseLong(Utils.extract(url, Pattern.compile("(?<=question/)\\d+")));
    ZQuestion question = zQuestionDao.get(questionId);
    if (question == null) {
      question = new ZQuestion(questionId, url);
      question.setLabels(StringUtils.join(
          contents(doc, "div.zm-tag-editor-labels>a.zm-item-tag", null, null), " "));
    }
    question.setFromUrl(url);
    question.setTitle(coalesce(content(doc, "div#zh-question-title>h2.zm-item-title"),
        question.getTitle()));
    question.setDetail(coalesce(content(doc, "div#zh-question-detail>div.zm-editable-content"),
        question.getDetail()));

    question.setAnswers(parseInt(coalesce(content(doc, "h3#zh-question-answer-num", "data-num"),
        coalesce(question.getAnswers(), 0) + "")));
    question.setComments(parseInt(coalesce(
        nextText(doc, "div#zh-question-meta-wrap>i.z-icon-comment", 0, "(\\d+)(?=.*)"),
        coalesce(question.getComments(), 0) + "")));
    question.setFollows(parseInt(coalesce(
        nextText(doc, "div#zh-question-side-header-wrap>button.zg-follow", 0, "(\\d+)(?=.*)"),
        coalesce(question.getFollows(), 0) + "")));
    zQuestionDao.saveOrUpdate(question);
  }
}
