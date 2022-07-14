package fm.douban.param;

import fm.douban.model.Song;

public class SongQueryParam extends Song {
    int pageNum = 1;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    int pageSize = 10;
}
