package net.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Slf4j
@SpringBootApplication
public class AopLoggingApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(AopLoggingApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {

    String pass1  = BCrypt.hashpw("12345678", BCrypt.gensalt(12));
    log.info("pass1 : {}", pass1);

    log.info(BCrypt.hashpw("12345678", BCrypt.gensalt(12)).replace("$2a$", "$2y$"));

    log.info(BCrypt.hashpw("12345678", BCrypt.gensalt(12)).replace("$2a$", "$2y$"));


  }
}
