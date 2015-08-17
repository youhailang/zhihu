package com.youhl.zhihu.service.impl;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.youhl.zhihu.entity.SeedUrl;
import com.youhl.zhihu.service.DocumentService;
import com.youhl.zhihu.service.SeedUrlService;
import com.youhl.zhihu.service.SpiderService;
import com.youhl.zhihu.utils.Utils;

@Service("spiderService")
public class SpiderServiceImpl extends ApplicationObjectSupport implements SpiderService {
  private static final Logger LOGGER = Logger.getLogger(SpiderServiceImpl.class);
  private static final int THREADS = 50;
  private static int step = 0;
  private static int submitPages = 0;
  private static int donePages = 0;
  private static DecimalFormat df = new DecimalFormat("#0.00%");
  @Autowired
  private SeedUrlService seedUrlService;

  @Autowired
  private CloseableHttpClient httpClient;

  private List<SeedUrl> cachedList = Collections.synchronizedList(new LinkedList<SeedUrl>());

  @Override
  public void start(int stopStep, int stopPages) {
    seedUrlService.init();
    while ((stopStep <= 0 || step < stopStep) && (stopPages <= 0 || submitPages < stopPages)
        && seedUrlService.getValidSeedUrl().size() > 0) {
      step++;
      ThreadFactory threadFactory =
          new ThreadFactoryBuilder().setNameFormat("Cycle[" + step + "]-%d").setDaemon(true)
              .build();
      ExecutorService pool = Executors.newFixedThreadPool(THREADS, threadFactory);
      donePages = 0;
      for (Entry<String, SeedUrl> entry : seedUrlService.getValidSeedUrl().entrySet()) {
        pool.execute(new MyThread(entry.getKey(), entry.getValue()));
        if (!(stopPages <= 0 || submitPages < stopPages)) {
          break;
        }
      }
      try {
        pool.shutdown();
        while (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
          updateSeeds(50);
        }
        updateSeeds(-1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      seedUrlService.combine();
    }
  }

  private void updateSeeds(int batch) {
    if (cachedList.size() > 0 && (batch < 0 || cachedList.size() > batch)) {
      List<SeedUrl> peddings = null;
      synchronized (this) {
        peddings = cachedList;
        cachedList = Collections.synchronizedList(new LinkedList<SeedUrl>());
      }
      for (SeedUrl seedUrl : peddings) {
        seedUrlService.update(seedUrl);
      }
    }
  }

  class MyThread extends Thread {
    protected String url;
    protected SeedUrl seedUrl;
    protected long[] timestamps = new long[4];

    public MyThread(String url, SeedUrl seedUrl) {
      this.url = url;
      this.seedUrl = seedUrl;
      submitPages++;
    }

    @Override
    public void run() {
      Arrays.fill(timestamps, System.currentTimeMillis());
      try {
        Document doc = Utils.parseDocument(httpClient, url);
        timestamps[1] = System.currentTimeMillis();
        if (doc != null) {
          parse(doc);
          timestamps[2] = System.currentTimeMillis();
          seed(doc);
          timestamps[3] = System.currentTimeMillis();
          success();
        } else {
          failure();
        }
      } catch (Exception e) {
        e.printStackTrace();
        failure();
      }
      cachedList.add(seedUrl);
      donePages++;
      LOGGER.info(String.format("[第%d轮] %s,%d/%d %s %s", step,
          df.format((donePages + 0D) / submitPages), donePages, submitPages, serviceInfo, url));
    }

    private String serviceInfo;

    void parse(Document doc) {
      if (StringUtils.isEmpty(seedUrl.getRegexUrl().getServiceName())) {
        return;
      }
      String[] ss = seedUrl.getRegexUrl().getServiceName().split(",");
      boolean[] flags = new boolean[ss.length];
      Map<String, DocumentService<?, ?>> map = Maps.newLinkedHashMap();
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < ss.length; i++) {
        String serviceName = ss[i];
        Object service = getApplicationContext().getBean(serviceName);
        flags[i] = service != null && service instanceof DocumentService<?, ?>;
        if (flags[i]) {
          map.put(serviceName, (DocumentService<?, ?>) service);
          sb.append(serviceName);
        } else {
          sb.append("(").append(serviceName).append(")");
        }
        sb.append(",");
      }
      for (DocumentService<?, ?> service : map.values()) {
        try {
          service.parse(url, doc);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      serviceInfo = sb.length() > 0 ? sb.substring(0, sb.length() - 1) : null;
    }

    void seed(Document doc) {
      try {
        seedUrlService.parse(url, doc);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }


    void success() {
      seedUrl.setSuccess(seedUrl.getSuccess() + 1);
      seedUrl.setDownUsetime(Math.max(timestamps[1] - timestamps[0], 0));
      seedUrl.setParseUsetime(Math.max(timestamps[2] - timestamps[1], 0));
      seedUrl.setSeedUsetime(Math.max(timestamps[3] - timestamps[2], 0));
    }

    void failure() {
      seedUrl.setFailure(seedUrl.getFailure() + 1);
    }
  }
}
