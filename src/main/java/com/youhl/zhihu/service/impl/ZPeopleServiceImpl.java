package com.youhl.zhihu.service.impl;

import static com.youhl.zhihu.utils.Utils.coalesce;
import static com.youhl.zhihu.utils.Utils.content;
import static com.youhl.zhihu.utils.Utils.nextText;
import static com.youhl.zhihu.utils.Utils.parseInt;
import static com.youhl.zhihu.utils.Utils.parseLong;

import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youhl.zhihu.dao.GenericDao;
import com.youhl.zhihu.dao.ZPeopleActivityDao;
import com.youhl.zhihu.dao.ZPeopleDao;
import com.youhl.zhihu.entity.ZPeople;
import com.youhl.zhihu.entity.ZPeopleActivity;
import com.youhl.zhihu.service.ZPeopleService;

@Service("zPeopleService")
public class ZPeopleServiceImpl extends BaseServiceImpl<ZPeople, Long> implements ZPeopleService {
  @Autowired
  private ZPeopleDao zPeopleDao;
  @Autowired
  private ZPeopleActivityDao zPeopleActivityDao;

  @Override
  public GenericDao<ZPeople, Long> getMainDao() {
    return zPeopleDao;
  }

  @Override
  public void parse(String url, Document doc) throws Exception {
    ZPeople people = zPeopleDao.getByUrl(url);
    if (people == null) {
      people = new ZPeople(url);
    }
    people.setFromUrl(url);
    updateByDoc(doc, people);
    zPeopleDao.saveOrUpdate(people);
    parsePeopleActivity(url, doc,
        doc.select("div#zh-profile-activity-page-list>div.zm-profile-section-item"), people);
  }

  private void updateByDoc(Document doc, ZPeople people) {
    people.setName(coalesce(content(doc, "div.title-section>span.name"), people.getName()));
    people.setBio(coalesce(content(doc, "div.title-section>span.bio", "title"), people.getName()));
    people.setWeibo(coalesce(content(doc, "div.weibo-wrap>a", "href"), people.getWeibo()));
    people.setLocation(coalesce(
        content(doc, "div.editable-group>span.info-wrap>span.location", "title"),
        people.getLocation()));
    people.setBusiness(coalesce(
        content(doc, "div.editable-group>span.info-wrap>span.business", "title"),
        people.getBusiness()));
    people.setGender(coalesce(
        content(doc, "div.editable-group>span.info-wrap>span.gender>i", "class".split(","),
            "(?<=icon-profile-)([a-z]+)"), people.getGender()));
    people.setEmployment(coalesce(
        content(doc, "div.editable-group>span.info-wrap>span.employment", "title"),
        people.getEmployment()));
    people.setPosition(coalesce(
        content(doc, "div.editable-group>span.info-wrap>span.position", "title"),
        people.getPosition()));
    people.setEducation(coalesce(
        content(doc, "div.editable-group>span.info-wrap>span.education", "title"),
        people.getEducation()));
    people.setProfessional(coalesce(
        content(doc, "div.editable-group>span.info-wrap>span.education-extra", "title"),
        people.getProfessional()));
    people.setContent(coalesce(
        content(doc, "div.editable-group>span.info-wrap>span.description>span.content"),
        people.getContent()));

    people.setAgrees(parseInt(coalesce(content(doc, "span.zm-profile-header-user-agree>strong"),
        coalesce(people.getAgrees(), 0) + "")));
    people.setThanks(parseInt(coalesce(content(doc, "span.zm-profile-header-user-thanks>strong"),
        coalesce(people.getThanks(), 0) + "")));
    people.setAsks(parseInt(coalesce(
        content(doc, "div.profile-navbar>a.item[href~=.*/asks$]>span.num"),
        coalesce(people.getAsks(), 0) + "")));
    people.setAnswers(parseInt(coalesce(
        content(doc, "div.profile-navbar>a.item[href~=.*/answers$]>span.num"),
        coalesce(people.getAnswers(), 0) + "")));
    people.setPosts(parseInt(coalesce(
        content(doc, "div.profile-navbar>a.item[href~=.*/posts$]>span.num"),
        coalesce(people.getPosts(), 0) + "")));
    people.setCollections(parseInt(coalesce(
        content(doc, "div.profile-navbar>a.item[href~=.*/collections$]>span.num"),
        coalesce(people.getCollections(), 0) + "")));
    people.setLogs(parseInt(coalesce(
        content(doc, "div.profile-navbar>a.item[href~=.*/logs$]>span.num"),
        coalesce(people.getLogs(), 0) + "")));

    people.setFollowees(parseInt(coalesce(
        content(doc, "div.zm-profile-side-following>a.item[href~=.*/followees$]>strong"),
        coalesce(people.getFollowees(), 0) + "")));
    people.setFollowers(parseInt(coalesce(
        content(doc, "div.zm-profile-side-following>a.item[href~=.*/followers$]>strong"),
        coalesce(people.getFollowers(), 0) + "")));
    people.setColumns(parseInt(coalesce(
        content(doc, "div.zm-profile-side-section-title>a[href~=.*/columns/followed$]>strong",
            null, "^\\d+(?=.*)"), coalesce(people.getColumns(), 0) + "")));
    people.setTopics(parseInt(coalesce(
        content(doc, "div.zm-profile-side-section-title>a[href~=.*/topics$]>strong", null,
            "^\\d+(?=.*)"), coalesce(people.getTopics(), 0) + "")));
    people.setVisits(parseInt(coalesce(
        content(doc,
            "div.zm-profile-side-section>div.zm-side-section-inner>span.zg-gray-normal>strong"),
        coalesce(people.getVisits(), 0) + "")));
  }

