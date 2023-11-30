package com.zuji.remind.biz.controller;

import com.zuji.remind.biz.entity.MemorialDayTask;
import com.zuji.remind.biz.model.dto.MemorialDayTaskDTO;
import com.zuji.remind.biz.model.vo.MemorialDayTaskVO;
import com.zuji.remind.biz.service.db.MemorialDayTaskService;
import com.zuji.remind.common.api.CommonResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提醒.
 *
 * @author inkzuji@gmail.com
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

    /**
     * 查询详情。
     */
    @GetMapping(value = "/detail/{id}")
    public CommonResult<MemorialDayTaskVO.TaskVO> detail(@Valid @NotNull(message = "查询ID不能为空") @PathVariable("id") Long id) {
        MemorialDayTask memorialDayTask = memorialDayTaskService.getById(id);
        return CommonResult.success(MemorialDayTaskVO.TaskVO.from(memorialDayTask));
    }

    /**
     * 新增数据。
     */
    @PostMapping(value = "/add")
    public CommonResult<Void> addOnce(@Valid @RequestBody MemorialDayTaskDTO.SaveTaskDTO dto) {
        MemorialDayTask memorialDayTask = MemorialDayTaskDTO.SaveTaskDTO.to(dto);
        boolean result = memorialDayTaskService.insertOnce(memorialDayTask);
        return result ? CommonResult.success() : CommonResult.failed();
    }

    /**
     * 修改。
     */
    @PutMapping(value = "/{id}")
    public CommonResult<Void> updateOnce(@Valid @NotNull(message = "更新ID不能为空") @PathVariable("id") Long id,
                                         @Valid @RequestBody MemorialDayTaskDTO.SaveTaskDTO dto) {
        MemorialDayTask memorialDayTask = MemorialDayTaskDTO.SaveTaskDTO.to(dto);
        boolean result = memorialDayTaskService.updateById(id, memorialDayTask);
        return result ? CommonResult.success() : CommonResult.failed();
    }

    /**
     * 删除。
     */
    @DeleteMapping(value = "/{id}")
    public CommonResult<Void> deleteOnce(@Valid @NotNull(message = "删除ID不能为空") @PathVariable("id") Long id) {
        boolean result = memorialDayTaskService.deleteById(id);
        return result ? CommonResult.success() : CommonResult.failed();
    }
}
