//import io.vertx.core.DeploymentOptions;
//import io.vertx.core.VertxOptions;
//import io.vertx.core.http.HttpServerOptions;
//import io.vertx.ext.web.client.WebClientOptions;
//import io.vertx.micrometer.MicrometerMetricsOptions;
//import io.vertx.mysqlclient.MySQLConnectOptions;
//import io.vertx.redis.client.RedisOptions;
//import io.vertx.sqlclient.PoolOptions;
//import org.springframework.aop.target.PoolingConfig;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//
//public class Generator {
//  @ConfigurationProperties("vertx-boot.core.vertx-options")
//  @Bean public VertxOptions vertxOptions() {
//    return new VertxOptions();
//  }
//
//  @ConfigurationProperties("vertx-boot.core.vertx-options.metrics-options")
//  @Bean public MicrometerMetricsOptions metricsOptions() {
//    return new MicrometerMetricsOptions();
//  }
//
//  @ConfigurationProperties("vertx-boot.http.client.options")
//  @Bean public WebClientOptions httpClientOptions() {
//    return new WebClientOptions();
//  }
//
//  @ConfigurationProperties("vertx-boot.http.server.deployment-options")
//  @Bean public DeploymentOptions httpDeploymentOptions(VertxOptions vertxOptions) {
//    return new DeploymentOptions().setInstances(vertxOptions.getEventLoopPoolSize());
//  }
//
//  @ConfigurationProperties("vertx-boot.http.server.options")
//  @Bean public HttpServerOptions httpServerOptions() {
//    return new HttpServerOptions();
//  }
//
//  @ConfigurationProperties("vertx-boot.redis.options")
//  @Bean public RedisOptions redisOptions() {
//    return new RedisOptions();
//  }
//
//  @ConfigurationProperties("vertx-boot.mysql.connection-options")
//  @Bean public MySQLConnectOptions mySQLConnectOptions() {
//    return new MySQLConnectOptions();
//  }
//
//  @ConfigurationProperties("vertx-boot.mysql.pool-options")
//  @Bean public PoolOptions poolOptions() {
//    return new PoolOptions();
//  }
//}