  // 解析关注动态
  private void parsePeopleActivity(String url, Document doc, Elements root, ZPeople people) {
    for (Element ele : root) {
      Long dataTime = parseLong(coalesce(ele.attr("data-time"), "0"));
      Element main = ele.select("div.zm-profile-section-main").first();
      if (main != null && dataTime > 0) {
        Date date = new Date(dataTime * 1000);
        ZPeopleActivity peopleActivity = zPeopleActivityDao.getByPeopleAndDataTime(people, date);
        if (peopleActivity == null) {
          peopleActivity = new ZPeopleActivity(people, date);
        }
        peopleActivity.setFromUrl(url);
        String action = nextText(main, "a.zg-link", 0, null);
        if (action == null) {
          continue;
        }
        peopleActivity.setAction(action);
        if (action.equals("关注了问题")) {
          parseQuestionLink(main, peopleActivity);
        } else if (action.equals("赞同了回答")) {
          parseQuestionLink(main, peopleActivity);
          parseAnswer(ele, peopleActivity);
        } else if (action.equals("关注了话题")) {
          parseTopicLink(main, peopleActivity);
        } else if (action.equals("回答了问题")) {
          parseQuestionLink(main, peopleActivity);
          parseAnswer(ele, peopleActivity);
        } else if (action.equals("关注了圆桌")) {
          parseRoundTableLink(main, peopleActivity);
        } else if (action.equals("关注了专栏")) {
          parseColumnLink(main, peopleActivity);
        } else if (action.equals("关注了收藏夹")) {
          parseCollection(main, peopleActivity);
        } else if (action.equals("提了一个问题")) {
          parseQuestionLink(main, peopleActivity);
        } else if (action.equals("赞同了")) {
          parseAgreeZhuanlanPaper(main, peopleActivity);
          peopleActivity
              .setSummary(coalesce(content(ele, "div.zm-item-rich-text>div>div.zh-summary"),
                  peopleActivity.getSummary()));
        } else if (action.equals("在")) {
          peopleActivity.setAuthor(coalesce(people.getName(), peopleActivity.getAuthor()));
          parseSendZhuanlanPaper(main, peopleActivity);
        }
        zPeopleActivityDao.saveOrUpdate(peopleActivity);
      }
    }
  }



