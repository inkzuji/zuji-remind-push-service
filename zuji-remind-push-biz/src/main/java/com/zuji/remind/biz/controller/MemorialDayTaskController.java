package com.zuji.remind.biz.controller;

import com.zuji.remind.biz.dao.entity.MemorialDayTask;
import com.zuji.remind.biz.model.dto.MemorialDayTaskDTO;
import com.zuji.remind.biz.model.vo.MemorialDayTaskVO;
import com.zuji.remind.biz.repository.MemorialDayTaskRepository;
import com.zuji.remind.common.api.CommonResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

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
    private final MemorialDayTaskRepository memorialDayTaskRepository;

    public MemorialDayTaskController(MemorialDayTaskRepository memorialDayTaskRepository) {
        this.memorialDayTaskRepository = memorialDayTaskRepository;
    }

    /**
     * 查询所有任务。
     */
    @GetMapping(value = "/list")
    public CommonResult<List<MemorialDayTaskVO.TaskVO>> list() {
        List<MemorialDayTask> taskList = memorialDayTaskRepository.listAll();
        List<MemorialDayTaskVO.TaskVO> list = taskList.stream()
                .map(MemorialDayTaskVO.TaskVO::from)
                .collect(Collectors.toList());
        return CommonResult.success(list);
    }

    /**
     * 查询详情。
     */
    @GetMapping(value = "/detail/{id}")
    public CommonResult<MemorialDayTaskVO.TaskVO> detail(@PathVariable @Valid @NotNull(message = "查询ID不能为空") Long id) {
        MemorialDayTask memorialDayTask = memorialDayTaskRepository.getById(id);
        return CommonResult.success(MemorialDayTaskVO.TaskVO.from(memorialDayTask));
    }

    /**
     * 新增数据。
     */
    @PostMapping(value = "/add")
    public CommonResult<Void> addOnce(@Valid @RequestBody MemorialDayTaskDTO.SaveTaskDTO dto) {
        MemorialDayTask memorialDayTask = MemorialDayTaskDTO.SaveTaskDTO.to(dto);
        boolean result = memorialDayTaskRepository.insertOnce(memorialDayTask);
        return result ? CommonResult.success() : CommonResult.failed();
    }

    /**
     * 修改。
     */
    @PutMapping(value = "/{id}")
    public CommonResult<Void> updateOnce(@PathVariable @Valid @NotNull(message = "更新ID不能为空") Long id,
                                         @Valid @RequestBody MemorialDayTaskDTO.SaveTaskDTO dto) {
        MemorialDayTask memorialDayTask = MemorialDayTaskDTO.SaveTaskDTO.to(dto);
        boolean result = memorialDayTaskRepository.updateById(id, memorialDayTask);
        return result ? CommonResult.success() : CommonResult.failed();
    }

    /**
     * 删除。
     */
    @DeleteMapping(value = "/{id}")
    public CommonResult<Void> deleteOnce(@PathVariable @Valid @NotNull(message = "删除ID不能为空") Long id) {
        boolean result = memorialDayTaskRepository.deleteById(id);
        return result ? CommonResult.success() : CommonResult.failed();
    }
}
