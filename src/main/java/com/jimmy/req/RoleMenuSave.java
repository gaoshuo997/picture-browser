package com.jimmy.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Data
public class RoleMenuSave {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @NotEmpty(message = "菜单ID不能为空")
    private Set<Long> menuIds;
}
