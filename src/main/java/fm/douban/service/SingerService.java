package fm.douban.service;

import fm.douban.model.Singer;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SingerService {

    //增加一个歌手
    Singer addSinger(Singer singer);

    //根据歌手id查询歌手
    Singer get(String singerId);

    //查询全部歌手
    List<Singer> getAll();

    //修改歌手
    boolean modify(Singer singer);

    //根据歌手id主键删除歌手
    boolean delete(String singerId);

}