package fm.douban.app.control;

import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.SubjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SubjectControl {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectControl.class);

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SongService songService;

    @Autowired
    private SingerService singerService;


    @PostConstruct
    public void ptint(){
        List<Subject> subjects = subjectService.getSubjects(SubjectUtil.TYPE_MHZ, SubjectUtil.TYPE_SUB_ARTIST);
        String sId = subjects.get(0).getId();
        System.out.println(sId);
    }

    @GetMapping("/artist")
    public String mhzDetail(Model model, @RequestParam(name = "subjectId")String subjectId){

        Subject subject = subjectService.get(subjectId);
        if (subject == null){
            LOG.error("subject is null");
        }

        model.addAttribute("subject", subject);

        List<Song> songs = new ArrayList<>();

        if (subject.getSongIds() != null)
        for (String songId : subject.getSongIds()){
            Song song = new Song();
            if (song != null)
            songs.add(songService.get(songId));
        }

        model.addAttribute("songs", songs);

        Singer singer = new Singer();
        if (subject.getMaster() != null){
            singer = singerService.get(subject.getMaster());
        }

        model.addAttribute("singer", singer);

        List<Singer> simSingers = new ArrayList<>();
        List<String> simSingersId = singer.getSimilarSingerIds();
        if (simSingersId != null && !simSingersId.isEmpty()){
            for (String simSingerId : simSingersId){
                simSingers.add(singerService.get(simSingerId));
            }
        }

        model.addAttribute("simSingers", simSingers);

        return "mhzdetail";
    }

    @GetMapping("/collection")
    public String collection(Model model){
        return "collection";
    }

}
