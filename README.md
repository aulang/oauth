### 1. 登录请求方式
`密码（password）均为SHA256摘要，禁止明文传输`
#### 1. 认证码模式
1. 路径: /authorize?client_id=xxxxxx&response_type=code&redirect_uri=xxxxxx&scope=xxxxxx&state=xxxxxx
2. 方式：get
3. 参数说明
    ```json
    {
        "client_id": "xxxxxx",      // 客户端ID
        "response_type": "code",    // 固定值，认证码模式为code
        "redirect_uri": "xxxxxx",   // 认证成功重定向uri，后台匹配验证，只设一个可以不传，支持正则配置
        "scope": "xxxxxx",          // 授权范围，文档约定
        "state": "xxxxxx"           // 若有则原样返回
    }
    ```
4. 重定向返回redirect_uri（已有?不会再加一个）
- 认证成功：redirect_uri?code=xxxxx-xxxx-xxxx-xxxxx&state=xxxxxx
- 认证失败：redirect_uri?error=reject&state=xxxxxx  
`如已单点登录，则重定向返回：redirect_uri?access_token=xxxxx-xxxx-xxxx-xxxxx&expires_in=xxx&state=xxxxxx`

5. 根据code获取Token
    1. 路径：/token
    2. 方式：post
    3. 参数说明
    ```json
    {
        "client_id": "xxxxxx",                      // 客户端ID
        "grant_type": "authorization_code",         // 固定值，认证码模式为authorization_code
        "code": "xxxxxx",                           // 上一步返回的code
        "client_secret": "xxxxx-xxxx-xxxx-xxxxx",   // 授权给客户端的密钥
        "redirect_uri": "xxxxxx",                   // 与之前的redirect_uri须一致
    }
    ```
    4. 响应信息
    正常返回：
    ```json
    {
        "access_token": "xxxxx-xxxx-xxxx-xxxxx",    // access_token
        "refresh_token": "xxxxx-xxxx-xxxx-xxxxx",   // refresh_token
        "expires_in": 86400                         // access_token失效时间，单位秒
    }
    ```
    异常返回（状态码400）：
    ```json
    {
        "error": "无效的客户端 or 未授权的grantType or code错误/不能为空 or client_secret错误 or redirect_uri不匹配"
    }
    ```

#### 2. 简化模式
1. 路径: /authorize?client_id=xxxxxx&response_type=token&redirect_uri=xxxxxx&scope=xxxxxx&state=xxxxxx
2. 方式：get
3. 参数说明
    ```json
    {
        "client_id": "xxxxxx",      // 客户端ID
        "response_type": "token",   // 固定值，认证码模式为token
        "redirect_uri": "xxxxxx",   // 认证成功重定向uri，后台匹配验证，只设一个可以不传，支持正则配置
        "scope": "xxxxxx",          // 授权范围，文档约定
        "state": "xxxxxx"           // 若有则原样返回
    }
    ```
4. 重定向返回redirect_uri（已有?不会再加一个）
- 认证成功：redirect_uri?access_token=xxxxx-xxxx-xxxx-xxxxx&expires_in=xxx&state=xxxxxx
- 认证失败：redirect_uri?error=reject&state=xxxxxx  
`如已单点登录，则重定向返回：redirect_uri?access_token=xxxxx-xxxx-xxxx-xxxxx&expires_in=xxx&state=xxxxxx`

#### 3. 密码模式（账号密码获取token）
1. 路径: /token
2. 方式：post
3. 参数说明
    ```json
    {
        "client_id": "xxxxxx",      // 客户端ID
        "grant_type": "password",   // 固定值，密码模式为password
        "username": "xxxxxx",       // 用户名
        "password": "xxxxxx"        // 密码
    }
    ```
4. 响应信息  
    正常返回：
    ```json
    {
        "access_token": "xxxxx-xxxx-xxxx-xxxxx",    // access_token
        "refresh_token": "xxxxx-xxxx-xxxx-xxxxx",   // refresh_token
        "expires_in": 86400                         // access_token失效时间，单位秒
    }
    ```
    异常返回：
    ```json
    {
        "error": "错误信息"
    }
    ```    
    | 状态码 | 错误信息 | 备注 |  
    |:-----:|:----|:----|
    | 401 | 账号或密码错误 | 账号或密码错误 |
    | 403 | 账号被锁定，请申诉解锁 | 账号被锁定，请申诉解锁 |
    | 406 | 密码过期，必须修改密码 | 密码过期，必须修改密码 |

#### 4. 凭证模式
1. 路径: /token
2. 方式：post
3. 参数说明
    ```json
    {
        "client_id": "xxxxxx",                  // 客户端ID
        "grant_type": "client_credentials",     // 固定值，凭证模式为client_credentials
        "client_secret": "xxxxxx"               // 客户端密钥
    }
    ```
