package com.example.sanketh.todaynews;

/**
 * News data model for fetching the required data
 */
public class News {

    private String headline, author, publishedDate, section, webUrl;

    public News(String headline, String author, String publishedDate, String section, String webUrl) {
        this.headline = headline;
        this.author = author;
        this.publishedDate = publishedDate;
        this.section = section;
        this.webUrl = webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getSection() {
        return section;
    }

    public String getWebUrl() {
        return webUrl;
    }
}
