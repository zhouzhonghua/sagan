package org.springframework.site.documentation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.actuate.metrics.CounterService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.site.search.CrawlerService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DocumentationIndexService {

	private static Log logger = LogFactory.getLog(DocumentationIndexService.class);

	private static final long ONE_HOUR = 1000 * 60 * 60;
	private final DocumentationService documentationService;
	private final CrawlerService crawlerService;
	private final CounterService counters;

	private ExecutorService executor = Executors.newFixedThreadPool(10);

	@Autowired
	public DocumentationIndexService(CrawlerService crawlerService, DocumentationService documentationService, CounterService counters) {
		this.crawlerService = crawlerService;
		this.documentationService = documentationService;
		this.counters = counters;
	}

	// ten minute delay initially by default
	@Scheduled(fixedDelay = ONE_HOUR, initialDelayString = "${search.index.delay:600000}")
	public void indexDocumentation() {
		logger.info("Indexing project documentation");
		int count = 0;
		for (final Project project : documentationService.getProjects()) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					try {
						process(project);
						counters.increment("search.indexes.projects.processed");
					} catch (Exception e) {
						logger.warn("Unable to load project: " + project.getId() + "(" + e.getClass().getName() + ", " + e.getMessage() + ")");
						counters.increment("search.indexes.projects.errors.count");
					}
				}
			});
			count++;
			if (count>2) {
				break; // TODO remove: hack to prevent index from getting too large in dev
			}
		}
		counters.increment("search.indexes.projects.refresh.count");
	}

	public void process(Project project) {
		logger.info("Indexing project: " + project.getId());
		if (!project.getSupportedVersions().isEmpty()) {
			crawlerService.crawl(new UriTemplate(project.getApiAllClassesUrl()).expand(project.getSupportedVersions().get(0)).toString(), 1);
			// TODO: support reference docs when we can work out a way to break them up into manageable pieces
			// crawlerService.crawl(new UriTemplate(project.getReferenceUrl()).expand(project.getSupportedVersions().get(0)).toString(), 2);
		}
		crawlerService.crawl(project.getGithubUrl(), 0);
	}


}