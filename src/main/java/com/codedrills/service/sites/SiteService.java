package com.codedrills.service.sites;

import com.codedrills.model.Handle;
import com.codedrills.model.Problem;
import com.codedrills.model.Site;
import com.codedrills.model.stats.UserStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SiteService {
  private final List<SiteInterface> siteInterfaces;
  private final Map<Site, SiteInterface> interfaceMap;

  @Autowired
  public SiteService(List<SiteInterface> siteInterfaces) {
    this.siteInterfaces = siteInterfaces;
    interfaceMap = new HashMap<>();
    siteInterfaces.stream()
      .forEach(s -> interfaceMap.put(s.site(), s));
  }

  public List<Problem> fetchProblemData() {
    log.info("Fetching problem data");
    return siteInterfaces
      .stream()
      .map(SiteInterface::fetchProblems)
      .flatMap(List::stream)
      .filter(Problem::isValid)
      .distinct()
      .collect(Collectors.toList());
  }

  public List<UserStats> fetchSubmissionStats(List<Handle> handles) {
    return handles
      .parallelStream()
      .unordered()
      .map(h -> interfaceMap.get(h.getSite()).fetchUserStats(h))
      .collect(Collectors.toList());
  }

}
