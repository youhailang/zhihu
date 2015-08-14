package com.youhl.zhihu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "z_people_activity")
public class ZPeopleActivity extends BaseEntity {
  private static final long serialVersionUID = 9150791560470546655L;
  private Long id;
  private String url;
  private String fromUrl;

  private ZPeople people;
  private String action;
  private String title;
  private String author;
  private String summary;
  private Date dataTime;

  public ZPeopleActivity() {}

  public ZPeopleActivity(ZPeople people, Date dataTime) {
    this.people = people;
    this.dataTime = dataTime;
  }

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long getId() {
    return id;
  }

  @Column(name = "url")
  public String getUrl() {
    return url;
  }

  @Column(name = "from_url", nullable = false)
  public String getFromUrl() {
    return fromUrl;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "people_id", nullable = false)
  public ZPeople getPeople() {
    return people;
  }

  @Column
  public String getAction() {
    return action;
  }

  @Column
  public String getTitle() {
    return title;
  }

  @Column(length = 2000)
  public String getSummary() {
    return summary;
  }

  @Column
  public String getAuthor() {
    return author;
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

  public void setAction(String action) {
    this.action = action;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public void setDataTime(Date dataTime) {
    this.dataTime = dataTime;
  }

  public void setFromUrl(String fromUrl) {
    this.fromUrl = fromUrl;
  }
}
