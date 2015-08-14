package com.youhl.zhihu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "z_people")
public class ZPeople extends BaseEntity {
  private static final long serialVersionUID = 9150791560470546655L;
  private Long id;
  private String url;
  private String fromUrl;
  private String name;
  private String bio;
  private String content;
  private String weibo;
  private String location;
  private String business;
  private String gender;
  private String employment;
  private String position;
  private String education;
  private String professional;

  private Integer agrees = 0;
  private Integer thanks = 0;
  private Integer asks = 0;
  private Integer answers = 0;
  private Integer posts = 0;
  private Integer collections = 0;
  private Integer logs = 0;
  private Integer followees = 0;
  private Integer followers = 0;
  private Integer columns = 0;
  private Integer topics = 0;
  private Integer visits = 0;


  public ZPeople() {}

  public ZPeople(String url) {
    this.url = url;
  }

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long getId() {
    return id;
  }

  @Column(name = "url", nullable = false)
  public String getUrl() {
    return url;
  }

  @Column(name = "from_url", nullable = false)
  public String getFromUrl() {
    return fromUrl;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  @Column(name = "bio")
  public String getBio() {
    return bio;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  @Column(name = "weibo")
  public String getWeibo() {
    return weibo;
  }

  @Column(name = "location")
  public String getLocation() {
    return location;
  }

  @Column(name = "business")
  public String getBusiness() {
    return business;
  }

  @Column(name = "gender")
  public String getGender() {
    return gender;
  }

  @Column(name = "employment")
  public String getEmployment() {
    return employment;
  }

  @Column(name = "position")
  public String getPosition() {
    return position;
  }

  @Column(name = "education")
  public String getEducation() {
    return education;
  }

  @Column(name = "professional")
  public String getProfessional() {
    return professional;
  }

  @Column
  public Integer getAgrees() {
    return agrees;
  }

  @Column
  public Integer getThanks() {
    return thanks;
  }

  @Column
  public Integer getAsks() {
    return asks;
  }

  @Column
  public Integer getAnswers() {
    return answers;
  }

  @Column
  public Integer getPosts() {
    return posts;
  }

  @Column
  public Integer getCollections() {
    return collections;
  }

  @Column
  public Integer getLogs() {
    return logs;
  }

  @Column
  public Integer getFollowees() {
    return followees;
  }

  @Column
  public Integer getFollowers() {
    return followers;
  }

  @Column
  public Integer getColumns() {
    return columns;
  }

  @Column
  public Integer getTopics() {
    return topics;
  }

  @Column
  public Integer getVisits() {
    return visits;
  }


  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "update_time", length = 19)
  public Date getUpdateTime() {
    return updateTime;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_time", length = 19, updatable = false)
  public Date getCreateTime() {
    return createTime;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setWeibo(String weibo) {
    this.weibo = weibo;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public void setBusiness(String business) {
    this.business = business;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public void setEmployment(String employment) {
    this.employment = employment;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public void setEducation(String education) {
    this.education = education;
  }

  public void setProfessional(String professional) {
    this.professional = professional;
  }

  public void setAgrees(Integer agrees) {
    this.agrees = agrees;
  }

  public void setThanks(Integer thanks) {
    this.thanks = thanks;
  }

  public void setAsks(Integer asks) {
    this.asks = asks;
  }

  public void setAnswers(Integer answers) {
    this.answers = answers;
  }

  public void setPosts(Integer posts) {
    this.posts = posts;
  }

  public void setCollections(Integer collections) {
    this.collections = collections;
  }

  public void setLogs(Integer logs) {
    this.logs = logs;
  }

  public void setFollowees(Integer followees) {
    this.followees = followees;
  }

  public void setFollowers(Integer followers) {
    this.followers = followers;
  }

  public void setColumns(Integer columns) {
    this.columns = columns;
  }

  public void setTopics(Integer topics) {
    this.topics = topics;
  }

  public void setVisits(Integer visits) {
    this.visits = visits;
  }

  public void setFromUrl(String fromUrl) {
    this.fromUrl = fromUrl;
  }
}
