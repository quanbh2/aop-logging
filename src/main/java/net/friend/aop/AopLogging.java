package net.friend.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AopLogging {

  private SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

  // Annotation
  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface MaskedParam {
    String maskedSpell();
  }

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface NoLogging {}

  // Pointcuts
  @Pointcut("execution(public * *(..))")
  private void anyPublicOperation() {}

  @Pointcut("within(net.friend.controller.*)")
  private void withinControllers() {}

  @Pointcut("within(net.friend.service.*)")
  private void withinServices() {}

  @Pointcut(
      "@annotation(org.springframework.web.bind.annotation.DeleteMapping) "
          + "|| @annotation(org.springframework.web.bind.annotation.GetMapping)"
          + "|| @annotation(org.springframework.web.bind.annotation.Mapping)"
          + "|| @annotation(org.springframework.web.bind.annotation.PatchMapping)"
          + "|| @annotation(org.springframework.web.bind.annotation.PostMapping)"
          + "|| @annotation(org.springframework.web.bind.annotation.PutMapping)"
          + "|| @annotation(org.springframework.web.bind.annotation.RequestMapping)")
  private void hasRequestMapping() {}

  @Pointcut("!@annotation(net.friend.aop.AopLogging.NoLogging)")
  private void logEnabled() {}

  // AOP Controller
  @Before("anyPublicOperation() && withinControllers() && hasRequestMapping() && logEnabled()")
  public void beforeControllersMappingMethod(JoinPoint joinPoint) {
    logBeforeMethodWithArgs(joinPoint);
  }

  @AfterReturning(
      value = "anyPublicOperation() && withinControllers() && hasRequestMapping() && logEnabled()",
      returning = "responseEntity")
  public void afterControllersMappingMethod(JoinPoint joinPoint, ResponseEntity responseEntity) {
    logAfterReturningController(joinPoint, responseEntity);
  }

  @AfterThrowing(value = "anyPublicOperation() && withinControllers() && logEnabled()", throwing = "throwable")
  public void afterRepoMethodThrow(JoinPoint joinPoint, Throwable throwable) {
    logAfterThrow(joinPoint, throwable);
  }

  // private Method

  private void logBeforeMethodWithArgs(JoinPoint joinPoint) {
    StringBuilder msgBuilder = new StringBuilder("before ");
    msgBuilder.append(jointPointName(joinPoint)).append(": ");

    Object[] args = joinPoint.getArgs();
    MethodSignature codeSignature = (MethodSignature) joinPoint.getSignature();
    int count = args.length;
    for (int i = 0; i < count; i++) {

      msgBuilder
          .append("(")
          .append(i + 1)
          .append(") ")
          .append(codeSignature.getParameterTypes()[i].getSimpleName());
      msgBuilder.append(" - ").append(codeSignature.getParameterNames()[i]);

      MaskedParam maskedParam =
          codeSignature.getMethod().getParameters()[i].getAnnotation(MaskedParam.class);

      if (maskedParam != null) {
        try {
          msgBuilder
              .append(" masked:")
              .append(spelExpressionParser.parseRaw(maskedParam.maskedSpell()).getValue(args[i]));
        } catch (Exception e) {
          // no-op
        }
      } else {
        msgBuilder.append(" - ").append(args[i]).append(" || ");
      }
    }

    log.info(msgBuilder.toString());
  }

  private void logAfterReturningController(JoinPoint joinPoint, ResponseEntity responseEntity) {
    log.info("after {}", jointPointName(joinPoint));
    log.info("Status: {}", responseEntity.getStatusCodeValue());
    log.info("Body: {}", responseEntity.getBody());
  }

  private String jointPointName(JoinPoint joinPoint) {
    return joinPoint.getSignature().toShortString();
  }

  private void logAfterThrow(JoinPoint joinPoint, Throwable throwable) {
    String rootCause = ExceptionUtils.getRootCauseMessage(throwable);
    String stackTrace = ExceptionUtils.getFullStackTrace(throwable);
    log.error("rootCause: {}",rootCause);
    log.error("stackTrace: {}",stackTrace);
  }
}
