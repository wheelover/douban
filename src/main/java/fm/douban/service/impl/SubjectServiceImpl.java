package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Subject;
import fm.douban.service.SubjectService;
import fm.douban.util.SubjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    private static final Logger LOG = LoggerFactory.getLogger(SongServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public Subject addSubject(Subject subject) {
        if (subject == null){
            LOG.error("input Subject data is null");
            return null;
        }

        return mongoTemplate.insert(subject);
    }

    @Override
    public Subject get(String subjectId) {
        if (!StringUtils.hasText(subjectId)){
            LOG.error("input subject id is blank");
            return null;
        }
        Subject subject = mongoTemplate.findById(subjectId, Subject.class);
        return subject;
    }

    @Override
    public List<Subject> getSubjects(String type) {
        return getSubjects(type, null);
    }

    @Override
    public List<Subject> getSubjects(String type, String subType) {
        Subject subjectParam = new Subject();
        subjectParam.setSubjectType(type);
        subjectParam.setSubjectSubType(subType);

        return getSubjects(subjectParam);
    }

    @Override
    public List<Subject> getSubjects(Subject subjectParam) {
        // 作为服务，要对入参进行判断，不能假设被调用时，入参一定正确
        if (subjectParam == null) {
            LOG.error("input subjectParam is not correct.");
            return null;
        }

        String type = subjectParam.getSubjectType();
        String subType = subjectParam.getSubjectSubType();
        String master = subjectParam.getMaster();

        // 作为服务，要对入参进行判断，不能假设被调用时，入参一定正确
        if (!StringUtils.hasText(type)) {
            LOG.error("input type is not correct.");
            return null;
        }

        // 总条件
        Criteria criteria = new Criteria();
        // 可能有多个子条件
        List<Criteria> subCris = new ArrayList();
        subCris.add(Criteria.where("subjectType").is(type));

        if (StringUtils.hasText(subType)) {
            subCris.add(Criteria.where("subjectSubType").is(subType));
        }

        if (StringUtils.hasText(master)) {
            subCris.add(Criteria.where("master").is(master));
        }

        // 三个子条件以 and 关键词连接成总条件对象，相当于 name='' and lyrics='' and subjectId=''
        criteria.andOperator(subCris.toArray(new Criteria[] {}));

        // 条件对象构建查询对象
        Query query = new Query(criteria);

        // 查询结果
        List<Subject> subjects = mongoTemplate.find(query, Subject.class);

        return subjects;
    }

    @Override
    public boolean modify(Subject subject) {
        if (subject == null || !StringUtils.hasText(subject.getId())){
            LOG.error("input User data is not correct.");
            return false;
        }

        Query query = new Query(Criteria.where("id").is(subject.getId()));

        Update updateData = new Update();

        if (subject.getSongIds() != null) {
            updateData.set("songIds", subject.getSongIds());
        }
        updateData.set("gmtModified", LocalDateTime.now());

        UpdateResult result = mongoTemplate.updateFirst(query, updateData, Subject.class);

        return result != null && result.getModifiedCount() > 0;
    }

    @Override
    public boolean delete(String subjectId) {
        if (!StringUtils.hasText(subjectId)) {
            LOG.error("input user Id is blank.");
            return false;
        }

        Subject subject = new Subject();
        subject.setId(subjectId);

        DeleteResult result = mongoTemplate.remove(subject);
        return result != null && result.getDeletedCount() > 0;
    }
}
