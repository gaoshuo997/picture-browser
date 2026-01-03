package com.jimmy.common.web;

import lombok.Data;

@Data
public class ApplicationResponseEntity<T> {

    /**
     * status of any api call.
     */
    private ActionStatus actionStatus = ActionStatus.SUCCESS;

    /**
     * api response content.
     */
    private T content;
}
