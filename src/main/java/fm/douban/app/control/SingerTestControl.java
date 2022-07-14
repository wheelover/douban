package fm.douban.app.control;

import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(path = "/test/singer")
public class SingerTestControl {
    @Autowired
    private SingerService singerService;

    @GetMapping("/add")
    public Singer testAddSinger(){
        Singer singer = new Singer();
        singer.setId("0");
        singer.setName("polor");
        singer.setAvatar("CytBeautifulPicture");
        singer.setHomepage("Cyt");
        return singerService.addSinger(singer);
    }

    @GetMapping("/getAll")
    public List<Singer> testGetAll(){
        return singerService.getAll();
    }

    @GetMapping("/getOne")
    public Singer testGetSinger(){
        return singerService.get("0");
    }

    @GetMapping("/modify")
    public boolean testModifySinger(){
        Singer singer = new Singer();
        singer.setId("0");
        singer.setName("Cyt");
        singer.setAvatar("CytBeautifulPicture");
        singer.setHomepage("Cyt");
        return singerService.modify(singer);
    }

    @GetMapping("/del")
    public boolean testDelSinger(){
        return singerService.delete("0");
    }
}