4. 响应信息  
    正常返回：
    ```json
    {
        "access_token": "xxxxx-xxxx-xxxx-xxxxx",    // access_token
        "refresh_token": "xxxxx-xxxx-xxxx-xxxxx",   // refresh_token
        "expires_in": 86400                         // access_token失效时间，单位秒
    }
    ```
    异常返回（状态码400）：
    ```json
    {
        "error": "client_secret错误"
    }
    ```  

### 2. 手机验证码登录

#### 1. 发送手机验证码
1. 路径: /api/captcha
2. 方式：post
3. 参数：
    ```json
    {
        "client_id": "xxxxxx",    //客户端ID
        "mobile": "xxxxxxxxxxx"   //手机号码
    }
    ```
4. 响应格式：text  

    | 状态码 | 响应体 | 备注 |  
    |:-----:|:----|:----|
    | 200 | {id} | 请求ID |
    | 400 | 参数不合法 | client_id或者mobile错误 |
    | 400 | 用户未注册 |  |
    | 500 | 发送验证码失败 |  |

#### 2. 手机验证码登录
1. 路径: /api/token
2. 方式：post
3. 参数：
    ```json
    {
        "authorize_id": "xxx",       //第一步返回的请求ID
        "mobile": "xxxxxxxxxxx",     //手机号码
        "captcha": "xxx"             //手机验证码
    }
    ```
4. 响应格式：json   
    1. 状态码: 200  
        ```json
        {
            "access_token": "xxxxx-xxxx-xxxx-xxxxx",     // access_token
            "refresh_token": "xxxxx-xxxx-xxxx-xxxxx",    // refresh_token
            "expires_in": 86400                          // access_token失效时间，单位秒
        }
        ```
    2. 状态码: 400  
        ```json
        {
            "error": "验证码已失效 or 手机号码不匹配 or 验证码错误 or 无效的客户端"
        }
        ```

### 3. 账号密码获取token
1. 路径: /token
2. 方式：post
3. 参数：
    ```json
    {
        "client_id": "xxxxxx",       //客户端ID
        "grant_type": "password",    //固定值
        "username": "xxx",           //账号
        "password": "xxx"            //密码       
    }
    ```
4. 响应格式：json  
    1. 状态码: 200
        ```json
        {
            "access_token": "xxxxx-xxxx-xxxx-xxxxx",    // access_token
            "refresh_token": "xxxxx-xxxx-xxxx-xxxxx",   // refresh_token
            "expires_in": 86400                         // access_token失效时间，单位秒
        }
        ```
    2. 状态码: 400
        ```json
        {
            "error": "无效的客户端 or 未授权的grantType or 账号和密码不能为空"
        }
        ```
    3. 状态码: 401
        ```json
        {
            "error": "账号或密码错误"
        }
        ```
    4. 状态码: 403
        ```json
        {
            "error": "账号被锁定，请申诉解锁"
        }
        ```
    5. 状态码: 406
        ```json
        {
            "error": "密码过期，必须修改密码"
        }
        ```

### 4. 用Token获取用户信息（密码模式）
1. 路径: /api/profile
2. 方式：get
3. 参数：
    1. 方式：Bearer Token
    2. 位置：HTTP HEAD
    4. 参数名：authorization
    3. 示例：authorization: "Bearer xxxxx-xxxx-xxxx-xxxxx"
4. 响应格式：json  
    1. 状态码: 200
        ```json
        {
            "id": "xxxxxx",
            "nickname": "xxxxx"     //用户昵称，可为null
        }
        ```
    2. 状态码: 403
        ```json
        {
            "error": "无效的access_token or 账号不存在"
        }
        ```

### 5. 刷新Token
1. 路径: /token
2. 方式：post
3. 参数：
    ```json
    {
        "client_id": "xxxxxx",                      //客户端ID
        "grant_type": "refresh_token",              //固定值
        "refresh_token": "xxxxx-xxxx-xxxx-xxxxx",   //refresh_token
    }
    ```
4. 响应格式：json  
    1. 状态码: 200
        ```json
        {
            "access_token": "xxxxx-xxxx-xxxx-xxxxx",     // 新值，刷新后的access_token，原值失效
            "refresh_token": "xxxxx-xxxx-xxxx-xxxxx",    // 原值
            "expires_in": 86400                          // 新access_token失效时间，单位秒
        }
        ```
    2. 状态码: 400
        ```json
        {
            "error": "无效的客户端 or 未授权的grantType or refresh_token不能为空 or 无效的refresh_token"
        }
        ```

### 6. 单点登出
`简化模式才能实现单点登录`
1. 路径: /logout?redirect_uri=xxxxxx
2. 方式：get
3. 参数：
    ```json
    {
        "redirect_uri": "xxxxxx"    //单点登出后重定向页面，不传为默认为'/'
    }
    ```