package org.example;

public class Song {
    private String downloadLink;
    private String name;

    public Song(String downloadLink, String name) {
        this.downloadLink = downloadLink;
        this.name = name;
    }
    public Song() {

    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
