package sejong.coffee.yun.webhook;

import lombok.RequiredArgsConstructor;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackField;
import net.gpedro.integrations.slack.SlackMessage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Aspect
@Component
@RequiredArgsConstructor
@Profile(value = {"local", "dev"})
public class SlackNotificationAspect {

    private final SlackApi slackApi;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Around("@annotation(sejong.coffee.yun.custom.annotation.SlackNotification)")
    public void slackNotification(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        proceedingJoinPoint.proceed();
//        threadPoolExecutor.execute(() -> sendSlackMessage(proceedingJoinPoint));
        sendSlackMessage(proceedingJoinPoint);
    }

    private void sendSlackMessage(ProceedingJoinPoint proceedingJoinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String httpMethod = request.getMethod();
        SlackAttachment slackAttachment = new SlackAttachment();
        slackAttachment.setFallback(httpMethod);
//        slackAttachment.setFallback("test");
        slackAttachment.setColor("good");
        slackAttachment.setTitle(String.format("Data %s detected", httpMethod));
//        slackAttachment.setTitle("test");
        slackAttachment.setFields(List.of(
                new SlackField().setTitle("Arguments").setValue(Arrays.toString(proceedingJoinPoint.getArgs())),
                new SlackField().setTitle("method").setValue(proceedingJoinPoint.getSignature().getName())
        ));

        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setAttachments(Collections.singletonList(slackAttachment));
        slackMessage.setIcon(":gear:");
        slackMessage.setText(String.format("%s Request", httpMethod));
//        slackMessage.setText("text");
        slackMessage.setUsername("Method Bot");

        slackApi.call(slackMessage);
    }
}
