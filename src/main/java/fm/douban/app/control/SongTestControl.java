package fm.douban.app.control;

import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import fm.douban.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/test/song")
public class SongTestControl {
    @Autowired
    private SongService songService;

    @GetMapping(path = "/add")
    public Song testAdd(){
        Song song = new Song();
        song.setId("0");
        song.setName("Cyt");
        song.setLyrics("Cyt123");
        song.setCover("Cyt Beautiful");
        return songService.add(song);
    }

    @GetMapping(path = "/get")
    public Song testGet(){
        return songService.get("0");
    }

    @GetMapping(path = "/list")
    public Page<Song> testList(){
        SongQueryParam songQueryParam = new SongQueryParam();
        songQueryParam.setPageNum(1);
        songQueryParam.setPageSize(1);
        return songService.list(songQueryParam);
    }

    @GetMapping(path = "modify")
    public boolean testModify(){
        Song song = new Song();

        song.setName("Cyt cute");
        song.setId("0");

        return songService.modify(song);
    }

    @GetMapping(path = "/del")
    public boolean testDelete()
    {
        return songService.delete("0");
    }
}












