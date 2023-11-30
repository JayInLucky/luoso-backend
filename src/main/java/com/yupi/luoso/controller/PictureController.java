package com.yupi.luoso.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.yupi.luoso.annotation.AuthCheck;
import com.yupi.luoso.common.BaseResponse;
import com.yupi.luoso.common.DeleteRequest;
import com.yupi.luoso.common.ErrorCode;
import com.yupi.luoso.common.ResultUtils;
import com.yupi.luoso.constant.UserConstant;
import com.yupi.luoso.exception.BusinessException;
import com.yupi.luoso.exception.ThrowUtils;
import com.yupi.luoso.model.dto.picture.PictureQueryRequest;
import com.yupi.luoso.model.dto.post.PostAddRequest;
import com.yupi.luoso.model.dto.post.PostEditRequest;
import com.yupi.luoso.model.dto.post.PostQueryRequest;
import com.yupi.luoso.model.dto.post.PostUpdateRequest;
import com.yupi.luoso.model.entity.Picture;
import com.yupi.luoso.model.entity.Post;
import com.yupi.luoso.model.entity.User;
import com.yupi.luoso.model.vo.PostVO;
import com.yupi.luoso.service.PictureService;
import com.yupi.luoso.service.PostService;
import com.yupi.luoso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图片接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {

    @Resource
    private PictureService pictureService;

    /**
     * 分页获取列表（封装类）
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPostVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                        HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        String searchText = pictureQueryRequest.getSearchText();
        Page<Picture> picturePage = pictureService.searchPicture(searchText, current, size);
        return ResultUtils.success(picturePage);
    }


}
