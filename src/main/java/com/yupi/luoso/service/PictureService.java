package com.yupi.luoso.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.luoso.model.entity.Picture;


public interface PictureService {

    Page<Picture> searchPicture(String searchText, long pageNum, long pageSize);


}
