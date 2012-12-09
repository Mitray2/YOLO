package utils;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;
import models.Post;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import play.Logger;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Evgeniy Yadlovskiy
 * Date: 01.12.12
 * Time: 23:06
 * To change this template use File | Settings | File Templates.
 */
public class RssHelper {

    private static RssHelper instance;

    private static final String FEED_TYPE = "rss_2.0";
    private static final int ARRAY_CAPACITY = 20;
    private RssList entries;
    private SyndFeed outputFeed;
    private String baseURL;

    private RssHelper(String baseURL) {
        this.baseURL = baseURL;
        outputFeed = new SyndFeedImpl();
        outputFeed.setEncoding(CharEncoding.UTF_8);
        outputFeed.setFeedType(FEED_TYPE);
        outputFeed.setTitle(StringUtils.capitalize(URLUtils.getDomainName(baseURL)));
        outputFeed.setLink(baseURL + "/rss");
        outputFeed.setDescription("Rss news of " + URLUtils.getApplicationName(baseURL) + " command");

        SyndImage syndImage = new SyndImageImpl();
        syndImage.setTitle(StringUtils.capitalize(URLUtils.getDomainName(baseURL)));
        syndImage.setLink(baseURL);
        //todo image for rss
        syndImage.setUrl("");
        outputFeed.setImage(syndImage);

        entries = readRssXML();
    }

    public static RssHelper getInstance(String baseURL){
        if(instance == null){
            instance = new RssHelper(baseURL);
        }
        return instance;
    }

    private RssList readRssXML() {
        RssList result = new RssList();
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed inputFeed = new SyndFeedImpl();
            inputFeed.setEncoding(CharEncoding.UTF_8);
            inputFeed = input.build(new XmlReader(FileStoreHelper.getRssFile()));
            result.addAll(inputFeed.getEntries());
        } catch (Exception e) {
            Logger.error("Cannot read rss xml", e);
        }
        return result;
    }

    public void addNews(Post post) {
        try {
            post.published = true;
            post.save();

            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(post.title);
            entry.setLink(baseURL + "/news/" + post.id);
            entry.setPublishedDate(new Date());

            SyndContent description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue(post.preview);
            entry.setDescription(description);

            entries.add(entry);
            outputFeed.setEntries(entries);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(outputFeed, new FileWriterWithEncoding(FileStoreHelper.getRssFile(), CharEncoding.UTF_8));
        } catch (Exception e) {
            Logger.error("Cannot write feed to rss xml", e);
        }
    }

    public String getRss() {
        try {
            outputFeed.setEntries(entries);
            return new SyndFeedOutput().outputString(outputFeed);
        } catch (FeedException e) {
            Logger.error("Cannot convert to xml", e);
        }
        return null;
    }

    private static class RssList extends ArrayList<SyndEntry> {

        @Override
        public boolean add(SyndEntry syndEntry) {
            if(size() == ARRAY_CAPACITY){
                remove(size() - 1);
            }
            add(0, syndEntry);
            return true;
        }

    }

}
