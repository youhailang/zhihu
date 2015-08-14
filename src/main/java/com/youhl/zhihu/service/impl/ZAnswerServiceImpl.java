package com.youhl.zhihu.service.impl;

import static com.youhl.zhihu.utils.Utils.coalesce;
import static com.youhl.zhihu.utils.Utils.content;
import static com.youhl.zhihu.utils.Utils.nextText;
import static com.youhl.zhihu.utils.Utils.parseInt;
import static com.youhl.zhihu.utils.Utils.parseLong;

import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.youhl.zhihu.dao.GenericDao;
import com.youhl.zhihu.dao.ZAnswerDao;
import com.youhl.zhihu.dao.ZPeopleDao;
import com.youhl.zhihu.dao.ZQuestionDao;
import com.youhl.zhihu.entity.ZAnswer;
import com.youhl.zhihu.entity.ZPeople;
import com.youhl.zhihu.entity.ZQuestion;
import com.youhl.zhihu.service.ZAnswerService;
import com.youhl.zhihu.utils.Utils;

@Service("zAnswerService")
public class ZAnswerServiceImpl extends BaseServiceImpl<ZAnswer, Long> implements ZAnswerService {
  @Autowired
  private ZAnswerDao zAnswerDao;
  @Autowired
  private ZQuestionDao zQuestionDao;
  @Autowired
  private ZPeopleDao zPeopleDao;

  @Override
  public GenericDao<ZAnswer, Long> getMainDao() {
    return zAnswerDao;
  }

  @Override
  public void parse(String url, Document doc) throws Exception {
    try {
      Long questionId = parseLong(Utils.extract(url, Pattern.compile("(?<=question/)\\d+")));
      if (questionId == null)
        return;
      ZQuestion question = zQuestionDao.get(questionId);
      if (question == null) {
        question = new ZQuestion(questionId, url);
        question.setFromUrl(url);
        zQuestionDao.save(question);
      }
      Elements eles = doc.select("div#zh-question-answer-wrap>div.zm-item-answer");
      Map<Long, Integer> collections = Maps.newTreeMap();
      if (url.contains("answer")) {
        Element coll = doc.select("div.zu-main-sidebar>div.zm-side-section-inner>h3>a").first();
        if (coll != null) {
          collections.put(
              parseLong(Utils.extract(coll.attr("abs:href"), Pattern.compile("(?<=answer/)\\d+"))),
              parseInt(coll.text()));
        }
      }
      for (Element ele : eles) {
        String href = content(ele, "span.answer-date-link-wrap>a.answer-date-link", "abs:href");
        Long answerId = parseLong(Utils.extract(href, Pattern.compile("(?<=answer/)\\d+")));
        if (answerId == null) {
          continue;
        }
        ZAnswer answer = zAnswerDao.get(answerId);
        if (answer == null) {
          answer = new ZAnswer(answerId, href);
          answer.setQuestion(question);
        }
        answer.setFromUrl(url);
        String peopleHomeUrl =
            content(ele, "h3.zm-item-answer-author-wrap>a[href~=.*/people/[^#^/]*$]", "abs:href");
        if (peopleHomeUrl != null) {
          ZPeople people = zPeopleDao.getByUrl(peopleHomeUrl);
          if (people == null) {
            people = new ZPeople(peopleHomeUrl);
            people.setFromUrl(url);
            zPeopleDao.save(people);
          }
          answer.setPeople(people);
        }
        answer.setBio(coalesce(content(ele, "strong.zu-question-my-bio"), answer.getBio()));
        Long dataTime = parseLong(coalesce(ele.attr("data-created"), "0"));
        answer.setDataTime(dataTime == 0 ? answer.getDataTime() : new Date(dataTime * 1000));
        Element content = ele.select("div.zm-item-rich-text>div.zm-editable-content").first();
        if (content != null) {
          answer.setContent(content.html());
        }
        answer.setComments(parseInt(coalesce(nextText(ele, "i.z-icon-comment", 0, "(\\d+)(?=.*)"),
            coalesce(answer.getComments(), 0) + "")));
        answer.setAgrees(parseInt(coalesce(content(ele, "div.zm-votebar>button.up>span.count"),
            coalesce(answer.getAgrees(), 0) + "")));
        answer.setCollections(coalesce(collections.get(answer.getId()), answer.getCollections()));
        zAnswerDao.saveOrUpdate(answer);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
