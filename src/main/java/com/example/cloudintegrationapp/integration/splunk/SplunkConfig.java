package com.example.cloudintegrationapp.integration.splunk;

import com.splunk.HttpService;
import com.splunk.SSLSecurityProtocol;
import com.splunk.Service;
import com.splunk.ServiceArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SplunkConfig {

    @Value("${splunk.host}")
    private String host;

    @Value("${splunk.port}")
    private int port;

    @Value("${splunk.username}")
    private String username;

    @Value("${splunk.password}")
    private String password;

//    @Bean
//    public Service splunkService() {
//        HttpService.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);
//
//        ServiceArgs loginArgs = new ServiceArgs();
//        loginArgs.setHost(host);
//        loginArgs.setPort(port);
//        loginArgs.setUsername(username);
//        loginArgs.setPassword(password);
//
//        return Service.connect(loginArgs);
//    }
}
