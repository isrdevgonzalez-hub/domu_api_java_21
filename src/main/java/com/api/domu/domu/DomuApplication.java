package com.api.domu.domu;

import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Properties;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
public class DomuApplication {

    public static void main(String[] args) {

        System.out.println("=== Inicio ===");

        // revisa_bbdd();
        // primera_iteracion();
        // segunda_iteracion();
        SpringApplication.run(DomuApplication.class, args);
    }

    private static String mask(String value) {
        return value == null || value.isBlank()
                ? "<vacío>"
                : "********";
    }

    private static void revisa_bbdd() {
        // String host = "host.docker.internal";
        // String host = "127.0.0.1";
        String host = "192.168.240.1";
        int port = 3306;

        String url = "jdbc:mysql://" + host + ":3306/api_java_clientes_NPM"
                + "?useSSL=false"
                + "&allowPublicKeyRetrieval=true"
                + "&serverTimezone=UTC";

        String user = "api_java_clientes_npm";
        String password = "Cuatico1234*";
        System.out.println("========== URL  ==========\t" + url);
        System.out.println("========== user  ==========\t" + user);
        System.out.println("========== password  ==========\t" + password);

        try {

            System.out.println("========== PRUEBA DNS ==========");

            InetAddress address = InetAddress.getByName(host);

            System.out.println("Host encontrado: " + address.getHostAddress());

            System.out.println();

            System.out.println("========== PRUEBA SOCKET ==========");
            try {

                System.out.println("host: " + host);
                System.out.println("port: " + port);
                System.out.println("user: " + user);
                Socket socket = new Socket(host, port);
                System.out.println("Conexión TCP OK");

                socket.close();
            } catch (Exception e) {
                System.out.println("FALLA TCP " + e.toString());
            }

            System.out.println();

            System.out.println("========== PRUEBA JDBC ==========");
            try {
                Connection connection
                        = DriverManager.getConnection(url, user, password);
                connection.close();
            } catch (Exception e) {
                System.out.println("FALLA JDBC " + e.toString());
            }
            System.out.println("Conexión JDBC OK");

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    private static void primera_iteracion() {
        SpringApplication application
                = new SpringApplication(DomuApplication.class);
        application.addInitializers(context -> {
            ConfigurableEnvironment environment
                    = context.getEnvironment();

            System.out.println("=== CONFIGURACIÓN RESUELTA ===");
            System.out.println("Perfiles activos: "
                    + Arrays.toString(environment.getActiveProfiles()));

            System.out.println("spring.datasource.url: "
                    + environment.getProperty("spring.datasource.url"));

            System.out.println("spring.datasource.username: "
                    + environment.getProperty("spring.datasource.username"));

            System.out.println("URL_BBDD: "
                    + environment.getProperty("URL_BBDD"));

            System.out.println("USR_BBDD: "
                    + environment.getProperty("USR_BBDD"));

            System.out.println("PSW_BBDD: "
                    + mask(environment.getProperty("PSW_BBDD")));
        });

    }

    private static void segunda_iteracion() {

        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));

        Properties props = yaml.getObject();

        props.forEach((k, v) -> System.out.println(k + " = " + v));

    }

}
