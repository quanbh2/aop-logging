package net.friend.controller;

import java.util.ArrayList;
import java.util.List;

import net.friend.model.TestModel;
import net.friend.model.TestObject;
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
    List<String> list = new ArrayList<>();
    list.add("string 1");
    list.add("String 2");
    TestObject object = new TestObject("object name");
    TestModel testModel = new TestModel("test name", 12, "test add", list, object);
    return new ResponseEntity(testModel , HttpStatus.OK);
  }

  @GetMapping("/testMaskedParam")
  public ResponseEntity testMaskedParam(
      @MaskedParam(maskedSpell = "'size:' + size()") @RequestParam("name") List<String> name) {

    return new ResponseEntity("testMaskedParam", HttpStatus.OK);
  }

  @GetMapping("/testThrowException")
  public ResponseEntity testThrowException() {
    pushException();

    return new ResponseEntity("testThrowException", HttpStatus.OK);
  }

  @GetMapping("/testThrowIgnoreLoggingException")
  public ResponseEntity testThrowIgnoreLoggingException() throws IgnoreLoggingException {
    pushIgnoreLoggingException();

    return new ResponseEntity("pushIgnoreLoggingException", HttpStatus.OK);
  }

  private void pushException() {
    Integer.parseInt("abc");
  }

  private void pushIgnoreLoggingException() throws IgnoreLoggingException {
    System.out.println("Ignore logging exception was here");
    throw new IgnoreLoggingException();
  }
}
