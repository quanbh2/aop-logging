package net.friend.controller;

import java.util.List;
import net.friend.aop.AopLogging.MaskedParam;
import net.friend.exception.IgnoreLoggingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @GetMapping("/testNoParam")
  public ResponseEntity testNoParam() {
    return new ResponseEntity("testNoParam", HttpStatus.OK);
  }

  @GetMapping("/testRawParam")
  public ResponseEntity testRawParam(
      @RequestParam("id") Long id, @RequestParam("name") String name)  throws Exception{

//    pushException();
    pushIgnoreLoggingException();

    return new ResponseEntity("testRawParam", HttpStatus.OK);
  }

  @GetMapping("/testMaskedParam")
  public ResponseEntity testMaskedParam(
      @MaskedParam(maskedSpell = "'size:' + size()") @RequestParam("name") List<String> name) {
    return new ResponseEntity("testMaskedParam", HttpStatus.OK);
  }

  private void pushException() {
    Integer.parseInt("abc");
  }

  private void pushIgnoreLoggingException() throws IgnoreLoggingException {
    System.out.println("Ignore logging exception was here");
    throw new IgnoreLoggingException();
  }
}
