package fm.douban.model;

import java.lang.reflect.Type;
import java.net.Proxy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Subject {
    private String id;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;
    private String name;
    private String description;
    private String cover;
    private String master;
    private LocalDate publishedDate;
    private String subjectType;
    private String subjectSubType;
    private List<String> songIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getSubjectSubType() {
        return subjectSubType;
    }

    public void setSubjectSubType(String subjectSubType) {
        this.subjectSubType = subjectSubType;
    }

    public List<String> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<String> songIds) {
        this.songIds = songIds;
    }
}
