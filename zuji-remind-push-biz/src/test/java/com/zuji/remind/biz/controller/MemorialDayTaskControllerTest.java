package com.zuji.remind.biz.controller;

import com.zuji.remind.biz.dao.entity.MemorialDayTask;
import com.zuji.remind.biz.model.dto.MemorialDayTaskDTO.SaveTaskDTO;
import com.zuji.remind.biz.repository.MemorialDayTaskRepository;
import com.zuji.remind.common.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MemorialDayTaskControllerTest {
    private final MemorialDayTaskRepository repository = mock(MemorialDayTaskRepository.class);
    private final MemorialDayTaskController controller = new MemorialDayTaskController(repository);

    @Test
    void listAndDetailReturnTaskId() {
        MemorialDayTask task = new MemorialDayTask();
        task.setId(42L);
        when(repository.listAll()).thenReturn(List.of(task));
        when(repository.getById(42L)).thenReturn(task);

        assertEquals(42L, controller.list().getData().get(0).getId());
        assertEquals(42L, controller.detail(42L).getData().getId());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "abc", "1.5", "1,,3", "1,", ",1", "1, 2", "9223372036854775808"})
    void invalidFrequencyCannotBeAddedOrUpdated(String frequency) {
        SaveTaskDTO dto = enabledTask();
        dto.setRemindTimes(frequency);

        assertThrows(ApiException.class, () -> controller.addOnce(dto));
        assertThrows(ApiException.class, () -> controller.updateOnce(42L, dto));
        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "3", "1,3", "4", "email", "1,,2", "1,", ",2"})
    void unsupportedChannelsCannotBeAddedOrUpdated(String channels) {
        SaveTaskDTO dto = enabledTask();
        dto.setRemindWay(channels);

        assertThrows(ApiException.class, () -> controller.addOnce(dto));
        assertThrows(ApiException.class, () -> controller.updateOnce(42L, dto));
        verifyNoInteractions(repository);
    }

    @Test
    void enabledTaskPreservesSupportedFrequencyAndMultipleChannels() {
        SaveTaskDTO dto = enabledTask();
        dto.setRemindTimes("-1,0,1,7,9223372036854775807");
        dto.setRemindWay("1,2");
        when(repository.insertOnce(any())).thenReturn(true);

        assertEquals(200, controller.addOnce(dto).getCode());

        ArgumentCaptor<MemorialDayTask> task = ArgumentCaptor.forClass(MemorialDayTask.class);
        verify(repository).insertOnce(task.capture());
        assertEquals(dto.getRemindTimes(), task.getValue().getRemindTimes());
        assertEquals("1,2", task.getValue().getRemindWay());
    }

    @Test
    void disabledTaskCanOmitReminderConfiguration() {
        SaveTaskDTO dto = enabledTask();
        dto.setStatusRemind(0);
        dto.setRemindTimes(null);
        dto.setRemindWay(null);
        when(repository.insertOnce(any())).thenReturn(true);

        assertEquals(200, controller.addOnce(dto).getCode());
        verify(repository).insertOnce(any());
    }

    @Test
    void disabledTaskRejectsMalformedProvidedConfiguration() {
        SaveTaskDTO dto = enabledTask();
        dto.setStatusRemind(0);
        dto.setRemindTimes("broken");
        assertThrows(ApiException.class, () -> controller.addOnce(dto));

        dto.setRemindTimes("7");
        dto.setRemindWay("3");
        assertThrows(ApiException.class, () -> controller.addOnce(dto));
        verifyNoInteractions(repository);
    }

    private SaveTaskDTO enabledTask() {
        SaveTaskDTO dto = new SaveTaskDTO();
        dto.setEventType(1);
        dto.setName("生日");
        dto.setDateType(1);
        dto.setIsLeapMonth(0);
        dto.setMemorialDate("2000-01-01");
        dto.setStatusRemind(1);
        dto.setRemindTimes("7,1");
        dto.setRemindWay("1");
        return dto;
    }
}
