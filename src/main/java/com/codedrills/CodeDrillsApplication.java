package com.codedrills;


import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
@EnableScheduling
public class CodeDrillsApplication {

  @Bean
  public RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    final TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
    final SSLContext sslContext = SSLContexts.custom()
            .loadTrustMaterial(null, acceptingTrustStrategy)
            .build();
    final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
    final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
            .register("https", sslsf)
            .register("http", new PlainConnectionSocketFactory())
            .build();

    final BasicHttpClientConnectionManager connectionManager =
            new BasicHttpClientConnectionManager(socketFactoryRegistry);
    final CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .build();

    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setHttpClient(httpClient);
    return new RestTemplate(requestFactory);
  }

  public static void main(String[] args) {
    SpringApplication.run(CodeDrillsApplication.class, args);
  }
}
