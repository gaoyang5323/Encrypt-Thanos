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

1)yml configuration
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

2)Test case code


pojo:

```
public class Student {

    private int age;
    private String name;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

controller:

```
    @SeparateEncrypt
    @RequestMapping("test")
    public Object t3(@RequestBody(required = false) Student student) {
        student.setAge(18);
        student.setName("gaoyang2");
        return student;
    }
```


test:
```
    @Test
    public void test() throws Exception {
        Student s = new Student();
        s.setAge(28);
        s.setName("gaoyang");

        RsaEncryptHandler rsaEncryptHandler = new RsaEncryptHandler();
        rsaEncryptHandler.setPublicKey("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ94IIx/5qw6KipB9+y62l7Z6sIrmhpA/lrL5cXslXP7Iwa4ZeX2xHJhXlNRi6Eyv3nx67O+1kbTIe6rJuE1fe0CAwEAAQ==");
        rsaEncryptHandler.setPrivateKey("MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAn3ggjH/mrDoqKkH37LraXtnqwiuaGkD+WsvlxeyVc/sjBrhl5fbEcmFeU1GLoTK/efHrs77WRtMh7qsm4TV97QIDAQABAkBb9ArguUer/AYgQ9XQHZaZpxKlUDsV9HA2rugZjuhG7Zoq0J8sXZxtGt3GfS02OTJ5D7xHKAZ5FpLQpxwnS71JAiEAyilhNwKLSRmA0Ee/0s0EvKG7LJBjE1ymqXXGsX6s8mMCIQDJ8CP/LVzRIb1EuYow5nQ3C6EUh1TJUM98zKnlbcCXbwIgTZFXBb5qJyAr9r6w8XdMy/vaT50PBszT/c188XnDbjUCIQC3E9gOyPmVQJlvbSc0HjrOjOSE0Ay2V2VFJ+f/8PjiUQIhALCvioDG38FxtxWOFvN2kZvahrL33Ht23TnaZLJ70ceh");

        byte[] bytes = rsaEncryptHandler.encryptByPublicKey(new ObjectMapper()
                .writeValueAsString(s).getBytes("utf-8"), Base64Utils.decodeFromString(rsaEncryptHandler.getPublicKey()));
        OkHttpClient c = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), bytes);
        Request request = new Request.Builder()
                .url("http://localhost:8080/test")
                .post(requestBody)
                .build();
        Call call = c.newCall(request);
        Response execute = call.execute();
        byte[] bytes1 = execute.body().bytes();

        byte[] decode = rsaEncryptHandler.decryptByPublicKey(bytes1, Base64Utils.decodeFromString(rsaEncryptHandler.getPublicKey()));
        System.out.println(new String(new String(decode)));
    }
```
out:

```
{"age":18,"name":"gaoyang2"}
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