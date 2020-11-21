## Spring Boot with Redis Cache, along with RedisSession implementation, 
Spring boot rest api with RedisCache and RedisSession to track the session. 
Redis instance using in docker container to avoid installation. Docker compose to up both service and redis instance under same network.

What is Redis Cache ?
1. RedisCache is an open source in-memory data structure store, used as database cache or message broker.
2. Redis has build-in replication, LRU (Less Frequent Use) eviction, transactions
3. Redis Cluster provides a way to run a Redis installation where data is automatically share across multiple nodes.

What is RedisSession/HttpSession ?
As you know HTTPSession is a stateless protocol. All requests and responses are independent. 
To keep track the client's activity across multiple requests.

Use Case:\
You a multiple instances of MicroServices behind a load balancer and you need the session to be\
maintained for the user after successfully authorization, regardless of which instance is executing the request.

Dependencies\
build.gradle
```groovy
        implementation 'org.springframework.boot:spring-boot-starter-actuator'
        implementation 'org.springframework.session:spring-session-core'
        implementation 'org.springframework.session:spring-session-data-redis'
        implementation 'org.springframework.boot:spring-boot-starter-data-redis'
        implementation 'redis.clients:jedis'
        implementation 'org.springframework.boot:spring-boot-starter-web'
        compileOnly 'org.projectlombok:lombok'
        annotationProcessor 'org.projectlombok:lombok'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
        implementation 'org.apache.commons:commons-pool2:2.0'
```
gradle.properties\
````properties
springBootVersion=2.4.0
micrometer=1.5.1
oauth2Version=2.1.0.RELEASE
org.gradle.jvmargs=-Xmx2g -XX:MaxPermSize=256m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
gradleWrapperUrl=https\://services.gradle.org/distributions/gradle-6.3-all.zip
````
Enable Redis configuration 

```java
@Configuration
@ConditionalOnExpression(
        "${redis.enabled:true}"
)
@Slf4j
@EnableCaching
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = RedisConfiguration.HTTPSESSION_MAX_INTERVAL)
public class RedisConfiguration {

    public static final int HTTPSESSION_MAX_INTERVAL = 900;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName("redis-server");
        factory.setPort(6379);
        factory.setUsePool(true);
        factory.getPoolConfig().setMaxIdle(30);
        factory.getPoolConfig().setMinIdle(10);
        return factory;
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setValueSerializer(new GenericToStringSerializer(Object.class));
        redisTemplate.setExposeConnection(true);
        return redisTemplate;
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(jedisConnectionFactory())
                .cacheDefaults(redisCacheConfig())
                .build();
        redisCacheManager.setTransactionAware(true);
        return redisCacheManager;
    }

    private RedisCacheConfiguration redisCacheConfig() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues();
        //.entryTtl(Duration.ofSeconds(10));
        redisCacheConfiguration.usePrefix();
        return redisCacheConfiguration;
    }
}

```
@ConditionalOnExpression helps to apply configuration based on profiles. For example this configration shouldn't apply at the time unit test cases
@ConditionalOnExpression(
        "${redis.enabled:true}"
)

```yaml
spring:
  profiles:
    - local
  session:
    store-type: redis
  cache:
    type: redis
    redis:
      time-to-live: 18000 # 30 minutes
      use-key-prefix: true
      cache-null-values: false
    cache-names:
      - user-session, UserProfile
  redis:
    host: redis-server
    port: 6379
    jedis:
      pool:
        max-active: 7
        max-idle: 8
        min-idle: 2
        max-wait: 1ms

redis:
  enabled: true

```

Docker compose files combined both service and redis instance.
```yaml
version: '3'
services:
  springboot-rediscache-api:
    build:
      context: .
      args:
        version: 0.0.1
      dockerfile: Dockerfile
    depends_on:
      - redis-server
    ports:
      - "8080:8080"
    networks:
      - spring-boot-redis-network
    links:
      - redis-server
  redis-server:
    image: redis
    command: [ "redis-server", "--protected-mode", "no" ]
    ports:
      - "6379:6379"
    networks:
      - spring-boot-redis-network

networks:
  spring-boot-redis-network:
    driver: bridge
```

Note: Use the Terminal in your idea or run the below commands at your project root.
```commandline
% ./gradlew clean build
% docker-compose up --build
```
Run the curl command to see the output
```commandline
% curl -v http://localhost:8080/api/v1/redis-api/2
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /api/v1/redis-api/2 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.64.1
> Accept: */*
> 
< HTTP/1.1 200 
< Set-Cookie: SESSION=M2JlMDEwN2ItYmQwZi00ODJiLWI4YjUtM2IzZTVjODU2OTVk; Path=/; HttpOnly; SameSite=Lax
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Sat, 21 Nov 2020 12:39:40 GMT

```