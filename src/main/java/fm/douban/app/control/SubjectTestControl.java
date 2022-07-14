package fm.douban.app.control;

import fm.douban.model.Subject;
import fm.douban.service.SubjectService;
import fm.douban.util.SubjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(path = "/test/subject")
public class SubjectTestControl {
    @Autowired
    private SubjectService subjectService;


    @GetMapping(path = "/add")
    public Subject testAdd(){
        SubjectUtil subjectUtil = new SubjectUtil();
        Subject subject = new Subject();
        subject.setId("0");
        subject.setSubjectType(SubjectUtil.TYPE_MHZ);
        subject.setSubjectSubType(SubjectUtil.TYPE_SUB_AGE);
        subject.setName("Cyt");
        subject.setCover("Cyt beautiful");
        return subjectService.addSubject(subject);
    }

    @GetMapping("/get")
    public Subject testGet(){
        Subject subject = new Subject();
        subject = subjectService.get("0");
        return subject;
    }

    @GetMapping("/getByType")
    public List<Subject> testGetByType(){
        SubjectUtil subjectUtil = new SubjectUtil();
        return subjectService.getSubjects(SubjectUtil.TYPE_MHZ);
    }

    @GetMapping("/getBySUbType")
    public List<Subject> testGetBySubType(){
        SubjectUtil subjectUtil = new SubjectUtil();
        return subjectService.getSubjects(SubjectUtil.TYPE_MHZ, SubjectUtil.TYPE_SUB_AGE);
    }

    @GetMapping("/del")
    public boolean testDelete(){
        return subjectService.delete("0");
    }

}
