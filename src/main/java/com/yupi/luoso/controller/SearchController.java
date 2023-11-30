package com.yupi.luoso.controller;

import com.yupi.luoso.common.BaseResponse;
import com.yupi.luoso.common.ResultUtils;
import com.yupi.luoso.manager.SearchFacade;
import com.yupi.luoso.model.dto.search.SearchRequest;
import com.yupi.luoso.model.vo.SearchVO;
import com.yupi.luoso.service.PictureService;
import com.yupi.luoso.service.PostService;
import com.yupi.luoso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.searchAll(searchRequest, request));
    }

}
