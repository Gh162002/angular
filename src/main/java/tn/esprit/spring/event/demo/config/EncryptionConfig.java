package tn.esprit.spring.event.demo.config;

import org.jasypt.contrib.org.apache.commons.codec_1_3.DecoderException;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EncryptionConfig {
    @Autowired
    private Environment environment;

    @Bean(name = "stringEncryptor")
    public StringEncryptor stringEncryptor() throws DecoderException{
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(environment.getProperty("jasypt.encryptor.password"));
        encryptor.setAlgorithm("PBEWithMD5AndDES"); // You can use other algorithms if needed
        encryptor.setSaltGenerator(new org.jasypt.salt.RandomSaltGenerator());
        return encryptor;
    }
}
