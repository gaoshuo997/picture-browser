package com.jimmy.req;

import lombok.Data;
import java.util.Set;

@Data
public class AssignUserRoleDTO {

    private Set<Long> roleIds;
}
