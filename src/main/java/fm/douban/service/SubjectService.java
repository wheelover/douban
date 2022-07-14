package fm.douban.service;

import fm.douban.model.Subject;

import java.util.List;

public interface SubjectService {
    //增加一个主题
    Subject addSubject(Subject subject);

    //查询单个主题
    Subject get(String subjectId);

    //查询一组主题
    List<Subject> getSubjects(String type);

    //查询一组主题
    List<Subject> getSubjects(String type, String subType);

    List<Subject> getSubjects(Subject subjectParam);

    boolean modify(Subject subject);



    //删除一组主题
    boolean delete(String subjectId);
}
