# Encrypt-Thanos `(springboot http transmission encryption and decryption framework, special edition jdk1.8 + support)`



## 1. Function introduction

1) Springboot, springcloud project quickly open front-end and back-end data transmission encryption

2) Provide multiple encryption methods (symmetric, asymmetric), support custom encryption methods

3) Simple integration, a @EnableEncrypt annotation can turn on global encryption rules, and supports @SeparateEncrypt class-level or method-level fine-grained control of encryption

4) yml configuration is simple and provides prompt description

Note: Currently only decryption of application / json data submission is supported; form submission is not decrypted!

------



## 二 .Integration

#### 1. Enable encryption

1) Write yml configuration file to enable the function

```
encrypt:
   type: rsa #Support base64 encoding, aes symmetric encryption, rsa asymmetric encryption, custom custom encryption method
   privateKey: #rsaPrivate key
   publicKey: #rsa
   debug: false #debug mode is true without encryption
   order: 1 #Encryption filter sequence number, if not filled, it is 0
   secret: #aes encryption key
```

3) Turn on global encryption (pseudo code)

This item must be written, and it must be written later when local encryption is turned on
```
@EnableEncrypt
@SpringBootApplication
public class Application {}
```
4) Enable local encryption:
```
@RestController
@SeparateEncrypt
public class Web {}

@PostMappint
@SeparateEncrypt
public Result get(@RequestBody Body body){}
```


#### 2.Integrated custom encryption mode

1) yml is configured as a custom method

```
encrypt:
  type: custom
```

2) Write custom encryption implementation

```
// Implement the EncryptHandler interface and register it with the spring container
@Component
public class EncryptCustom implements EncryptHandler {
// out attendance
     @Override
     public byte [] encode (byte [] content) {
         return content;
     }
     // Entry parameter decryption
     @Override
     public byte [] decode (byte [] content) {
         return content;
     }
}
```

#### 3. Generate RSA public and private keys

```
@Test
public void test() throws Exception {
    RsaKeyEntity rsaKeys = RsaEncryptHandler.getRsaKeys();
    String privateKey = rsaKeys.getPrivateKey();
    String publicKey = rsaKeys.getPublicKey();
}
```



------


## Three. Function Demo

#### 1.Integrated RSA encrypted transmission mode

1) yml configuration
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

2) Controller test code

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

3) Test case code

```
	@Test
        public void test () throws Exception {
        // Incoming parameters
            ControllerDemo.UserDemo userDemo = new ControllerDemo.UserDemo ();
            userDemo.setAge (27);
            userDemo.setName ("gaoyang");
            // public key encrypted transmission
            byte [] bytes = RSACoder.encryptByPublicKey (new ObjectMapper ()
            .writeValueAsString (userDemo) .getBytes ("utf-8"), Base64.decodeBase64 ("MFwwDQYJKo ...."));
     // Build http request parameters
            OkHttpClient c = new OkHttpClient ();
            RequestBody requestBody = RequestBody.create (MediaType.parse ("application / json; charset = UTF-8"), bytes);
            Request request = new Request.Builder ()
                    .url ("http: // localhost: 8888 / test")
                    .post (requestBody)
                    .build ();
            Call call = c.newCall (request);
            Response execute = call.execute ();
            // Get back end array of bytes
            byte [] resultByte = execute.body (). bytes ();
            // public key decryption
            byte [] result = RSACoder.decryptByPublicKey (resultByte, Base64.decodeBase64 ("MFwwDQYJKoZIhv ....."));
            System.out.println (new String (result));
        }
```


## Four. Springcloud integrated openfeign call encryption interface error reporting:

1) Producer
```
    @RestController
    public class MyController implements MyService {
        @Override
        @SeparateEncrypt
        @PostMapping("get")
        public Student get(@RequestBody(required = false) Student student) {
            if (student != null) {
                System.out.println("i am producer+" + student.getName() + student.getAge());
            }
            Student s = new Student();
            s.setAge(18);
            s.setName("producer");
            return s;
        }
    }
```

2) Consumers

Encryption and decryption processing classes for encoding and decoding in the same way as producers;

```
@FeignClient(value = "MECHANT", configuration = {MyClient.MyFeignConfig.class})
public interface MyClient extends MyService {

    @Override
    @PostMapping(value = "get", consumes = "application/json")
    Student get(@RequestBody Student student);


    @Configuration
    class MyFeignConfig {
        private static Gson gson = new Gson();
        private static Base64EncryptHandler base64EncryptHandler = new Base64EncryptHandler();

        class MyBase64DeCoder implements Decoder {
            @Override
            public Object decode(Response response, Type type) throws IOException, FeignException {
                try (InputStream inputStream = response.body().asInputStream()) {
                    byte[] b = new byte[inputStream.available()];
                    inputStream.read(b);
                    inputStream.close();
                    byte[] decode = base64EncryptHandler.decode(b);
                    return gson.fromJson(new String(decode, "UTF-8"), type);
                }
            }
        }

        class MyBase64EnCoder implements Encoder {
            @Override
            public void encode(Object o, Type type, RequestTemplate requestTemplate) throws EncodeException {
                String s = gson.toJson(o, type);
                byte[] encode = base64EncryptHandler.encode(s.getBytes());
                requestTemplate.body(encode, Charset.forName("UTF-8"));
            }
        }


        @Bean
        public Decoder myBase64Decoder() {
            return new MyBase64DeCoder();
        }

        @Bean
        public Encoder myBase64Encoder() {
            return new MyBase64EnCoder();
        }
    }
}
```