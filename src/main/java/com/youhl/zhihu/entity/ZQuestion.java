package com.youhl.zhihu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "z_question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ZQuestion extends BaseEntity {
  private static final long serialVersionUID = 9150791560470546655L;
  private Long id;
  private String url;
  private String fromUrl;

  private String labels;
  private String title;
  private String detail;

  private Integer answers = 0;
  private Integer follows = 0;
  private Integer comments = 0;

  public ZQuestion() {}

  public ZQuestion(Long id, String url) {
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

  @Column
  public String getLabels() {
    return labels;
  }

  @Column
  public String getTitle() {
    return title;
  }

  @Column(length = 2000)
  public String getDetail() {
    return detail;
  }

  @Column
  public Integer getAnswers() {
    return answers;
  }

  @Column
  public Integer getFollows() {
    return follows;
  }

  @Column
  public Integer getComments() {
    return comments;
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

  public void setLabels(String labels) {
    this.labels = labels;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public void setAnswers(Integer answers) {
    this.answers = answers;
  }

  public void setFollows(Integer follows) {
    this.follows = follows;
  }


  public void setComments(Integer comments) {
    this.comments = comments;
  }

  public void setFromUrl(String fromUrl) {
    this.fromUrl = fromUrl;
  }

}
