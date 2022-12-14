package fm.douban.spider;

import com.alibaba.fastjson.JSON;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.HttpUtil;
import fm.douban.util.SubjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class SubjectSpider {
    private static Logger logger = LoggerFactory.getLogger(SubjectSpider.class);

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SingerService singerService;

    @Autowired
    private SongService songService;

    private static final String MHZ_URL = "https://fm.douban.com/j/v2/rec_channels?specific=all";
    private static final String MHZ_REFERER = "https://fm.douban.com/";
    private static final String HOST = "fm.douban.com";

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter df2 = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private static final String SD_URL =
            "https://fm.douban.com/j/v2/playlist?channel={0}&kbps=128&client=s%3Amainsite%7Cy%3A3.0&app_name=radio_website&version=100&type=n";

    private static final String SL_URL =
            "https://fm.douban.com/j/v2/songlist/explore?type=hot&genre=0&limit=20&sample_cnt=5";
    private static final String SL_REFERER = "https://fm.douban.com/explore/songlists";

    @PostConstruct
    public void init() {

        CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> doExcute())
                .thenAccept(result -> logger.info("spider end ..."));

        logger.info("spider end ...");
    }

    public boolean doExcute() {
        getSubjectData();
        getCollectionsData();
        return true;
    }

    private void getSubjectData() {
        // ????????????????????????????????????????????????????????????
        String cookie = "bid=d6of8GWtyPM; __utmv=30149280.25392; __utma=30149280.1603986795.1644641802.1644641802.1646051102.2; __utmz=30149280.1646051102.2.2.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; _ga=GA1.2.1603986795.1644641802; _gid=GA1.2.42144544.1656498418; dbcl2=\"258612309:84nf9NHazsU\"; ck=pJit; _pk_ref.100001.f71f=%5B%22%22%2C%22%22%2C1656503824%2C%22https%3A%2F%2Flearn.youkeda.com%2F%22%5D; _pk_ses.100001.f71f=*; _pk_id.100001.f71f=802ecd78197c72ba.1652618196.7.1656503927.1656500444.";
        Map<String, String> headerData = httpUtil.buildHeaderData(MHZ_REFERER, HOST, cookie);
        String content = httpUtil.getContent(MHZ_URL, headerData);

        if (!StringUtils.hasText(content)) {
            return;
        }

        Map dataObj = null;

        try {
            dataObj = JSON.parseObject(content, Map.class);
        } catch (Exception e) {
            // ????????????????????????????????????????????????????????? json ????????????????????????????????????????????????
            logger.error("parse content to map error. ", e);
        }

        // ??????????????????
        if (dataObj == null) {
            return;
        }

        Map subDataObj = (Map)dataObj.get("data");

        Map channelsObj = null;
        if (subDataObj != null && !subDataObj.isEmpty()) {
            channelsObj = (Map)subDataObj.get("channels");
        }

        if (channelsObj == null) {
            return;
        }

        // ?????????????????? ??????
        List artists = (List)channelsObj.get("artist");

        // ??????/?????? ??????
        List languages = (List)channelsObj.get("language");

        // ??????/?????? ??????
        List genres = (List)channelsObj.get("genre");

        // ??????/?????? ??????
        List scenarios = (List)channelsObj.get("scenario");

        System.out.println("artists=" + artists.size());
        System.out.println("languages=" + languages.size());
        System.out.println("genres=" + genres.size());
        System.out.println("scenarios=" + scenarios.size());

        addMHZSubject(artists, SubjectUtil.TYPE_SUB_ARTIST);
        addMHZSubject(languages, SubjectUtil.TYPE_SUB_AGE);
        addMHZSubject(genres, SubjectUtil.TYPE_SUB_STYLE);
        addMHZSubject(scenarios, SubjectUtil.TYPE_SUB_MOOD);
    }

    private void addMHZSubject(List<Map> channels, String subjectSubType) {
        if (channels == null || channels.isEmpty()) {
            return;
        }

        channels.forEach(channelObj -> {
            Subject subject = buildSubject(channelObj, SubjectUtil.TYPE_MHZ, subjectSubType);

            if (SubjectUtil.TYPE_SUB_ARTIST.equals(subjectSubType)) {
                // ?????????????????????
                List relatedArtists = (List)channelObj.get("related_artists");
                addSingers(relatedArtists);
            }

            // ??????MHZ??????
            saveSubject(subject);

            getSubjectSongData(subject);
        });

    }



    private void getSubjectSongData(Subject subject){
        String subjectId = subject.getId();
        String songDataUrl = MessageFormat.format(SD_URL, subjectId);

        String cookie = "bid=d6of8GWtyPM; __utmv=30149280.25392; __utma=30149280.1603986795.1644641802.1644641802.1646051102.2; __utmz=30149280.1646051102.2.2.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; _ga=GA1.2.1603986795.1644641802; _gid=GA1.2.42144544.1656498418; dbcl2=\"258612309:84nf9NHazsU\"; ck=pJit; _pk_ref.100001.f71f=%5B%22%22%2C%22%22%2C1656503824%2C%22https%3A%2F%2Flearn.youkeda.com%2F%22%5D; _pk_id.100001.f71f=802ecd78197c72ba.1652618196.7.1656503927.1656500444.";
        Map<String, String> headerData = httpUtil.buildHeaderData(MHZ_REFERER, HOST, cookie);
        String content = httpUtil.getContent(songDataUrl, headerData);

        if (!StringUtils.hasText(content)) {
            return;
        }

        Map dataObj = null;

        try {
            dataObj = JSON.parseObject(content, Map.class);
        } catch (Exception e) {
            // ????????????????????????????????????????????????????????? json ????????????????????????????????????????????????
            logger.error("parse content to map error. ", e);
        }

        // ??????????????????
        if (dataObj == null) {
            return;
        }

        List<Map> songsData = (List<Map>)dataObj.get("song");
        if (songsData == null || songsData.isEmpty()) {
            return;
        }

        // ??????????????????????????? id ??????
        List<String> songIdList = subject.getSongIds();
        if (songIdList == null) {
            // ????????? null???????????????
            songIdList = new ArrayList<>();
        }

        for (Map songObj : songsData) {
            Song song = buildSong(songObj);
            saveSong(song);

            // ???????????? id ??????????????????????????????????????????
            if (!songIdList.contains(song.getId())) {
                songIdList.add(song.getId());
            }
        }

        // ????????????????????????
        if (!songIdList.isEmpty()) {
            subject.setSongIds(songIdList);
            subjectService.modify(subject);
        }


    }

    private void getCollectionsData(){
        // ????????????????????????????????????????????????????????????
        String cookie = "bid=d6of8GWtyPM; __utmv=30149280.25392; __utma=30149280.1603986795.1644641802.1644641802.1646051102.2; __utmz=30149280.1646051102.2.2.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; _ga=GA1.2.1603986795.1644641802; _gid=GA1.2.42144544.1656498418; dbcl2=\"258612309:84nf9NHazsU\"; ck=pJit; _pk_ref.100001.f71f=%5B%22%22%2C%22%22%2C1656503824%2C%22https%3A%2F%2Flearn.youkeda.com%2F%22%5D; _pk_ses.100001.f71f=*; _pk_id.100001.f71f=802ecd78197c72ba.1652618196.7.1656503927.1656500444.";
        Map<String, String> headerData = httpUtil.buildHeaderData(SL_REFERER, HOST, cookie);
        String content = httpUtil.getContent(SL_URL, headerData);

        if (!StringUtils.hasText(content)) {
            return;
        }

        List<Map> dataList = null;

        try {
            dataList = JSON.parseObject(content, List.class);
        } catch (Exception e) {
            // ????????????????????????????????????????????????????????? json ????????????????????????????????????????????????
            logger.error("parse content to map error. ", e);
        }

        if (dataList == null || dataList.isEmpty()) {
            return;
        }

        dataList.forEach(slObj -> {
            Subject collection = buildSubject(slObj, SubjectUtil.TYPE_COLLECTION, null);

            // ?????????????????????????????? user
            Map creator = (Map)slObj.get("creator");

            if (creator != null && creator.get("id") != null && StringUtils.hasText(creator.get("id").toString())) {
                Singer singer = buildSinger(creator);
                saveSinger(singer);
            }

            // ?????? ?????? ??????
            saveSubject(collection);
        });

    }

    // ????????????
    private void addSingers(List<Map> artists) {
        if (artists == null || artists.isEmpty()) {
            return;
        }

        artists.forEach(artistObj -> {
            Singer singer = buildSinger(artistObj);
            saveSinger(singer);
        });

    }

    private Subject buildSubject(Map sourceData, String mainType, String subType) {
        Subject subject = new Subject();
        subject.setCover((String)sourceData.get("cover"));

        if (sourceData.get("title") != null) {
            subject.setName((String)sourceData.get("title"));
        } else if (sourceData.get("name") != null) {
            subject.setName((String)sourceData.get("name"));
        } else {
            subject.setName("");
        }

        subject.setDescription((String)sourceData.get("intro"));
        subject.setId(sourceData.get("id") == null ? null : sourceData.get("id").toString());

        if (sourceData.get("created_time") != null) {
            subject.setPublishedDate(LocalDate.parse(sourceData.get("created_time").toString(), df));
        } else {
            subject.setPublishedDate(LocalDate.now());
        }

        subject.setSubjectSubType(subType);
        subject.setSubjectType(mainType);

        if (SubjectUtil.TYPE_SUB_ARTIST.equals(subType) && sourceData.get("artist_id") != null) {
            subject.setMaster(sourceData.get("artist_id").toString());
        } else if (SubjectUtil.TYPE_COLLECTION.equals(mainType) && sourceData.get("creator") != null) {
            Map creator = (Map)sourceData.get("creator");
            if (creator != null && creator.get("id") != null) {
                subject.setMaster(creator.get("id").toString());
            }
        }

        return subject;
    }

    private void saveSubject(Subject subject) {
        try {
            // ??????????????????????????????
            Subject oldSubject = subjectService.get(subject.getId());
            if (oldSubject == null) {
                subjectService.addSubject(subject);
            }
        } catch (Exception e) {
            logger.error("operate subject error. new subject=" + JSON.toJSONString(subject), e);
        }
    }

    public Singer buildSinger(Map source) {
        Singer singer = new Singer();
        singer.setId(source.get("id") == null ? null : source.get("id").toString());
        singer.setName(source.get("name") == null ? null : source.get("name").toString());

        if (source.get("cover") != null && StringUtils.hasText(source.get("cover").toString())) {
            singer.setAvatar(source.get("cover").toString());
        } else if (source.get("picture") != null && StringUtils.hasText(source.get("picture").toString())) {
            singer.setAvatar(source.get("picture").toString());
        } else if (source.get("avatar") != null && StringUtils.hasText(source.get("avatar").toString())) {
            singer.setAvatar(source.get("avatar").toString());
        }

        if (source.get("create_time") != null && StringUtils.hasText(source.get("create_time").toString())) {
            LocalDate ld = LocalDate.parse(source.get("create_time").toString(), df2);
            LocalTime lt = LocalTime.of(0, 0, 0);
            singer.setGmtCreated(LocalDateTime.of(ld, lt));
            singer.setGmtModified(singer.getGmtCreated());
        }

        if (source.get("url") != null && StringUtils.hasText(source.get("url").toString())) {
            singer.setHomepage(source.get("url").toString());
        }

        return singer;
    }

    public void saveSinger(Singer singer) {
        try {
            // ??????????????????????????????
            Singer oldSinger = singerService.get(singer.getId());
            if (oldSinger == null) {
                singerService.addSinger(singer);
            }
        } catch (Exception e) {
            logger.error("operate singer error. new singer=" + JSON.toJSONString(singer), e);
        }
    }

    public Song buildSong(Map source){
        Song song = new Song();
        song.setUrl((String)source.get("url"));
        song.setCover((String)source.get("picture"));
        song.setId((String)source.get("sid"));
        song.setName((String)source.get("title"));

        List<String> singerIds = new ArrayList<>();
        List<Map> singerSources = (List<Map>)source.get("singers");

        singerSources.forEach(singerObj -> {
            Singer singer = buildSinger(singerObj);
            singerIds.add(singer.getId());
            saveSinger(singer);
        });

        song.setSingerIds(singerIds);
        return song;
    }

    public void saveSong(Song song){
        try {
            Song oldSong = songService.get(song.getId());
            if (oldSong == null) {
                songService.add(song);
            }
        }catch (Exception e){
            logger.error("operate song error. new song=" + JSON.toJSONString(song), e);
        }
    }
}
