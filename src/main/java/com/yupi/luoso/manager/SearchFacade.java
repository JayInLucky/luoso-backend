package com.yupi.luoso.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.luoso.common.BaseResponse;
import com.yupi.luoso.common.ErrorCode;
import com.yupi.luoso.common.ResultUtils;
import com.yupi.luoso.datasource.*;
import com.yupi.luoso.exception.BusinessException;
import com.yupi.luoso.exception.ThrowUtils;
import com.yupi.luoso.model.dto.post.PostEsDTO;
import com.yupi.luoso.model.dto.post.PostQueryRequest;
import com.yupi.luoso.model.dto.search.SearchRequest;
import com.yupi.luoso.model.dto.user.UserQueryRequest;
import com.yupi.luoso.model.entity.Picture;
import com.yupi.luoso.model.enums.SearchTypeEnum;
import com.yupi.luoso.model.vo.PostVO;
import com.yupi.luoso.model.vo.SearchVO;
import com.yupi.luoso.model.vo.UserVO;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 搜索门面
 */
@Component
@Slf4j
public class SearchFacade {

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    public SearchVO searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {

        String type = searchRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);

        String searchText = searchRequest.getSearchText();
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();

        // 如果为空 则搜索出所有数据
        if (searchTypeEnum == null) {
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                Page<UserVO> userVOPage = userDataSource.doSearch(searchText, current, pageSize);
                return userVOPage;
            });

            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                //获取 httpServletRequest对象并存储到Threadlocal中
                RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
                try {
                    //在异步执行的线程中取出 httpServletRequest 对象 并使用
                    return postDataSource.doSearch(searchText, current, pageSize);
                }finally {
                    //清空ThreadLocal中存储的HttpServletRequest对象
                    RequestContextHolder.resetRequestAttributes();
                }
            });

            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureDataSource.doSearch(searchText, 1, 10);
                return picturePage;
            });

            CompletableFuture.allOf(userTask, postTask, pictureTask).join();
            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();
                SearchVO searchVO = new SearchVO();
                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                searchVO.setPictureList(picturePage.getRecords());
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());
            return searchVO;
        }
    }
}
