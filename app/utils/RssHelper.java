package utils;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;
import models.Post;
import org.apache.commons.io.output.FileWriterWithEncoding;
import play.Logger;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Администратор
 * Date: 01.12.12
 * Time: 23:06
 * To change this template use File | Settings | File Templates.
 */
public class RssHelper {

    private static RssHelper instance;

    private static final String FEED_TYPE = "rss_2.0";
    private static final int ARRAY_CAPACITY = 20;
    private static final String RSS_ENCODING = "UTF-8";
    private RssList entries;
    private SyndFeed outputFeed;

    private RssHelper(){
        outputFeed = new SyndFeedImpl();
        outputFeed.setEncoding(RSS_ENCODING);
        outputFeed.setFeedType(FEED_TYPE);
        outputFeed.setTitle("Startnewream.ru");
        outputFeed.setLink("http://startnewteam.ru/rss");
        outputFeed.setDescription("Rss news of startnewteam.ru command");

        SyndImage syndImage = new SyndImageImpl();
        syndImage.setTitle("Startnewteam");
        syndImage.setLink("http://startnewteam.ru");
        //todo
        syndImage.setUrl("");
        outputFeed.setImage(syndImage);

        entries = readRssXML();
    }

    public static RssHelper getInstance(){
        if(instance == null){
            instance = new RssHelper();
        }
        return instance;
    }

    private RssList readRssXML() {
        RssList result = new RssList();
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed inputFeed = new SyndFeedImpl();
            inputFeed.setEncoding(RSS_ENCODING);
            inputFeed = input.build(new XmlReader(FileStoreHelper.getRssFile()));
            result.addAll(inputFeed.getEntries());
        } catch (Exception e) {
            Logger.error("Cannot read rss xml", e);
        }
        return result;
    }

    public void addNews(Post post, String base) {
        try {
            post.published = true;
            post.save();

            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(post.title);
            entry.setLink(base + "/news/" + post.id);
            entry.setPublishedDate(new Date());

            SyndContent description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue(post.preview);
            entry.setDescription(description);

            entries.add(entry);
            outputFeed.setEntries(entries);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(outputFeed, new FileWriterWithEncoding(FileStoreHelper.getRssFile(), RSS_ENCODING));
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