  private void parseSendZhuanlanPaper(Element main, ZPeopleActivity peopleActivity) {
    peopleActivity.setUrl(coalesce(content(main, "a.post-link", "abs:href"),
        peopleActivity.getUrl()));
    peopleActivity.setTitle(coalesce(content(main, "a.post-link"), peopleActivity.getTitle()));
    String nextText = coalesce(nextText(main, "a.column_link"), "");
    if (nextText.length() >= 5) {
      peopleActivity.setAction(peopleActivity.getAction()
          + nextText.substring(nextText.length() - 5));
    }
  }

  private void parseAgreeZhuanlanPaper(Element main, ZPeopleActivity peopleActivity) {
    peopleActivity.setUrl(coalesce(content(main, "a.post-link", "abs:href"),
        peopleActivity.getUrl()));
    peopleActivity.setTitle(coalesce(content(main, "a.post-link"), peopleActivity.getTitle()));
    peopleActivity.setAuthor(coalesce(content(main, "a.column_link"), peopleActivity.getAuthor()));
    String nextText = coalesce(nextText(main, "a.column_link"), "");
    if (nextText.length() >= 2) {
      peopleActivity.setAction(peopleActivity.getAction()
          + nextText.substring(nextText.length() - 2));
    }
  }

  private void parseAnswer(Element ele, ZPeopleActivity peopleActivity) {
    peopleActivity.setAuthor(coalesce(
        content(ele, "h3.zm-item-answer-author-wrap>a[href~=.*/people/.*]", 1, null, null),
        peopleActivity.getAuthor()));
    if (peopleActivity.getAuthor() == null) {
      peopleActivity.setAuthor(coalesce(
          nextText(ele, "h3.zm-item-answer-author-wrap>a.zm-item-link-avatar"),
          peopleActivity.getAuthor()));
    }
    peopleActivity.setSummary(coalesce(content(ele, "div.zm-item-rich-text>div.zh-summary"),
        peopleActivity.getSummary()));
  }

  private void parseTopicLink(Element main, ZPeopleActivity peopleActivity) {
    peopleActivity.setUrl(coalesce(content(main, "a.topic-link", "abs:href"),
        peopleActivity.getUrl()));
    peopleActivity.setTitle(coalesce(content(main, "a.topic-link", "title"),
        peopleActivity.getTitle()));
  }

  private void parseQuestionLink(Element main, ZPeopleActivity peopleActivity) {
    peopleActivity.setTitle(coalesce(content(main, "a.question_link"), peopleActivity.getTitle()));
    peopleActivity.setUrl(coalesce(content(main, "a.question_link", "abs:href"),
        peopleActivity.getUrl()));
  }

  private void parseRoundTableLink(Element main, ZPeopleActivity peopleActivity) {
    peopleActivity
        .setTitle(coalesce(content(main, "a.roundtable_link"), peopleActivity.getTitle()));
    peopleActivity.setUrl(coalesce(content(main, "a.roundtable_link", "abs:href"),
        peopleActivity.getUrl()));
  }

  private void parseColumnLink(Element main, ZPeopleActivity peopleActivity) {
    peopleActivity.setTitle(coalesce(content(main, "a.column_link"), peopleActivity.getTitle()));
    peopleActivity.setUrl(coalesce(content(main, "a.column_link", "abs:href"),
        peopleActivity.getUrl()));
  }

  private void parseCollection(Element main, ZPeopleActivity peopleActivity) {
    peopleActivity.setTitle(coalesce(content(main, "a[href~=.*/collection/.*]"),
        peopleActivity.getTitle()));
    peopleActivity.setUrl(coalesce(content(main, "a[href~=.*/collection/.*]", "abs:href"),
        peopleActivity.getUrl()));
  }
}
