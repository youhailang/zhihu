package com.youhl.zhihu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "z_answer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ZAnswer extends BaseEntity {
  private static final long serialVersionUID = 9150791560470546655L;
  private Long id;
  private String url;
  private String fromUrl;

  private ZPeople people;
  private ZQuestion question;
  private String bio;
  private String content;
  private Integer agrees = 0;
  private Integer comments = 0;
  private Integer collections = 0;
  private Date dataTime;

  public ZAnswer() {}

  public ZAnswer(Long id, String url) {
    this.id = id;
    this.url = url;
  }

  @Id
  @Column(name = "id", unique = true, nullable = false)
  public Long getId() {
    return id;
  }

  @Column(nullable = false)
  public String getUrl() {
    return url;
  }

  @Column(name = "from_url", nullable = false)
  public String getFromUrl() {
    return fromUrl;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "people_id")
  public ZPeople getPeople() {
    return people;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id", nullable = false)
  public ZQuestion getQuestion() {
    return question;
  }

  @Column
  public String getBio() {
    return bio;
  }

  @Column(length = 20000)
  public String getContent() {
    return content;
  }

  @Column
  public Integer getAgrees() {
    return agrees;
  }

  @Column
  public Integer getComments() {
    return comments;
  }

  @Column
  public Integer getCollections() {
    return collections;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "data_time", length = 19)
  public Date getDataTime() {
    return dataTime;
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

  public void setPeople(ZPeople people) {
    this.people = people;
  }

  public void setQuestion(ZQuestion question) {
    this.question = question;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setAgrees(Integer agrees) {
    this.agrees = agrees;
  }

  public void setComments(Integer comments) {
    this.comments = comments;
  }

  public void setDataTime(Date dataTime) {
    this.dataTime = dataTime;
  }

  public void setFromUrl(String fromUrl) {
    this.fromUrl = fromUrl;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public void setCollections(Integer collections) {
    this.collections = collections;
  }
}
