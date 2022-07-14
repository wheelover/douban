package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import fm.douban.service.SongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

@Service
public class SongServiceImpl implements SongService {

    private static final Logger LOG = LoggerFactory.getLogger(SongServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public Song add(Song song) {
        if (song == null){
            LOG.error("input Song data is null");
            return null;
        }

        return mongoTemplate.insert(song);
    }

    @Override
    public Song get(String songId) {
        if (!StringUtils.hasText(songId)){
            LOG.error("input song id is blank");
            return null;
        }
        Song song = mongoTemplate.findById(songId, Song.class);
        return song;
    }

    @Override
    public Page<Song> list(SongQueryParam songQueryParam) {
        if (songQueryParam == null) {
            LOG.error("input Song data is not correct.");
            return null;
        }

//        // 总条件
//        Criteria criteria = new Criteria();
//        // 可能有多个子条件
//        List<Criteria> subCris = new ArrayList();
//        if (StringUtils.hasText(songQueryParam.getName())) {
//            subCris.add(Criteria.where("name").is(songQueryParam));
//        }
//
//        if (StringUtils.hasText(songQueryParam.getCover())) {
//            subCris.add(Criteria.where("cover").is(songQueryParam));
//        }
//
//        if (StringUtils.hasText(songQueryParam.getLyrics())) {
//            subCris.add(Criteria.where("lyrics").is(songQueryParam));
//        }
//
//        if (StringUtils.hasText(songQueryParam.getUrl())){
//            subCris.add(Criteria.where("url").is(songQueryParam));
//        }
//
//        if (StringUtils.hasText(songQueryParam.getId())){
//            subCris.add(Criteria.where("id").is(songQueryParam));
//        }
//
//        // 必须至少有一个查询条件
//        if (subCris.isEmpty()) {
//            LOG.error("input User query param is not correct.");
//            return null;
//        }
//
//        // 三个子条件以 and 关键词连接成总条件对象，相当于 name='' and lyrics='' and subjectId=''
//        criteria.andOperator(subCris.toArray(new Criteria[] {}));
//
//        // 条件对象构建查询对象
//        Query query = new Query(criteria);

        Query query = new Query();

        // 总数
        long count = mongoTemplate.count(query, Song.class);
        // 构建分页对象。注意此对象页码号是从 0 开始计数的。
        Pageable pageable = PageRequest.of(songQueryParam.getPageNum() - 1, songQueryParam.getPageSize());
        query.with(pageable);

        // 查询结果
        List<Song> songs = mongoTemplate.find(query, Song.class);
        // 构建分页器
        Page<Song> pageResult = PageableExecutionUtils.getPage(songs, pageable, new LongSupplier() {
            @Override
            public long getAsLong() {
                return count;
            }
        });

        return pageResult;
    }

    @Override
    public boolean modify(Song song) {
        // 作为服务，要对入参进行判断，不能假设被调用时，入参一定正确
        if (song == null || !StringUtils.hasText(song.getId())) {
            LOG.error("input User data is not correct.");
            return false;
        }

        // 主键不能修改，作为查询条件
        Query query = new Query(Criteria.where("id").is(song.getId()));

        Update updateData = new Update();
        // 值为 null 表示不修改。值为长度为 0 的字符串 "" 表示清空此字段
        if (song.getSingerIds() != null) {
            updateData.set("singerIds", song.getSingerIds());
        }

        if (song.getId() != null) {
            updateData.set("id", song.getId());
        }

        if (song.getCover() != null) {
            updateData.set("cover", song.getCover());
        }

        if (song.getName() != null){
            updateData.set("name", song.getName());
        }

        if(song.getLyrics() != null){
            updateData.set("lyrics", song.getLyrics());
        }

        if (song.getUrl() != null){
            updateData.set("url", song.getUrl());
        }

        // 把一条符合条件的记录，修改其字段
        UpdateResult result = mongoTemplate.updateFirst(query, updateData, Song.class);
        return result != null && result.getModifiedCount() > 0;
    }

    @Override
    public boolean delete(String songId) {
        if (!StringUtils.hasText(songId)) {
            LOG.error("input user Id is blank.");
            return false;
        }

        Song song = new Song();
        song.setId(songId);

        DeleteResult result = mongoTemplate.remove(song);
        return result != null && result.getDeletedCount() > 0;
    }

}
