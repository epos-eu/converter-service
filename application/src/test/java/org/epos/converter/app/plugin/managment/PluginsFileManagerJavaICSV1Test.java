package org.epos.converter.app.plugin.managment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PluginsFileManagerJavaICSV1Test {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGenerateRequestRepoUrl() 
	{
		List<String[]> repoData = List.of(
				new String[] {"https://epos-ci.brgm.fr/epos/converter-plugins/ah-episodes-elements-plugin.git", "clati-eCpig78KIDP-xKkn9OTP" },
				new String[] {"https://epos-ci.brgm.fr/epos/converter-plugins/ah-list-of-applications-plugin.git", "glpat-HJGg7dhXzZq-ty7fmmol"},
				new String[] {"https://epos-ci.brgm.fr/epos/converter-plugins/ah-episodes-plugin.git", "sqIFF-Llus6777Pla-ty7poloq"},
				new String[] {"https://epos-ci.brgm.fr/epos/converter-plugins/satd-plugin.git", "glpat-HJGg7dh6TLD-Ch7hfyu9"},
				new String[] {"http://epos-ci.brgm.fr/epos/converter-plugins/satd-plugin.git", "glpat-HJGg7dh6TLD-Ch7hfyu9"});
		
		List<String> expectedURLs = List.of(
				"https://gitlab-ci-token:clati-eCpig78KIDP-xKkn9OTP@epos-ci.brgm.fr/epos/converter-plugins/ah-episodes-elements-plugin.git",
				"https://gitlab-ci-token:glpat-HJGg7dhXzZq-ty7fmmol@epos-ci.brgm.fr/epos/converter-plugins/ah-list-of-applications-plugin.git",
				"https://gitlab-ci-token:sqIFF-Llus6777Pla-ty7poloq@epos-ci.brgm.fr/epos/converter-plugins/ah-episodes-plugin.git",
				"https://gitlab-ci-token:glpat-HJGg7dh6TLD-Ch7hfyu9@epos-ci.brgm.fr/epos/converter-plugins/satd-plugin.git",
				"http://gitlab-ci-token:glpat-HJGg7dh6TLD-Ch7hfyu9@epos-ci.brgm.fr/epos/converter-plugins/satd-plugin.git");

		IntStream.range(0, repoData.size()).forEach(i -> {

			try {
				// prepare
				URL repoUrl = new URL(repoData.get(i)[0]);
				String repoToken = repoData.get(i)[1];
				
				// test 
				URL actualRepoUrl = PluginsFileManagerJavaICSV1.generateSecuredGitLabRepoUrl(repoUrl, repoToken);
				
				// compare
				URL expectedRepoUrl = new URL(expectedURLs.get(i));
				assertEquals(expectedRepoUrl, actualRepoUrl);
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
		});

	}

}
