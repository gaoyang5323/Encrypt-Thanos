# Encrypt-Thanos `(springboot http传输加解密框架,灭霸特别版 jdk1.8+支持)`



## 一.功能介绍

1) springboot,springcloud项目快速开启前后端数据传输加密

2) 提供多种加密方式(对称,非对称),支持自定义加密方式

3) 集成简单,一个@EnableEncrypt注解即可开启全局加密规则,并支持@SeparateEncrypt类级别或方法级别细粒度控制加密

4) yml配置简单,并且提供提示描述

注意:目前仅支持解密application/json数据提交方式;表单提交不进行解密!

------



## 二.集成方式

#### 1.开启加密功能

1) 下载源码,maven打包发布到本机,使用依赖方式或jar包方式集成

```
	<dependency>
            <groupId>com.kakuiwong</groupId>
            <artifactId>Encrypt-Thanos</artifactId>
            <version>1.0</version>
        </dependency>
```

2) 编写yml配置文件开启功能

```
encrypt:
  type: rsa       #支持base64编码,aes对称加密,rsa非对称加密,custom自定义加密方式
  privateKey:     #rsa私钥
  publicKey:      #rsa公钥
  debug: false    #debug模式为true不开启加密
  order: 1        #加密过滤器顺序号,不填则为0
  secret:         #aes加密方式秘钥
```

3) 开启全局加密(伪代码)

此项必写,后面开启局部加密也要写
```
@EnableEncrypt
@SpringBootApplication
public class Application {}
```
4) 开启局部加密方式:
```
@RestController
@SeparateEncrypt
public class Web {}

@PostMappint
@SeparateEncrypt
public Result get(@RequestBody Body body){}
```


#### 2.集成自定义加密模式

1) yml配置为自定义方式

```
encrypt:
  type: custom
```

2) 编写自定义加密实现

```
//实现EncryptHandler接口并注册到spring容器
@Component
public class EncryptCustom implements EncryptHandler {
	//出参加密
    @Override
    public byte[] encode(byte[] content) {
        return content;
    }
    //入参解密
    @Override
    public byte[] decode(byte[] content) {
        return content;
    }
}
```

#### 3.生成RSA公钥及私钥

```
@Test
public void test() throws Exception {
    RsaKeyEntity rsaKeys = RsaEncryptHandler.getRsaKeys();
    String privateKey = rsaKeys.getPrivateKey();
    String publicKey = rsaKeys.getPublicKey();
}
```



------



## 三.功能演示

#### 1.集成RSA加密传输模式

1) yml配置

```
server:
  port: 8888
encrypt:
  type: rsa
  privateKey:
    MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAmsk/1OfysroyVE8vl9e........
  publicKey:
    MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJrJP9Tn8rK6MlRP........
```

2) controller测试代码

```
@SeparateEncrypt
@RestController
public class ControllerDemo {
	
	@Data
    public static class UserDemo {
        private String name;
        private Integer age;
    }

    @ResponseBody
    @PostMapping({"test"})
    public Object tt(@RequestBody(required = false) UserDemo userDemo) {
        System.out.println(userDemo);
        return userDemo;
    }
}

```

3)测试用例代码

```
	@Test
    public void test() throws Exception {
    	//传入参数
        ControllerDemo.UserDemo userDemo = new ControllerDemo.UserDemo();
        userDemo.setAge(27);
        userDemo.setName("gaoyang");
        //公钥加密传输
        byte[] bytes = RSACoder.encryptByPublicKey(new ObjectMapper()
        .writeValueAsString(userDemo).getBytes("utf-8"),Base64.decodeBase64("MFwwDQYJKo...."));
 		//构建http请求参数
        OkHttpClient c = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), bytes);
        Request request = new Request.Builder()
                .url("http://localhost:8888/test")
                .post(requestBody)
                .build();
        Call call = c.newCall(request);
        Response execute = call.execute();
        //得到后端返回字节数组
        byte[] resultByte = execute.body().bytes();
        //公钥解密
        byte[] result = RSACoder.decryptByPublicKey(resultByte,Base64.decodeBase64("MFwwDQYJKoZIhv....."));
        System.out.println(new String(result));
    }
```