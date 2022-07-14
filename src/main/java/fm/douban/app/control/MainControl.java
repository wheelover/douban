package fm.douban.app.control;

import com.alibaba.fastjson.JSON;
import fm.douban.model.MhzViewModel;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.param.SongQueryParam;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.SubjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class MainControl {

    private static final Logger LOG = LoggerFactory.getLogger(MainControl.class);

    @Autowired
    private SongService songService;

    @Autowired
    private SingerService singerService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping(path = "/index")
    public String index(Model model){
        // 设置首屏歌曲数据
        setSongData(model);
        // 设置赫兹数据
        setMhzData(model);
        return "index";
    }

    //搜索页
    @GetMapping("/search")
    public String search(Model model){

        return "search";

    }

    //搜索结果
    @GetMapping("/searchContent")
    @ResponseBody
    public Map searchContent(@RequestParam(name = "keyword")String keyword){
        SongQueryParam songQueryParam = new SongQueryParam();
        songQueryParam.setName(keyword);
        Page<Song> songs = songService.list(songQueryParam);
        Map result = new HashMap<>();
        result.put("songs", songs);
        return result;
    }

    private void setSongData(Model model) {
        SongQueryParam songParam = new SongQueryParam();
        songParam.setPageNum(1);
        songParam.setPageSize(1);
        Page<Song> songs = songService.list(songParam);

        if (songs != null && !songs.isEmpty()) {
            Song resultSong = songs.getContent().get(0);
            model.addAttribute("song", resultSong);

            List<String> singerIds = resultSong.getSingerIds();

            List<Singer> singers = new ArrayList<>();
            if (singerIds != null && !singerIds.isEmpty()) {

                for (String singerId : singerIds) {
                    Singer singer = singerService.get(singerId);
                    singers.add(singer);
                }
            }

            model.addAttribute("singers", singers);
        }
    }

    private void setMhzData(Model model){
        List<Subject> subjectList = subjectService.getSubjects(SubjectUtil.TYPE_MHZ);
        List<Subject> artistData = new ArrayList<>();
        List<Subject> moodData = new ArrayList<>();
        List<Subject> ageData = new ArrayList<>();
        List<Subject> styleData = new ArrayList<>();

        for (Subject subject : subjectList){
            if (subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_AGE)){
                ageData.add(subject);
            }else if (subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_ARTIST)){
                artistData.add(subject);
            }else if (subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_MOOD)){
                moodData.add(subject);
            } else if (subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_STYLE)){
                styleData.add(subject);
            }else {
                // 防止数据错误
                LOG.error("subject data error. unknown subtype. subject=" + JSON.toJSONString(subject));
            }
        }

        model.addAttribute("artistDatas", artistData);

        List<MhzViewModel> mhzViewModels = new ArrayList<>();
        buildMhzViewModel(moodData, "心情 / 场景", mhzViewModels);
        buildMhzViewModel(ageData, "语言 / 年代", mhzViewModels);
        buildMhzViewModel(styleData, "风格 / 流派", mhzViewModels);
        model.addAttribute("mhzViewModels", mhzViewModels);

    }

    private void buildMhzViewModel(List<Subject> subjects, String title, List<MhzViewModel> mhzViewModels) {
        MhzViewModel mhzVM = new MhzViewModel();
        mhzVM.setSubjects(subjects);
        mhzVM.setTitle(title);
        mhzViewModels.add(mhzVM);
    }

}




