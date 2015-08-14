package com.youhl.zhihu.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "z_seed_url")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SeedUrl extends BaseEntity {
  private static final long serialVersionUID = 9150791560470546655L;
  private Long id;
  private String url;
  private Long success = 0L;
  private Long failure = 0L;
  private Integer level = 1;
  private Integer flag = 1;

  private Long downUsetime = 0L;
  private Long parseUsetime = 0L;
  private Long seedUsetime = 0L;

  private SeedUrl parent;
  private List<SeedUrl> childrens;

  private RegexUrl regexUrl;

  public SeedUrl() {}

  public SeedUrl(String url) {
    super();
    this.url = url;
  }

  public SeedUrl(String url, SeedUrl parent) {
    super();
    this.url = url;
    if (parent != null) {
      this.parent = parent;
      this.level = parent.level + 1;
      // if (parent.childrens != null) {
      // parent.childrens.add(this);
      // }
    }
  }

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long getId() {
    return id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pid")
  public SeedUrl getParent() {
    return parent;
  }

  @OneToMany(targetEntity = SeedUrl.class, mappedBy = "parent", cascade = {CascadeType.ALL})
  @Fetch(FetchMode.SUBSELECT)
  public List<SeedUrl> getChildrens() {
    return childrens;
  }

  @Column(name = "url", nullable = false)
  public String getUrl() {
    return url;
  }


  @Column(name = "success")
  public Long getSuccess() {
    return success;
  }

  @Column(name = "failure")
  public Long getFailure() {
    return failure;
  }

  @Column(name = "level")
  public Integer getLevel() {
    return level;
  }

  @Column(name = "flag")
  public Integer getFlag() {
    return flag;
  }

  @Column(name = "down_usetime")
  public Long getDownUsetime() {
    return downUsetime;
  }

  @Column(name = "seed_usetime")
  public Long getSeedUsetime() {
    return seedUsetime;
  }

  @Column(name = "parse_usetime")
  public Long getParseUsetime() {
    return parseUsetime;
  }

  @Transient
  public RegexUrl getRegexUrl() {
    return regexUrl;
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

  public void setUrl(String url) {
    this.url = url;
  }

  public void setSuccess(Long success) {
    this.success = success;
  }

  public void setFailure(Long failure) {
    this.failure = failure;
  }

  public void setFlag(Integer flag) {
    this.flag = flag;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setParent(SeedUrl parent) {
    this.parent = parent;
  }

  public void setChildrens(List<SeedUrl> childrens) {
    this.childrens = childrens;
  }

  public void setDownUsetime(Long downUsetime) {
    this.downUsetime = downUsetime;
  }

  public void setSeedUsetime(Long seedUsetime) {
    this.seedUsetime = seedUsetime;
  }

  public void setParseUsetime(Long parseUsetime) {
    this.parseUsetime = parseUsetime;
  }


  public void setRegexUrl(RegexUrl regexUrl) {
    this.regexUrl = regexUrl;
  }
}
