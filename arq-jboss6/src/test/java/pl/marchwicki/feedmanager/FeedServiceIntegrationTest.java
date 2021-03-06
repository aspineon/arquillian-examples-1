package pl.marchwicki.feedmanager;

import static org.junit.Assert.*;

import java.io.File;

import javax.ejb.EJBException;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.xml.sax.SAXParseException;

import pl.marchwicki.feedmanager.log.FeedBodyLoggingInterceptor;
import pl.marchwicki.feedmanager.log.FeedEventLog;
import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.FeedBuilder;
import pl.marchwicki.feedmanager.model.Item;

@RunWith(Arquillian.class)
public class FeedServiceIntegrationTest {

	final String FEED_NAME = "some_feed_name";
	
	@Deployment
	public static WebArchive deployment() {
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("rome:rome:0.9").withTransitivity()
				.asFile();
		
		return ShrinkWrap.create(WebArchive.class, "test.war")
				.addAsLibraries(libs)
				.addClasses(FeedsService.class, FeedBuilder.class)
				.addClasses(InMemoryFeedsRepository.class, FeedsRepository.class)
				.addClasses(Feed.class, Item.class)
				.addClasses(FeedBodyLoggingInterceptor.class, FeedEventLog.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}

	@Inject
	FeedsService service;
	
	@Test(expected = EJBException.class)
	public void shouldThrowExceptionOnEmptyFeed() {
		service.addNewItems(FEED_NAME, "");
	}

	@Test
	public void shouldThrowParsingExceptionOnEmptyFeed() {
		try {
			service.addNewItems(FEED_NAME, "");
			fail("Should have got exception");
		} catch (EJBException e) {
			assertEquals(IllegalArgumentException.class, e.getCausedByException().getClass());
		}
	}

	
}
