package com.TaskManager.models.dto;

public record TaskSummaryDto (
        long taskInvolved,
        long taskManage,
        long inprogressTask,
        long completedTask
){
}
