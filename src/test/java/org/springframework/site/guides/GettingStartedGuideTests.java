package org.springframework.site.guides;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GettingStartedGuideTests {

	public static final String CONTENT = "";
	public static final String SIDEBAR = "";
	private GettingStartedGuide guide;

	@Before
	public void setUp() throws Exception {
		guide = new GettingStartedGuide("rest-service", CONTENT, SIDEBAR);
	}

	@Test
	public void testGetZipUrl() throws Exception {
		assertThat(guide.getZipUrl(), is("https://github.com/springframework-meta/gs-rest-service/archive/master.zip"));
	}

	@Test
	public void testGetGitRepoHttpsUrl() throws Exception {
		assertThat(guide.getGitRepoHttpsUrl(), is("https://github.com/springframework-meta/gs-rest-service.git"));
	}

	@Test
	public void testGetGitRepoSshUrl() throws Exception {
		assertThat(guide.getGitRepoSshUrl(), is("git@github.com:springframework-meta/gs-rest-service.git"));
	}

	@Test
	public void testGetRepoSubversionUrl() throws Exception {
		assertThat(guide.getGitRepoSubversionUrl(), is("https://github.com/springframework-meta/gs-rest-service"));
	}
}