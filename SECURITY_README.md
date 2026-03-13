# Spring Security + JWT 权限校验实现

## 概述

本项目已集成 **Spring Security + JWT** 实现用户认证和权限校验。

## 功能特性

1. **JWT Token 认证**
   - 登录成功后生成 JWT Token
   - 每次请求携带 Token 进行认证
   - Token 包含用户ID和角色信息

2. **基于角色的权限控制 (RBAC)**
   - 支持 `@PreAuthorize` 注解控制方法访问权限
   - 支持 `hasRole`, `hasAnyRole` 等权限表达式
   - 提供 SecurityUtils 工具类获取当前用户信息

3. **安全异常处理**
   - 未认证用户访问受保护资源返回 401
   - 无权限用户访问资源返回 403
   - 统一 JSON 格式错误响应

## 项目结构

```
src/main/java/com/jimmy/security/
├── SecurityConfig.java              # Spring Security 核心配置
├── UserDetailsServiceImpl.java      # 用户详情服务实现
├── JwtAuthenticationEntryPoint.java # 认证失败处理器
├── JwtAccessDeniedHandler.java      # 权限不足处理器
└── SecurityUtils.java               # 安全工具类

src/main/java/com/jimmy/filter/
└── JwtAuthenticationFilter.java     # JWT 认证过滤器

src/main/java/com/jimmy/controller/
├── AuthController.java              # 认证接口（登录、注册、登出）
└── TestSecurityController.java      # 安全测试接口
```

## API 接口

### 公开接口（无需认证）

| 接口 | 方法 | 说明 |
|------|------|------|
| `/auth/register` | POST | 用户注册 |
| `/auth/login` | POST | 用户登录 |
| `/auth/public/**` | - | 其他公开接口 |

### 认证接口（需要登录）

| 接口 | 方法 | 说明 |
|------|------|------|
| `/auth/logout` | POST | 用户登出 |
| `/auth/me` | GET | 获取当前用户信息 |
| `/auth/refresh` | POST | 刷新 Token |

### 权限测试接口

| 接口 | 方法 | 所需权限 |
|------|------|----------|
| `/api/test/public` | GET | 无（公开） |
| `/api/test/user` | GET | 已登录 |
| `/api/test/admin` | GET | ADMIN 角色 |
| `/api/test/any-role` | GET | USER 或 ADMIN 角色 |
| `/api/test/current-user` | GET | 已登录 |

## 使用示例

### 1. 登录获取 Token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginName": "admin",
    "password": "123456"
  }'
```

响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "result": true,
    "message": "登录成功",
    "user": {
      "id": 1,
      "loginName": "admin",
      ...
    }
  }
}
```

### 2. 使用 Token 访问受保护接口

```bash
curl -X GET http://localhost:8080/api/test/user \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 3. 在代码中获取当前用户

```java
@RestController
public class MyController {

    @GetMapping("/my-endpoint")
    public Result<?> myMethod() {
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();

        // 获取当前用户详情
        UserDetails userDetails = SecurityUtils.getCurrentUserDetails();

        // 检查是否有特定角色
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");

        // 业务逻辑...

        return Result.success();
    }
}
```

### 4. 在控制器上使用权限注解

```java
@RestController
@RequestMapping("/admin")
public class AdminController {

    // 只有 ADMIN 角色可以访问
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public Result<?> listUsers() {
        // ...
        return Result.success();
    }

    // USER 或 ADMIN 角色都可以访问
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/profile")
    public Result<?> getProfile() {
        // ...
        return Result.success();
    }

    // 需要特定权限（非角色）
    @PreAuthorize("hasAuthority('user:delete')")
    @DeleteMapping("/users/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        // ...
        return Result.success();
    }
}
```

## 配置说明

### JWT 配置 (application.yml)

```yaml
jwt:
  secret-key: your-secret-key-here  # JWT 密钥（生产环境应使用环境变量）
  expire-time: 7200000              # Token 过期时间（毫秒），默认 2 小时
```

### Spring Security 配置

主要配置在 `SecurityConfig.java` 中：

- 公开接口：`/auth/**`, `/public/**`
- 静态资源：`/*.html`, `/css/**`, `/js/**` 等
- 其他接口：需要认证

## 注意事项

1. **生产环境配置**
   - 修改 `application.yml` 中的 JWT 密钥
   - 启用 HTTPS
   - 配置 Redis 存储已注销的 Token

2. **Token 刷新**
   - 前端应在 Token 过期前调用刷新接口
   - 或实现自动刷新机制

3. **权限数据初始化**
   - 首次运行时需要创建角色数据
   - 为用户分配适当的角色

## 故障排查

### 401 Unauthorized（未认证）

- 检查请求头是否包含 `Authorization: Bearer {token}`
- 检查 Token 是否过期
- 检查 Token 格式是否正确

### 403 Forbidden（无权限）

- 检查用户是否拥有访问该资源所需的角色
- 检查方法上的 `@PreAuthorize` 注解

### Token 解析失败

- 检查 JWT 密钥配置是否正确
- 检查 Token 是否被篡改