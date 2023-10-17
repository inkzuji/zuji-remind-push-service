package com.zuji.remind.biz.controller;

import com.zuji.remind.biz.entity.MemorialDayTask;
import com.zuji.remind.biz.model.vo.MemorialDayTaskVO;
import com.zuji.remind.biz.service.common.MemorialDayTaskService;
import com.zuji.remind.common.api.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 提醒.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-20 21:41
 **/
@RequestMapping(value = "/api/memorialDay")
@RestController
public class MemorialDayTaskController {
    private final MemorialDayTaskService memorialDayTaskService;

    public MemorialDayTaskController(MemorialDayTaskService memorialDayTaskService) {
        this.memorialDayTaskService = memorialDayTaskService;
    }

    /**
     * 查询所有任务。
     */
    @GetMapping(value = "/list")
    public CommonResult<List<MemorialDayTaskVO.TaskVO>> list() {
        List<MemorialDayTask> taskList = memorialDayTaskService.listAll();
        List<MemorialDayTaskVO.TaskVO> list = taskList.stream()
                .map(MemorialDayTaskVO.TaskVO::from)
                .collect(Collectors.toList());
        return CommonResult.success(list);
    }
}
