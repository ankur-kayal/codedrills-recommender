package com.codedrills.service.sites;

import com.codedrills.model.Problem;
import com.codedrills.util.Helper;
import com.codedrills.util.TagHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public abstract class AbstractTagProblemFetcher extends AbstractSiteService {
  @Value("${tags_limit}")
  private int tagFetchLimit;
  private List<String> pendingTags;

  protected abstract List<Problem> fetchForTag(String tag);

  @Override
  public List<Problem> fetchProblems() {
    log.info(String.format("Fetching problems from %s", site().getShortName()));
    pendingTags = TagHelper.tagsOfSite(site())
      .stream()
      .limit(tagFetchLimit)
      .collect(Collectors.toList());

    List<Problem> problems = new ArrayList<>();

    int trials = 10;
    while(pendingTags.size() > 0 && trials > 0) {
      --trials;
      problems.addAll(tryFetch());
      Helper.sleep(5 * 1000);
    }

    return problems;
  }

  private List<Problem> tryFetch() {
    List<String> errorTags = new ArrayList<>();
    List<Problem> problems = pendingTags
      .stream()
      .peek(__ -> Helper.sleep(100))
      .map(t -> {
        try {
          return fetchForTag(t);
        } catch(Exception ex) {
          log.warn(String.format("Error while fetching for tag %s for site %s", t, site().getShortName()), ex);
          errorTags.add(t);
          return new ArrayList<Problem>();
        }
      })
      .flatMap(List::stream)
      .collect(Collectors.toList());

    log.info(String.format("For site %s, fetched %d tags", site().getShortName(), pendingTags.size() - errorTags.size()));
    pendingTags = errorTags;
    return problems;
  }

}
