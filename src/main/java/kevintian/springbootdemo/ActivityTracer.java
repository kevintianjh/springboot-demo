package kevintian.springbootdemo;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedAsyncClientOption;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsAsyncClient;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.CreateLogStreamRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.InputLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutLogEventsResponse;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ActivityTracer {

    static final CloudWatchLogsClient logsClient = CloudWatchLogsClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                    "AKIA3XMORVFIMBCBYAIO",
                    "Plb5jazl2aITbhZLpX06VVL/ekZuRR1dw1yYAs6T")))
            .overrideConfiguration(ClientOverrideConfiguration.builder().build())
            .endpointOverride(URI.create("https://logs.ap-southeast-1.amazonaws.com"))
            .region(Region.AP_SOUTHEAST_1)
            .build();

    public void shutdown() throws Exception {
        putCWLogEvents("test_group_1", "test_stream_1");
    }

    public void putCWLogEvents(String logGroupName, String streamName) throws Exception {

        int loopCount = 0;

        while(loopCount <= 1) {
            try {
                InputLogEvent inputLogEvent = InputLogEvent.builder()
                        .message("my name is Kevin TIAN!")
                        .timestamp(System.currentTimeMillis())
                        .build();

                PutLogEventsRequest putLogEventsRequest = PutLogEventsRequest.builder()
                        .logEvents(List.of(inputLogEvent))
                        .logGroupName(logGroupName)
                        .logStreamName(streamName)
                        .build();

                logsClient.putLogEvents(putLogEventsRequest);

                break;

            } catch (Exception e) {

                if(loopCount < 1 &&
                        e.getMessage() != null &&
                        e.getMessage().contains("The specified log stream does not exist")) {

                    logsClient.createLogStream(
                            CreateLogStreamRequest.builder()
                                    .logGroupName(logGroupName)
                                    .logStreamName(streamName)
                                    .build());

                    loopCount++;
                }
                else {
                    throw e;
                }
            }
        }


    }

    public static void main(String[] args) throws Exception {
        ActivityTracer activityTracer = new ActivityTracer();
        activityTracer.putCWLogEvents("test_group_1", "test_stream_3");
    }
}