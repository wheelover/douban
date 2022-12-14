package fm.douban.app.control;

import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class SingerControl {

    @Autowired
    private SingerService singerService;


    @GetMapping(path = "/user-guide")
    public String myMhz(Model model){
        List<Singer> singers = new ArrayList<>();
        singers = randomSingers();
        model.addAttribute("singers", singers);
        return "userguide";
    }

    @GetMapping("/singer/random")
    @ResponseBody
    public List<Singer> randomSingers(){
        List<Singer> singerList = singerService.getAll();
        List<Singer> singers = new ArrayList<>();
        if (singerList != null && singerList.size() > 0) {
            Random random = new Random();
            for (int i = 0; i < 10; i++) {
                singers.add(singerList.get(random.nextInt(singerList.size())));

            }
        }
        return singers;
    }



}
